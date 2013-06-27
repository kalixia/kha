package com.kalixia.ha.dao.cassandra

import com.google.common.collect.ImmutableMap
import com.google.common.collect.Maps
import com.kalixia.ha.dao.DevicesDao
import com.kalixia.ha.dao.UsersDao
import com.kalixia.ha.model.User
import com.kalixia.ha.model.devices.Device
import com.kalixia.ha.model.devices.RGBLamp
import com.kalixia.ha.model.sensors.MutableSensor
import com.kalixia.ha.model.sensors.Sensor
import com.netflix.astyanax.Keyspace
import com.netflix.astyanax.MutationBatch
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException
import com.netflix.astyanax.ddl.KeyspaceDefinition
import com.netflix.astyanax.model.Column
import com.netflix.astyanax.model.ColumnFamily
import com.netflix.astyanax.model.ColumnList
import com.netflix.astyanax.model.Equality
import com.netflix.astyanax.serializers.AnnotatedCompositeSerializer
import com.netflix.astyanax.serializers.StringSerializer
import com.netflix.astyanax.serializers.UUIDSerializer
import groovy.util.logging.Slf4j
import org.joda.time.DateTime
import rx.Observable
import rx.Observer

import javax.measure.unit.Unit

import static com.netflix.astyanax.serializers.ComparatorType.UUIDTYPE
/**
 * Devices are stored in a CF named <tt>Devices</tt>.
 * This CF is made of composite columns whose names are defined by {@link SensorProperty}.
 * <p>
 * The device is made of metadata about it (like name, owner, etc.) but also sensors. Those sensors are stored with
 * the device metadata by the use of composite columns in a wide-row arrangement. This storage enables fast retrieval
 * of device data along with its sensors.
 * <p>
 * On top of that, as we usually need to fetch devices of a user, the CF for the users is enriched with device IDs
 * so that lookup can be done efficiently, like a reversed index.
 */
@Slf4j("LOGGER")
public class CassandraDevicesDao extends AbstractCassandraDao<Device, UUID, SensorProperty> implements DevicesDao {
    private final SchemaDefinition schema
    private final UsersDao usersDao
    private static final SensorProperty COL_NAME = new SensorProperty(property: "name")
    private static final SensorProperty COL_OWNER = new SensorProperty(property: "owner")
    private static final SensorProperty COL_CREATION_DATE = new SensorProperty(property: "creationDate")
    private static final SensorProperty COL_LAST_UPDATE_DATE = new SensorProperty(property: "lastUpdateDate")

    public CassandraDevicesDao(SchemaDefinition schema, UsersDao usersDao) {
        this.schema = schema
        this.usersDao = usersDao
    }

    @Override
    public Device findById(UUID id) throws ConnectionException {
        ColumnList<SensorProperty> result = schema.keyspace.prepareQuery(schema.devicesCF)
                .getKey(id)
                .execute().getResult()
        try {
            return buildFromColumnList(id, result)
        } catch (NullPointerException e) {
            throw new IllegalStateException("User should be found for device $id", e)
        }
    }

    @Override
    public Device findByOwnerAndName(String ownerUsername, String name) throws ConnectionException {
        def userColumnSerializer = (AnnotatedCompositeSerializer<UserProperty>) schema.usersCF.columnSerializer
        def startColumn = userColumnSerializer
                .makeEndpoint("device", Equality.EQUAL)
                .append(name, Equality.EQUAL)
                .toBytes()
        def endColumn = userColumnSerializer
                .makeEndpoint("device", Equality.EQUAL)
                .append(name, Equality.LESS_THAN_EQUALS)
                .toBytes()

        ColumnList<UserProperty> devicesNames = schema.keyspace.prepareQuery(schema.usersCF)
                .getKey(ownerUsername)
                .withColumnRange(startColumn, endColumn, false, 1)
                .execute().getResult()
        return findById(devicesNames.iterator().next().getUUIDValue())
    }

    @Override
    public Observable<? extends Device> findAllDevicesOfUser(String username) throws ConnectionException {
        def userColumnSerializer = (AnnotatedCompositeSerializer<UserProperty>) schema.usersCF.columnSerializer
        def startColumn = userColumnSerializer.makeEndpoint("device", Equality.EQUAL).toBytes()
        def endColumn = userColumnSerializer.makeEndpoint("device", Equality.LESS_THAN_EQUALS).toBytes()

        ColumnList<UserProperty> devicesNames = schema.keyspace.prepareQuery(schema.usersCF)
                .getKey(username)
                .withColumnRange(startColumn, endColumn, false, 10000)
                .autoPaginate(true)
                .execute().getResult()
        return Observable.create({ Observer<Device> observer ->
            devicesNames.each { Column<UserProperty> col ->
                Device device = findById(col.getUUIDValue())
                observer.onNext(device)
            }
            observer.onCompleted()
        })
    }

    @Override
    public void save(Device device) throws ConnectionException {
        device.setLastUpdateDate(new DateTime())
        MutationBatch m = schema.keyspace.prepareMutationBatch()
        def row = m.withRow(schema.devicesCF, device.id)
        row
                .putColumn(COL_NAME, device.name)
                .putColumn(COL_OWNER, device.owner.username)
                .putColumn(COL_CREATION_DATE, device.creationDate.toDate())
                .putColumn(COL_LAST_UPDATE_DATE, device.lastUpdateDate.toDate())
        device.sensors.each { Sensor sensor ->
            row
                .putColumn(new SensorProperty(sensor: sensor.name, property: 'name'), sensor.name)
                .putColumn(new SensorProperty(sensor: sensor.name, property: 'unit'), sensor.unit.toString())
        }
        m.withRow(schema.usersCF, device.owner.username)
            .putColumn(new UserProperty(type: "device", property: device.getName()), device.id)
        m.execute()
    }

    @Override
    public void delete(UUID id) {
        MutationBatch m = schema.keyspace.prepareMutationBatch()
        m.withRow(schema.devicesCF, id).delete()
        m.execute()
    }

    @Override
    protected Device buildFromColumnList(UUID id, ColumnList<SensorProperty> result) throws ConnectionException {
        if (result.isEmpty()) {
            return null;
        }

        String deviceName, ownerUsername
        DateTime creationDate, lastUpdateDate
        Map<String, MutableSensor> sensorsMap = Maps.newHashMap()
        result.each { Column<SensorProperty> col ->
            switch (col.name) {
                case COL_NAME:
                    deviceName = col.stringValue
                    break;
                case COL_OWNER:
                    ownerUsername = col.stringValue
                    break;
                case COL_CREATION_DATE:
                    creationDate = new DateTime(col.dateValue)
                    break;
                case COL_LAST_UPDATE_DATE:
                    lastUpdateDate = new DateTime(col.dateValue)
                    break;
                default:
                    def sensorName = col.name.sensor
                    def sensorProperty = col.name.property
                    def sensorPropertyValue = col.stringValue

                    MutableSensor sensor
                    if (sensorsMap.containsKey(sensorName))
                        sensor = sensorsMap.get(sensorName)
                    else
                        sensor = new MutableSensor()

                    switch (sensorProperty) {
                        case 'name':
                            sensor.name = sensorPropertyValue
                            break;
                        case 'unit':
                            sensor.unit = Unit.valueOf(sensorPropertyValue)
                            break;
                        default:
                            LOGGER.error("Found unexpected column (${col.getName().sensor},${col.getName().property})")
                    }
                    sensorsMap.put(sensorName, sensor)
            }
        }

        User owner = usersDao.findByUsername(ownerUsername)
        if (owner == null) {
            LOGGER.error("Invalid data: device {} is linked to owner {} which can't be found!", deviceName, ownerUsername)
        }
        def device = new RGBLamp<>(id, deviceName, owner, creationDate, lastUpdateDate)
        sensorsMap.values().each { Sensor sensor ->
            device.addSensor(sensor)
        }
        return device
    }
}


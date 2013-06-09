package com.kalixia.ha.api.cassandra;

import com.google.common.collect.ImmutableMap;
import com.kalixia.ha.api.DevicesDao;
import com.kalixia.ha.api.UsersDao;
import com.kalixia.ha.model.Device;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.devices.RGBLamp;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.ddl.KeyspaceDefinition;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.serializers.ComparatorType;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.serializers.TimeUUIDSerializer;
import com.netflix.astyanax.util.TimeUUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import java.util.UUID;

public class CassandraDevicesDao<T> implements DevicesDao {
    private final Keyspace keyspace;
    private final ColumnFamily<UUID, String> cfDevices;
//    private final ColumnFamily<UUID, Date> cfSensorsData;
//    private final ColumnFamily<String, Integer> cfSensorsStats;
    private final UsersDao usersDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraDevicesDao.class);

    public CassandraDevicesDao(Keyspace keyspace, UsersDao usersDao) throws ConnectionException {
        this.keyspace = keyspace;
        this.usersDao = usersDao;
        cfDevices = new ColumnFamily<>("Devices", TimeUUIDSerializer.get(), StringSerializer.get());
//        cfSensorsData = new ColumnFamily<>("SensorsData", TimeUUIDSerializer.get(), DateSerializer.get());
//        cfSensorsStats = new ColumnFamily<>("SensorsStats", StringSerializer.get(), IntegerSerializer.get());

        // analyze keyspace schema
        KeyspaceDefinition keyspaceDefinition = CassandraUtils.getOrCreateKeyspace(keyspace);

        // create Sensors CF if missing
        if (keyspaceDefinition.getColumnFamily(cfDevices.getName()) == null) {
            // column family is missing -- create it!
            LOGGER.warn("Creating missing Column Family '{}'", cfDevices.getName());
            keyspace.createColumnFamily(cfDevices, ImmutableMap.<String, Object>builder()
                    .put("default_validation_class", ComparatorType.UTF8TYPE.getTypeName())
                    .put("key_validation_class", ComparatorType.TIMEUUIDTYPE.getTypeName())
                    .put("comparator_type", ComparatorType.UTF8TYPE.getTypeName())
                    .put("column_metadata", ImmutableMap.<String, Object>builder()
                            .put("name", ImmutableMap.<String, Object>builder()
                                    .put("validation_class", ComparatorType.UTF8TYPE.getTypeName())
                                    .put("index_name", "idx_device_name")
                                    .put("index_name", "idx_owner")
                                    .put("index_type", "KEYS")
                                    .build())
                            .build())
                    .build());
        }

        /*
        // création de la CF SensorsData si elle n'existe pas
        if (keyspaceDefinition.getColumnFamily(cfSensorsData.getName()) == null) {
            // column family is missing -- create it!
            keyspace.createColumnFamily(cfSensorsData, ImmutableMap.<String, Object>builder()
                    .put("default_validation_class", ComparatorType.DOUBLETYPE.getTypeName())
                    .put("key_validation_class", ComparatorType.TIMEUUIDTYPE.getTypeName())
                    .put("comparator_type", ComparatorType.DATETYPE.getTypeName())
                    .build());
        }

        // création de la CF SensorsStats si elle n'existe pas
        if (keyspaceDefinition.getColumnFamily(cfSensorsStats.getName()) == null) {
            // column family is missing -- create it!
            keyspace.createColumnFamily(cfSensorsStats, ImmutableMap.<String, Object>builder()
                    .put("default_validation_class", ComparatorType.COUNTERTYPE.getTypeName())
                    .put("key_validation_class", ComparatorType.UTF8TYPE.getTypeName())
                    .put("comparator_type", ComparatorType.INTEGERTYPE.getTypeName())
                    .build());
        }
        */
    }

    @Override
    public Device findById(UUID id) throws ConnectionException {
        ColumnList<String> result = keyspace.prepareQuery(cfDevices)
                .getKey(id)
                .withColumnSlice("name")
                .execute().getResult();
        if (result.isEmpty()) {
            return null;
        }
        String deviceName = result.getStringValue("name", null);
        String ownerUsername = result.getStringValue("owner", null);
        User owner = usersDao.findByUsername(ownerUsername);
        return new RGBLamp(id, deviceName, owner);
    }

    @Override
    public Device findByName(String name) throws ConnectionException {
        Rows<UUID, String> deviceRows = keyspace.prepareQuery(cfDevices)
                .searchWithIndex()
                .setRowLimit(1)
                .addExpression().whereColumn("name").equals().value(name)
                .execute().getResult();
        if (deviceRows.iterator().hasNext()) {
            Row<UUID, String> deviceRow = deviceRows.getRowByIndex(0);
            ColumnList<String> columns = deviceRow.getColumns();
            UUID id = deviceRow.getKey();
            String deviceName = columns.getStringValue("name", null);
            String ownerUsername = columns.getStringValue("owner", null);
            User owner = usersDao.findByUsername(ownerUsername);
            return new RGBLamp(id, deviceName, owner);
        } else {
            return null;        // no device found!
        }
    }

    @Override
    public Observable<? extends Device> findAllDevicesOfUser(User user) {
//        keyspace.prepareQuery(cfDevices)
        return null;
    }

    @Override
    public void save(Device device) throws ConnectionException {
        Device deviceFound = findById(device.getId());
        if (deviceFound == null) {
            MutationBatch m = keyspace.prepareMutationBatch();
            UUID id = TimeUUIDUtils.getUniqueTimeUUIDinMicros();
            m.withRow(cfDevices, id)
                    .putColumn("name", device.getName())
                    .putColumn("owner", device.getOwner().getUsername());
            m.execute();
        }
    }
}

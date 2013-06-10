package com.kalixia.ha.dao.cassandra

import com.google.common.collect.ImmutableMap
import com.kalixia.ha.dao.DevicesDao
import com.kalixia.ha.dao.UsersDao
import com.kalixia.ha.model.devices.Device
import com.kalixia.ha.model.User
import com.kalixia.ha.model.devices.DeviceID
import com.kalixia.ha.model.devices.RGBLamp
import com.netflix.astyanax.Keyspace
import com.netflix.astyanax.MutationBatch
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException
import com.netflix.astyanax.ddl.KeyspaceDefinition
import com.netflix.astyanax.model.ColumnFamily
import com.netflix.astyanax.model.ColumnList
import com.netflix.astyanax.model.Row
import com.netflix.astyanax.model.Rows
import com.netflix.astyanax.serializers.ComparatorType
import com.netflix.astyanax.serializers.StringSerializer
import groovy.util.logging.Slf4j
import rx.Observable

@Slf4j("LOGGER")
public class CassandraDevicesDao implements DevicesDao {
    private final Keyspace keyspace;
    private final ColumnFamily<String, String> cfDevices;
    private final UsersDao usersDao;

    public CassandraDevicesDao(Keyspace keyspace, UsersDao usersDao) throws ConnectionException {
        this.keyspace = keyspace;
        this.usersDao = usersDao;
        cfDevices = new ColumnFamily<>("Devices", StringSerializer.get(), StringSerializer.get());

        // analyze keyspace schema
        KeyspaceDefinition keyspaceDefinition = CassandraUtils.getOrCreateKeyspace(keyspace);

        // create Devices CF if missing
        if (keyspaceDefinition.getColumnFamily(cfDevices.getName()) == null) {
            // column family is missing -- create it!
            LOGGER.warn("Creating missing Column Family '{}'", cfDevices.getName());
            keyspace.createColumnFamily(cfDevices, ImmutableMap.<String, Object>builder()
                    .put("default_validation_class", ComparatorType.UTF8TYPE.getTypeName())
                    .put("key_validation_class", ComparatorType.UTF8TYPE.getTypeName())
                    .put("comparator_type", ComparatorType.UTF8TYPE.getTypeName())
                    .put("column_metadata", ImmutableMap.<String, Object>builder()
                            .put("name", ImmutableMap.<String, Object>builder()
                                    .put("validation_class", ComparatorType.UTF8TYPE.getTypeName())
                                    .put("index_name", "idx_device_name")
                                    .put("index_type", "KEYS")
                                    .build())
                            .put("owner", ImmutableMap.<String, Object>builder()
                                    .put("validation_class", ComparatorType.UTF8TYPE.getTypeName())
                                    .put("index_name", "idx_owner_username")
                                    .put("index_type", "KEYS")
                                    .build())
                            .build())
                    .build());
        }
    }

    @Override
    public Device findById(DeviceID id) throws ConnectionException {
        ColumnList<String> result = keyspace.prepareQuery(cfDevices)
                .getKey(id.getRowKey())
                .execute().getResult();
        if (result.isEmpty()) {
            return null;
        }
        return buildDeviceFromColumnList(result);
    }

    @Override
    public Device findByName(String name) throws ConnectionException {
        Rows<String, String> deviceRows = keyspace.prepareQuery(cfDevices)
                .searchWithIndex()
                .setRowLimit(1)
                .addExpression().whereColumn("name").equals().value(name)
                .execute().getResult();
        if (deviceRows.iterator().hasNext()) {
            return buildDeviceFromColumnList(deviceRows.iterator().next().getColumns());
        } else {
            return null;        // no device found!
        }
    }

    @Override
    public Observable<? extends Device> findAllDevicesOfUser(String username) throws ConnectionException {
        Rows<String, String> rows = keyspace.prepareQuery(cfDevices)
                .searchWithIndex()
                .addExpression().whereColumn("owner").equals().value(username)
                .execute().getResult()

        return Observable.from(rows).map({ Row<String, String> row ->
            buildDeviceFromColumnList(row.getColumns())
        })
    }

    @Override
    public void save(Device device) throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        DeviceID id = new DeviceID(device.getOwner().getUsername(), device.getName());
        m.withRow(cfDevices, id.getRowKey())
                .putColumn("name", device.getName())
                .putColumn("owner", device.getOwner().getUsername());
        m.execute();
    }

    private Device buildDeviceFromColumnList(ColumnList<String> result) throws ConnectionException {
        String deviceName = result.getStringValue("name", null);
        String ownerUsername = result.getStringValue("owner", null);
        User owner = usersDao.findByUsername(ownerUsername);

        if (owner == null) {
            LOGGER.error("Invalid data: device {} is linked to owner {} which can't be found!", deviceName, ownerUsername);
        }
        return new RGBLamp<>(new DeviceID(ownerUsername, deviceName), deviceName, owner);
    }

}


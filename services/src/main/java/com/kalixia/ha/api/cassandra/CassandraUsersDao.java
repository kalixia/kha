package com.kalixia.ha.api.cassandra;

import com.google.common.collect.ImmutableMap;
import com.kalixia.ha.api.UsersDao;
import com.kalixia.ha.model.User;
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
import java.util.UUID;

public class CassandraUsersDao implements UsersDao {
    private final Keyspace keyspace;
    private final ColumnFamily<UUID, String> cfUsers;
    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraUsersDao.class);

    public CassandraUsersDao(Keyspace keyspace) throws ConnectionException {
        this.keyspace = keyspace;
        cfUsers = new ColumnFamily<>("Users", TimeUUIDSerializer.get(), StringSerializer.get());

        // analyze keyspace schema
        KeyspaceDefinition keyspaceDefinition = CassandraUtils.getOrCreateKeyspace(keyspace);

        // create Sensors CF if missing
        if (keyspaceDefinition.getColumnFamily(cfUsers.getName()) == null) {
            // column family is missing -- create it!
            LOGGER.warn("Creating missing Column Family '{}'", cfUsers.getName());
            keyspace.createColumnFamily(cfUsers, ImmutableMap.<String, Object>builder()
                    .put("default_validation_class", ComparatorType.UTF8TYPE.getTypeName())
                    .put("key_validation_class", ComparatorType.TIMEUUIDTYPE.getTypeName())
                    .put("comparator_type", ComparatorType.UTF8TYPE.getTypeName())
                    .put("column_metadata", ImmutableMap.<String, Object>builder()
                            .put("username", ImmutableMap.<String, Object>builder()
                                    .put("validation_class", ComparatorType.UTF8TYPE.getTypeName())
                                    .put("index_name", "idx_username")
                                    .put("index_type", "KEYS")
                                    .build())
                            .build())
                    .build());
        }
    }

    @Override
    public User findById(UUID id) throws ConnectionException {
        ColumnList<String> result = keyspace.prepareQuery(cfUsers)
                .getKey(id)
                .withColumnSlice("username")
                .execute().getResult();
        if (result.isEmpty()) {
            return null;
        }
        String username = result.getStringValue("username", null);
        return new User(id, username);
    }

    @Override
    public User findByUsername(String username) throws ConnectionException {
        Rows<UUID, String> userRows = keyspace.prepareQuery(cfUsers)
                .searchWithIndex()
                .setRowLimit(1)
                .addExpression().whereColumn("username").equals().value(username)
                .execute().getResult();
        if (userRows.iterator().hasNext()) {
            Row<UUID, String> userRow = userRows.getRowByIndex(0);
            UUID id = userRow.getKey();
            return new User(id, username);
        } else {
            return null;        // no device found!
        }
    }

    @Override
    public void save(User user) throws ConnectionException {
        User userFound = findByUsername(user.getUsername());
        if (userFound == null) {
            MutationBatch m = keyspace.prepareMutationBatch();
            UUID id = TimeUUIDUtils.getUniqueTimeUUIDinMicros();
            m.withRow(cfUsers, id)
                    .putColumn("username", user.getUsername());
            m.execute();
        }
    }

}

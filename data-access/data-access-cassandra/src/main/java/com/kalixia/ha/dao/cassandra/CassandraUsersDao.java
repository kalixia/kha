package com.kalixia.ha.dao.cassandra;

import com.google.common.collect.ImmutableMap;
import com.kalixia.ha.dao.UsersDao;
import com.kalixia.ha.model.User;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.ddl.KeyspaceDefinition;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.serializers.ComparatorType;
import com.netflix.astyanax.serializers.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraUsersDao implements UsersDao {
    private final Keyspace keyspace;
    private final ColumnFamily<String, String> cfUsers;
    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraUsersDao.class);

    public CassandraUsersDao(Keyspace keyspace) throws ConnectionException {
        this.keyspace = keyspace;
        cfUsers = new ColumnFamily<>("Users", StringSerializer.get(), StringSerializer.get());

        // analyze keyspace schema
        KeyspaceDefinition keyspaceDefinition = CassandraUtils.getOrCreateKeyspace(keyspace);

        // create Sensors CF if missing
        if (keyspaceDefinition.getColumnFamily(cfUsers.getName()) == null) {
            // column family is missing -- create it!
            LOGGER.warn("Creating missing Column Family '{}'", cfUsers.getName());
            keyspace.createColumnFamily(cfUsers, ImmutableMap.<String, Object>builder()
                    .put("default_validation_class", ComparatorType.UTF8TYPE.getTypeName())
                    .put("key_validation_class", ComparatorType.UTF8TYPE.getTypeName())
                    .put("comparator_type", ComparatorType.UTF8TYPE.getTypeName())
                    .build());
        }
    }

    @Override
    public User findByUsername(String username) throws ConnectionException {
        ColumnList<String> result = keyspace.prepareQuery(cfUsers)
                .getKey(username)
                .execute().getResult();
        if (result.isEmpty()) {
            return null;
        }
        User user = new User(username);
        user.setEmail(result.getStringValue("email", null));
        user.setFirstName(result.getStringValue("firstName", null));
        user.setLastName(result.getStringValue("lastName", null));
        return user;
    }

    @Override
    public void save(User user) throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(cfUsers, user.getUsername())
                .putColumn("email", user.getEmail())
                .putColumn("firstName", user.getFirstName())
                .putColumn("lastName", user.getLastName());
        m.execute();
    }

}

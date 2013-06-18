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
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraUsersDao extends AbstractCassandraDao<User, String> implements UsersDao {
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
                    .put("column_metadata", ImmutableMap.<String, Object>builder()
                        .put("email", ImmutableMap.<String, Object>builder()
                                .put("validation_class", ComparatorType.UTF8TYPE.getTypeName())
                                .build())
                        .put("firstName", ImmutableMap.<String, Object>builder()
                                .put("validation_class", ComparatorType.UTF8TYPE.getTypeName())
                                .build())
                        .put("lastName", ImmutableMap.<String, Object>builder()
                                .put("validation_class", ComparatorType.UTF8TYPE.getTypeName())
                                .build())
                        .put("creationDate", ImmutableMap.<String, Object>builder()
                                .put("validation_class", ComparatorType.DATETYPE.getTypeName())
                                .build())
                        .put("lastUpdateDate", ImmutableMap.<String, Object>builder()
                                .put("validation_class", ComparatorType.DATETYPE.getTypeName())
                                .build())
                        .build())
                    .build());
        }
    }

    @Override
    public User findByUsername(String username) throws ConnectionException {
        ColumnList<String> result = keyspace.prepareQuery(cfUsers)
                .getKey(username)
                .execute().getResult();
        return buildFromColumnList(username, result);
    }

    @Override
    public void save(User user) throws ConnectionException {
        user.setLastUpdateDate(new DateTime());
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(cfUsers, user.getUsername())
                .putColumn("email", user.getEmail())
                .putColumn("firstName", user.getFirstName())
                .putColumn("lastName", user.getLastName())
                .putColumn("creationDate", user.getCreationDate().toDate())
                .putColumn("lastUpdateDate", user.getLastUpdateDate().toDate())
        ;
        m.execute();
    }

    @Override
    protected User buildFromColumnList(String username, ColumnList<String> result) throws ConnectionException {
        if (result.isEmpty()) {
            return null;
        }
        DateTime creationDate = new DateTime(result.getDateValue("creationDate", null));
        DateTime lastUpdateDate = new DateTime(result.getDateValue("lastUpdateDate", null));
        User user = new User(username, creationDate, lastUpdateDate);
        user.setEmail(result.getStringValue("email", null));
        user.setFirstName(result.getStringValue("firstName", null));
        user.setLastName(result.getStringValue("lastName", null));
        return user;
    }
}

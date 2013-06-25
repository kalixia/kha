package com.kalixia.ha.dao.cassandra

import com.google.common.collect.ImmutableMap
import com.google.common.collect.Maps
import com.kalixia.ha.dao.UsersDao
import com.kalixia.ha.model.User
import com.kalixia.ha.model.sensors.MutableSensor
import com.netflix.astyanax.Keyspace
import com.netflix.astyanax.MutationBatch
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException
import com.netflix.astyanax.ddl.KeyspaceDefinition
import com.netflix.astyanax.model.Column
import com.netflix.astyanax.model.ColumnFamily
import com.netflix.astyanax.model.ColumnList
import com.netflix.astyanax.serializers.AnnotatedCompositeSerializer
import com.netflix.astyanax.serializers.StringSerializer
import groovy.util.logging.Slf4j
import org.joda.time.DateTime

import static com.google.common.base.Preconditions.checkNotNull
import static com.netflix.astyanax.serializers.ComparatorType.UTF8TYPE

@Slf4j("LOGGER")
public class CassandraUsersDao extends AbstractCassandraDao<User, String, UserProperty> implements UsersDao {
    private final Keyspace keyspace
    private final ColumnFamily<String, UserProperty> cfUsers
    private static final UserProperty COL_EMAIL = new UserProperty(property: "email")
    private static final UserProperty COL_FIRST_NAME = new UserProperty(property: "firstName")
    private static final UserProperty COL_LAST_NAME = new UserProperty(property: "lastName")
    private static final UserProperty COL_CREATION_DATE = new UserProperty(property: "creationDate")
    private static final UserProperty COL_LAST_UPDATE_DATE = new UserProperty(property: "lastUpdateDate")

    public CassandraUsersDao(Keyspace keyspace) throws ConnectionException {
        this.keyspace = keyspace
        AnnotatedCompositeSerializer userPropertySerializer = new AnnotatedCompositeSerializer(UserProperty.class)
        cfUsers = new ColumnFamily<>("Users", StringSerializer.get(), userPropertySerializer)

        // analyze keyspace schema
        KeyspaceDefinition keyspaceDefinition = CassandraUtils.getOrCreateKeyspace(keyspace);

        // create Sensors CF if missing
        if (keyspaceDefinition.getColumnFamily(cfUsers.getName()) == null) {
            // column family is missing -- create it!
            LOGGER.warn("Creating missing Column Family '{}'", cfUsers.getName());
            keyspace.createColumnFamily(cfUsers, ImmutableMap.<String, Object>builder()
//                    .put("default_validation_class", UTF8TYPE.getTypeName())
                    .put("key_validation_class", UTF8TYPE.getTypeName())
                    .put("comparator_type", "CompositeType(UTF8Type,UTF8Type)")
//                    .put("column_metadata", ImmutableMap.<String, Object>builder()
//                        .put("email", ImmutableMap.<String, Object>builder()
//                                .put("validation_class", UTF8TYPE.getTypeName())
//                                .build())
//                        .put("firstName", ImmutableMap.<String, Object>builder()
//                                .put("validation_class", UTF8TYPE.getTypeName())
//                                .build())
//                        .put("lastName", ImmutableMap.<String, Object>builder()
//                                .put("validation_class", UTF8TYPE.getTypeName())
//                                .build())
//                        .put("creationDate", ImmutableMap.<String, Object>builder()
//                                .put("validation_class", ComparatorType.DATETYPE.getTypeName())
//                                .build())
//                        .put("lastUpdateDate", ImmutableMap.<String, Object>builder()
//                                .put("validation_class", ComparatorType.DATETYPE.getTypeName())
//                                .build())
//                        .build())
                    .build());
        }
    }

    @Override
    public User findByUsername(String username) throws ConnectionException {
        checkNotNull(username, "The username can't be null")
        ColumnList<UserProperty> result = keyspace.prepareQuery(cfUsers)
                .getKey(username)
                .execute().getResult();
        return buildFromColumnList(username, result);
    }

    @Override
    public void save(User user) throws ConnectionException {
        user.setLastUpdateDate(new DateTime())
        MutationBatch m = keyspace.prepareMutationBatch()
        m.withRow(cfUsers, user.username)
                .putColumn(COL_EMAIL, user.email)
                .putColumn(COL_FIRST_NAME, user.firstName)
                .putColumn(COL_LAST_NAME, user.lastName)
                .putColumn(COL_CREATION_DATE, user.creationDate.toDate())
                .putColumn(COL_LAST_UPDATE_DATE, user.lastUpdateDate.toDate())
        m.execute()
    }

    @Override
    protected User buildFromColumnList(String username, ColumnList<UserProperty> result) throws ConnectionException {
        if (result.isEmpty()) {
            return null;
        }

        String email, firstName, lastName
        DateTime creationDate, lastUpdateDate
        Map<String, MutableSensor> sensorsMap = Maps.newHashMap()
        result.each { Column<SensorProperty> col ->
            switch (col.name) {
                case COL_EMAIL:
                    email = col.stringValue
                    break;
                case COL_FIRST_NAME:
                    firstName = col.stringValue
                    break;
                case COL_LAST_NAME:
                    lastName = col.stringValue
                    break;
                case COL_CREATION_DATE:
                    creationDate = new DateTime(col.dateValue)
                    break;
                case COL_LAST_UPDATE_DATE:
                    lastUpdateDate = new DateTime(col.dateValue)
                    break;
                default:
                    def userProperty = col.name
            }
        }
        return new User(username, email, firstName, lastName, creationDate, lastUpdateDate)
    }
}

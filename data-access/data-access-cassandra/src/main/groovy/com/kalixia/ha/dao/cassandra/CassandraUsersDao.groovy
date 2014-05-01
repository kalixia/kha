package com.kalixia.ha.dao.cassandra

import com.kalixia.ha.dao.UsersDao
import com.kalixia.ha.model.User
import com.netflix.astyanax.MutationBatch
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException
import com.netflix.astyanax.model.Column
import com.netflix.astyanax.model.ColumnList
import groovy.util.logging.Slf4j
import org.joda.time.DateTime

import static com.google.common.base.Preconditions.checkNotNull

@Slf4j("LOGGER")
public class CassandraUsersDao extends AbstractCassandraDao<User, String, UserProperty> implements UsersDao {
    private final SchemaDefinition schema
    private static final UserProperty COL_EMAIL = new UserProperty(property: "email")
    private static final UserProperty COL_FIRST_NAME = new UserProperty(property: "firstName")
    private static final UserProperty COL_LAST_NAME = new UserProperty(property: "lastName")
    private static final UserProperty COL_CREATION_DATE = new UserProperty(property: "creationDate")
    private static final UserProperty COL_LAST_UPDATE_DATE = new UserProperty(property: "lastUpdateDate")

    public CassandraUsersDao(SchemaDefinition schema) {
        this.schema = schema
    }

    @Override
    public User findByUsername(String username) throws ConnectionException {
        checkNotNull(username, "The username can't be null")
        ColumnList<UserProperty> result = schema.keyspace.prepareQuery(schema.usersCF)
                .getKey(username)
                .execute().getResult();
        return buildFromColumnList(username, result);
    }

    @Override
    public void save(User user) throws ConnectionException {
        user.setLastUpdateDate(DateTime.now())
        MutationBatch m = schema.keyspace.prepareMutationBatch()
        m.withRow(schema.usersCF, user.username)
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
                    break;
            }
        }
        return new User(username, email, firstName, lastName, creationDate, lastUpdateDate)
    }
}

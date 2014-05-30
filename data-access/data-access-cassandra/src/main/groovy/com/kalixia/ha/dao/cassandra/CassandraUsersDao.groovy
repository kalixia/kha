package com.kalixia.ha.dao.cassandra

import com.kalixia.ha.dao.UsersDao
import com.kalixia.ha.model.Role
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
    private static final UserProperty COL_PASSWORD = new UserProperty(property: "password")
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
        def row = m.withRow(schema.usersCF, user.username)
        row
                .putColumn(COL_PASSWORD, user.password)
                .putColumn(COL_EMAIL, user.email)
                .putColumn(COL_FIRST_NAME, user.firstName)
                .putColumn(COL_LAST_NAME, user.lastName)
                .putColumn(COL_CREATION_DATE, user.creationDate.toDate())
                .putColumn(COL_LAST_UPDATE_DATE, user.lastUpdateDate.toDate())
        user.roles.each { role -> row.putColumn(new UserProperty(type: 'role', property: role.name()), role.name()) }
        m.execute()
    }

    @Override
    rx.Observable<User> findUsers() throws Exception {
        // TODO: implement this!
        return rx.Observable.empty()
    }

    @Override
    Long getUsersCount() throws Exception {
        // TODO: implement this!
        throw new UnsupportedOperationException()
    }

    @Override
    protected User buildFromColumnList(String username, ColumnList<UserProperty> result) throws ConnectionException {
        if (result.isEmpty()) {
            return null;
        }

        String password, email, firstName, lastName
        Set<Role> roles = []
        DateTime creationDate, lastUpdateDate
        result.each { Column<UserProperty> col ->
            switch (col.name) {
                case COL_PASSWORD:
                    password = col.stringValue
                    break
                case COL_EMAIL:
                    email = col.stringValue
                    break
                case COL_FIRST_NAME:
                    firstName = col.stringValue
                    break
                case COL_LAST_NAME:
                    lastName = col.stringValue
                    break
                case COL_CREATION_DATE:
                    creationDate = new DateTime(col.dateValue)
                    break
                case COL_LAST_UPDATE_DATE:
                    lastUpdateDate = new DateTime(col.dateValue)
                    break
                default:
                    switch (col.name.type) {
                        case 'role':
                            roles << Role.valueOf(col.stringValue)
                            break;
                        default:
                            LOGGER.error("Unexpected column {}", col.name);
                            break;
                    }
                    break
            }
        }
        return new User(username, password, email, firstName, lastName, roles, creationDate, lastUpdateDate)
    }
}

package com.kalixia.ha.dao.cassandra;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.kalixia.ha.dao.UsersDao;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.security.OAuthTokens;
import com.kalixia.ha.model.security.Role;
import org.joda.time.DateTime;
import rx.Observable;

import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class CassandraUsersDao implements UsersDao {
    private final Session session;
    private final PreparedStatement psCreateUser;
    private final PreparedStatement psFindUserByUsername;
    private final PreparedStatement psDeleteAccessToken;
    private final PreparedStatement psCreateUserOAuthTokens;
    private final PreparedStatement psFindUsernameByAccessToken;
    private final PreparedStatement psFindOAuthTokensOfUser;
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";
    private static final String COL_EMAIL = "email";
    private static final String COL_FIRST_NAME = "first_name";
    private static final String COL_LAST_NAME = "last_name";
    private static final String COL_ROLES = "roles";
    private static final String COL_CREATION_DATE = "creation_date";
    private static final String COL_LAST_UPDATE_DATE = "last_update_date";
    private static final String COL_OAUTH_ACCESS_TOKEN = "access_token";
    private static final String COL_OAUTH_REFRESH_TOKEN = "refresh_token";

    public CassandraUsersDao(Session session) {
        this.session = session;
        psCreateUser = session.prepare("INSERT INTO kha.users (username, password, email, first_name, last_name, roles, creation_date, last_update_date)" +
                " VALUES(:username, :password, :email, :first_name, :last_name,:roles, :creation_date, :last_update_date)");
        psFindUserByUsername = session.prepare("SELECT * FROM kha.users where username = :username");
        psDeleteAccessToken = session.prepare("DELETE FROM kha.oauth_tokens where access_token = :access_token");
        psCreateUserOAuthTokens = session.prepare("INSERT INTO kha.oauth_tokens (access_token, refresh_token, username)" +
                " VALUES(:access_token, :refresh_token, :username);");
        psFindUsernameByAccessToken = session.prepare("SELECT username FROM kha.oauth_tokens where access_token = :access_token");
        psFindOAuthTokensOfUser = session.prepare("SELECT * FROM kha.oauth_tokens where username = :username");
    }

    @Override
    public Optional<User> findByUsername(String username) {
        BoundStatement boundStatement = new BoundStatement(psFindUserByUsername)
                .setString(COL_USERNAME, username);
        return Observable.from(session.executeAsync(boundStatement))
                .flatMap(result -> Observable.from(result.all()))
                .map(row -> Optional.of(buildUserFromRow(session, row)))
                .defaultIfEmpty(Optional.empty())
                .toBlocking().single();
    }

    @Override
    public Optional<User> findByOAuthAccessToken(String token) {
        BoundStatement boundStatement = new BoundStatement(psFindUsernameByAccessToken)
                .setString("access_token", token);
        return Observable.from(session.executeAsync(boundStatement))
                .flatMap(result -> Observable.from(result.all()))
                .map(row -> row.getString(COL_USERNAME))
                .map(this::findByUsername)
                .defaultIfEmpty(Optional.empty())
                .toBlocking().single();
    }

    @Override
    public Observable<User> findUsers() {
        return Observable.from(session.executeAsync("SELECT * FROM kha.users"))
                .flatMap(result -> Observable.from(result.all()))
                .map(row -> buildUserFromRow(session, row));
    }

    @Override
    public long getUsersCount() {
        return Observable.from(session.executeAsync("SELECT COUNT(*) FROM kha.users"))
                .flatMap(result -> Observable.from(result.all()))
                .map(row -> row.getLong("count"))
                .defaultIfEmpty(null)
                .toBlocking().single();
    }

    @Override
    public void save(User user) {
        // store user profile
        BoundStatement boundStatement = new BoundStatement(psCreateUser)
                .setString(COL_USERNAME, user.getUsername())
                .setString(COL_PASSWORD, user.getPassword())
                .setString(COL_EMAIL, user.getEmail())
                .setString(COL_FIRST_NAME, user.getFirstName())
                .setString(COL_LAST_NAME, user.getLastName())
                .setSet(COL_ROLES, user.getRoles().stream().map(Enum::name).collect(toSet()))
                .setDate(COL_CREATION_DATE, user.getCreationDate().toDate())
                .setDate(COL_LAST_UPDATE_DATE, user.getLastUpdateDate().toDate());
        session.execute(boundStatement);
        // delete old OAuth2 tokens for this user
        boundStatement = new BoundStatement(psFindOAuthTokensOfUser)
                .setString(COL_USERNAME, user.getUsername());
        session.execute(boundStatement).all().stream()
                .map(row -> row.getString(COL_OAUTH_ACCESS_TOKEN))
                .forEach(accessToken -> {
                    BoundStatement deleteStatement = new BoundStatement(psDeleteAccessToken)
                            .setString(COL_OAUTH_ACCESS_TOKEN, accessToken);
                    session.execute(deleteStatement);
                });
        // add OAuth2 tokens
        BatchStatement batchStatement = new BatchStatement(BatchStatement.Type.LOGGED);
        user.getOauthTokens().stream()
                .map(tokens -> new BoundStatement(psCreateUserOAuthTokens)
                        .setString(COL_OAUTH_ACCESS_TOKEN, tokens.getAccessToken())
                        .setString(COL_OAUTH_REFRESH_TOKEN, tokens.getRefreshToken())
                        .setString(COL_USERNAME, user.getUsername()))
                .forEach(batchStatement::add);
        session.execute(batchStatement);
    }

    private User buildUserFromRow(Session session, Row row) {
        // retrieve user's profile
        String username = row.getString(COL_USERNAME);
        String password = row.getString(COL_PASSWORD);
        String email = row.getString(COL_EMAIL);
        String firstName = row.getString(COL_FIRST_NAME);
        String lastName = row.getString(COL_LAST_NAME);
        Set<Role> roles = row.getSet(COL_ROLES, String.class).stream().map(Role::valueOf).collect(toSet());
        DateTime creationDate = new DateTime(row.getDate(COL_CREATION_DATE));
        DateTime lastUpdateDate = new DateTime(row.getDate(COL_LAST_UPDATE_DATE));
        // retrieve user's OAuth2 tokens
        BoundStatement boundStatement = new BoundStatement(psFindOAuthTokensOfUser)
                .setString(COL_USERNAME, username);
        Set<OAuthTokens> oauthTokens = session.execute(boundStatement).all().stream()
                .map(oauthRow -> {
                    String accessToken = oauthRow.getString(COL_OAUTH_ACCESS_TOKEN);
                    String refreshToken = oauthRow.getString(COL_OAUTH_REFRESH_TOKEN);
                    return new OAuthTokens(accessToken, refreshToken);
                })
                .collect(toSet());
        return new User(username, password, email, firstName, lastName, roles, oauthTokens, creationDate, lastUpdateDate);
    }
}

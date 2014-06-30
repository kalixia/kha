package com.kalixia.ha.dao.cassandra;

import com.datastax.driver.core.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Initializes Cassandra schema if needed.
 */
@Singleton
public class SchemaCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaCreator.class);

    @Inject
    public SchemaCreator(Session session) {
        setupSchema(session);
        setupForUsers(session);
        setupForDevices(session);
    }

    public void setupSchema(Session session) {
        session.execute("CREATE KEYSPACE IF NOT EXISTS kha WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};");
    }

    public void setupForUsers(Session session) {
        LOGGER.info("Preparing schema for users data...");
        session.execute("CREATE TABLE IF NOT EXISTS kha.users (" +
                "username text PRIMARY KEY," +
                "password text," +
                "email text," +
                "first_name text," +
                "last_name text," +
                "roles set<text>," +
                "creation_date timestamp," +
                "last_update_date timestamp" +
                ");");
        session.execute("CREATE TABLE IF NOT EXISTS kha.oauth_tokens (" +
                "access_token text PRIMARY KEY," +
                "refresh_token text," +
                "username text" +
                ");");
        session.execute("CREATE INDEX IF NOT EXISTS idx_tokens_by_username ON kha.oauth_tokens(username);");
    }

    public void setupForDevices(Session session) {
        LOGGER.info("Preparing schema for devices data...");
        session.execute("CREATE TABLE IF NOT EXISTS kha.devices (\n" +
                "    id uuid PRIMARY KEY,\n" +
                "    type text,\n" +
                "    owner text,\n" +
                "    name text,\n" +
                "    sensors map<text,text>,\n" +
                "    creation_date timestamp,\n" +
                "    last_update_date timestamp\n" +
                ");");
        session.execute("CREATE INDEX IF NOT EXISTS idx_devices_by_owner ON kha.devices (owner);");
        session.execute("CREATE INDEX IF NOT EXISTS idx_devices_by_name ON kha.devices (name);");
    }
}

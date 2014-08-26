CREATE KEYSPACE IF NOT EXISTS kha WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};


CREATE TABLE IF NOT EXISTS kha.users (
    username text PRIMARY KEY,
    password text,
    email text,
    first_name text,
    last_name text,
    roles set<text>,
    creation_date timestamp,
    last_update_date timestamp
);

CREATE TABLE IF NOT EXISTS kha.oauth_tokens (
    access_token text PRIMARY KEY,
    refresh_token text,
    username text
);
CREATE INDEX IF NOT EXISTS idx_tokens_by_username ON kha.oauth_tokens (username);


CREATE TABLE IF NOT EXISTS kha.devices (
    id uuid PRIMARY KEY,
    owner text,
    name text,
    creation_date timestamp,
    last_update_date timestamp
);
CREATE INDEX IF NOT EXISTS idx_devices_by_owner ON kha.devices (owner);
CREATE INDEX IF NOT EXISTS idx_devices_by_name ON kha.devices (name);

CREATE TABLE IF NOT EXISTS kha.sensors (
    device uuid,
    name text,
    unit text,
    type text,
    PRIMARY KEY(device, name)
);
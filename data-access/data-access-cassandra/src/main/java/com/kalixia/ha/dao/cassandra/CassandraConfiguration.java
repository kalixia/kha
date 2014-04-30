package com.kalixia.ha.dao.cassandra;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kalixia.ha.model.configuration.Configuration;

import java.util.Map;

public class CassandraConfiguration extends Configuration {
    @JsonProperty("keyspace")
    private KeyspaceConfiguration keyspace;

    public KeyspaceConfiguration getKeyspace() {
        return keyspace;
    }
}

class KeyspaceConfiguration extends Configuration {
    @JsonProperty("strategy_class")
    private String strategyClass;

    @JsonProperty("strategy_options")
    private Map<String, String> strategyOptions;

    public String getStrategyClass() {
        return strategyClass;
    }
}
package com.kalixia.ha.api

import com.fasterxml.jackson.annotation.JsonProperty
import com.kalixia.ha.api.environment.Configuration

class CassandraConfiguration extends Configuration {
    def KeyspaceConfiguration keyspace
}

class KeyspaceConfiguration extends Configuration {
    @JsonProperty('strategy_class')
    def String strategyClass

    @JsonProperty('strategy_options')
    def Map<String, String> strategyOptions
}

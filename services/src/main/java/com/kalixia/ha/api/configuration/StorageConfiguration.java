package com.kalixia.ha.api.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kalixia.ha.dao.cassandra.CassandraConfiguration;
import com.kalixia.ha.dao.lucene.LuceneConfiguration;
import com.kalixia.ha.model.configuration.Configuration;

public class StorageConfiguration extends Configuration {
    @JsonProperty("strategy")
    private String strategy;

    @JsonProperty("lucene")
    private LuceneConfiguration lucene;

    @JsonProperty("cassandra")
    private CassandraConfiguration cassandra;

    public String getStrategy() {
        return strategy;
    }

    public LuceneConfiguration getLucene() {
        return lucene;
    }

    public CassandraConfiguration getCassandra() {
        return cassandra;
    }
}

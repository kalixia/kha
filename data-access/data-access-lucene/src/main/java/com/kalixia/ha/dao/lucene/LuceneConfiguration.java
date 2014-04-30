package com.kalixia.ha.dao.lucene;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kalixia.ha.model.configuration.Configuration;

public class LuceneConfiguration extends Configuration {
    @JsonProperty("directory")
    private String directory;

    public String getDirectory() {
        return directory;
    }
}

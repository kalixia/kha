package com.kalixia.ha.dao.lucene;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kalixia.ha.model.configuration.Configuration;

public class LuceneConfiguration extends Configuration {
    @JsonProperty("directory")
    private final String directory;

    public LuceneConfiguration(@JsonProperty("directory") String directory) {
        this.directory = directory;
    }

    public String getDirectory() {
        return directory;
    }
}

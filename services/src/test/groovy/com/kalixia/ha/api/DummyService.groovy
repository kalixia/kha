package com.kalixia.ha.api

import com.kalixia.ha.model.configuration.Configuration

class DummyService extends Service<DummyConfiguration> {
    @Override
    protected String getName() {
        return "dummy"
    }

    @Override
    protected Class<DummyConfiguration> getConfigurationClass() {
        return DummyConfiguration.class
    }
}

class DummyConfiguration extends Configuration {
    def something
    def foo
}

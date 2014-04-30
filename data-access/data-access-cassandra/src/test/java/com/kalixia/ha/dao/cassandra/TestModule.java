package com.kalixia.ha.dao.cassandra;

import dagger.Module;

@Module(injects = DataHolder.class, includes = CassandraTestModule.class)
public class TestModule {
}

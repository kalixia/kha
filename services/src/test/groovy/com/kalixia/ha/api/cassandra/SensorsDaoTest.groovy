package com.kalixia.ha.api.cassandra

import com.kalixia.ha.api.SensorsDao
import dagger.Module
import dagger.ObjectGraph
import dagger.Provides

import javax.inject.Inject

class SensorsDaoTest extends spock.lang.Specification {
//    @Inject SensorsDao dao

    def "test dagger injection"() {
        when:
        ObjectGraph objectGraph = ObjectGraph.create(new TestModule());//.inject(this)
        then:
        objectGraph.get(SensorsDao.class) != null
//        dao != null
    }

    @Module(
            includes = CassandraModule.class,
            entryPoints = [SensorsDaoTest.class, SensorsDao.class],
            overrides = true
    )
    static class TestModule {
//        @Provides @Singleton Heater provideHeater() {
//            return Mockito.mock(Heater.class);
//        }
    }

}

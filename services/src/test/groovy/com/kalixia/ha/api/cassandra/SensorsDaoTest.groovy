package com.kalixia.ha.api.cassandra

import com.kalixia.ha.api.SensorsDao
import com.netflix.astyanax.test.EmbeddedCassandra
import spock.lang.Shared

import javax.inject.Inject

class SensorsDaoTest extends spock.lang.Specification {
    @Inject SensorsDao dao
    @Shared EmbeddedCassandra cassandra

    def setupSpec() {
        cassandra = new EmbeddedCassandra()
        cassandra.start()
    }

    def cleanupSpec() {
        cassandra.stop()
    }

//    def "test dagger injection"() {
//        when:
//        ObjectGraph.create(new TestModule()).inject(this)
//
//        then:
//        dao != null
//    }

//    @Module(
//            includes = CassandraModule.class,
//            injects = [SensorsDaoTest.class, SensorsDao.class],
//            overrides = true
//    )
//    static class TestModule {
//    }

}

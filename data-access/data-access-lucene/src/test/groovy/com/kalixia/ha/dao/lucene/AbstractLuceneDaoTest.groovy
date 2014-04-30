package com.kalixia.ha.dao.lucene

import com.kalixia.ha.dao.UsersDao
import dagger.ObjectGraph
import groovy.util.logging.Slf4j
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.search.IndexSearcher
import spock.lang.Shared
import spock.lang.Specification

/**
 * Abstract class easing tests for Cassandra DAOs.
 */
@Slf4j("LOGGER")
abstract class AbstractLuceneDaoTest extends Specification {
    @Shared UsersDao usersDao
    @Shared IndexWriter indexWriter;

    def setupSpec() {
        ObjectGraph objectGraph = ObjectGraph.create(new TestModule(), new LuceneModule());
        DataHolder holder = objectGraph.get(DataHolder.class)

        indexWriter = holder.indexWriter
        indexWriter.deleteAll()
        usersDao = new LuceneUsersDao(indexWriter, holder.analyzer)
    }

    def cleanupSpec() {
        indexWriter.close(true)
    }

}

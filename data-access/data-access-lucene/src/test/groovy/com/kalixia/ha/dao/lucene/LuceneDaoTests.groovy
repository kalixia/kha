package com.kalixia.ha.dao.lucene

import com.kalixia.ha.dao.DevicesDao
import com.kalixia.ha.dao.UsersDao
import dagger.ObjectGraph
import org.apache.lucene.index.IndexWriter

class LuceneDaoTests {
    public final static UsersDao usersDao
    public final static DevicesDao devicesDao
    public final static IndexWriter indexWriter

    static {
        ObjectGraph objectGraph = ObjectGraph.create(new TestModule());
        DataHolder holder = objectGraph.get(DataHolder.class)

        usersDao = holder.usersDao
        devicesDao = holder.devicesDao
        indexWriter = holder.indexWriter
    }

}

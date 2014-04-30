package com.kalixia.ha.dao.lucene;

import com.kalixia.ha.dao.UsersDao;
import org.apache.lucene.index.IndexWriter;

import javax.inject.Inject;

public class DataHolder {
    private final UsersDao usersDao;
    private final IndexWriter indexWriter;

    @Inject
    public DataHolder(UsersDao usersDao, IndexWriter indexWriter) {
        this.usersDao = usersDao;
        this.indexWriter = indexWriter;
    }

    public UsersDao getUsersDao() {
        return usersDao;
    }

    public IndexWriter getIndexWriter() {
        return indexWriter;
    }
}

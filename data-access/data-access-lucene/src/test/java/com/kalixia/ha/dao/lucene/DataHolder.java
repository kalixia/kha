package com.kalixia.ha.dao.lucene;

import com.kalixia.ha.dao.DevicesDao;
import com.kalixia.ha.dao.UsersDao;
import org.apache.lucene.index.IndexWriter;

import javax.inject.Inject;

public class DataHolder {
    private final UsersDao usersDao;
    private final DevicesDao devicesDao;
    private final IndexWriter indexWriter;

    @Inject
    public DataHolder(UsersDao usersDao, DevicesDao devicesDao, IndexWriter indexWriter) {
        this.usersDao = usersDao;
        this.devicesDao = devicesDao;
        this.indexWriter = indexWriter;
    }

    public UsersDao getUsersDao() {
        return usersDao;
    }

    public DevicesDao getDevicesDao() {
        return devicesDao;
    }

    public IndexWriter getIndexWriter() {
        return indexWriter;
    }
}

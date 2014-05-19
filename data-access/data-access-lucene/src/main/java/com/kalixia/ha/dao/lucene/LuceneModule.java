package com.kalixia.ha.dao.lucene;

import com.kalixia.ha.dao.DevicesDao;
import com.kalixia.ha.dao.SensorsDao;
import com.kalixia.ha.dao.UsersDao;
import com.kalixia.ha.model.sensors.DataPoint;
import dagger.Module;
import dagger.Provides;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static org.apache.lucene.index.IndexWriterConfig.OpenMode;

@Module(library = true, complete = false)
public class LuceneModule {
    public static final Version LUCENE_VERSION = Version.LUCENE_47;
    public static final Logger LOGGER = LoggerFactory.getLogger(LuceneModule.class);

    @Singleton @Provides UsersDao provideUsersDao(IndexWriter indexWriter) {
        return new LuceneUsersDao(indexWriter);
    }

    @Singleton @Provides DevicesDao provideDevicesDao(IndexWriter indexWriter, UsersDao usersDao) {
        return new LuceneDevicesDao(indexWriter, usersDao);
    }

    @Singleton @Provides SensorsDao provideSensorsDao() {
        return new SensorsDao() {
            @Override
            public DataPoint getLastValue(UUID sensorID) {
                return null;
            }

            @Override
            public List<DataPoint> getHistory(DateTime from, DateTime to, Period period) {
                return null;
            }
        };
    }

    @Singleton @Provides IndexWriter provideIndexWriter(File indexDir, Analyzer analyzer) {
        try {
            LOGGER.info("Storing Lucene indexes in '{}'", indexDir.getAbsolutePath());
            Directory fsDir = FSDirectory.open(indexDir);
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(LUCENE_VERSION, analyzer);
            indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
            IndexWriter indexWriter = new IndexWriter(fsDir, indexWriterConfig);
            indexWriter.commit();
            return indexWriter;
        } catch (IOException e) {
            LOGGER.error("Can't initialize Lucene index writer", e);
            System.exit(-1);        // TODO: handle this in a nicer way!!!
            return null;
        }
    }

    @Provides Analyzer provideAnalyzer() {
        return new StandardAnalyzer(LUCENE_VERSION);
    }

    @Singleton @Provides File provideIndexDirectory(LuceneConfiguration config) {
        try {
            Path indexPath = Paths.get(System.getProperty("app.home", ""), config.getDirectory());
            if (Files.isDirectory(indexPath))
                return indexPath.toFile();
            else
                return Files.createDirectory(indexPath).toFile();
        } catch (IOException e) {
            LOGGER.error("Can't create Lucene directory", e);
            System.exit(-1);        // TODO: handle this in a nicer way!!!
            return null;
        }
    }

}

package com.kalixia.ha.dao.lucene;

import dagger.Module;
import dagger.Provides;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.apache.lucene.index.IndexWriterConfig.OpenMode;

@Module(library = true)
public class LuceneModule {
    public static final Version LUCENE_VERSION = Version.LUCENE_47;
    public static final Logger LOGGER = LoggerFactory.getLogger(LuceneModule.class);

    @Singleton @Provides File provideIndexDirectory() {
        try {
            Path indexPath = Files.createTempDirectory("kalixia-ha");
//            String userHome = System.getProperty("user.home");
//            Path indexPath = Paths.get((userHome + "/lucene-test"));
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

    @Singleton @Provides IndexWriter provideIndexWriter(File indexDir, Analyzer analyzer) {
        try {
            LOGGER.info("Storing Lucene indexes in {}", indexDir.toString());
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

}

package com.kalixia.ha.dao.lucene;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(injects = DataHolder.class, includes = LuceneModule.class)
public class TestModule {
    @Singleton @Provides LuceneConfiguration provideLuceneConfiguration() {
        return new LuceneConfiguration("/tmp/kha");
    }
}

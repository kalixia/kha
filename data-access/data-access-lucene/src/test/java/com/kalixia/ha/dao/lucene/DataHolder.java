package com.kalixia.ha.dao.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;

import javax.inject.Inject;

public class DataHolder {
    private final IndexWriter indexWriter;
    private final Analyzer analyzer;

    @Inject
    public DataHolder(IndexWriter indexWriter, Analyzer analyzer) {
        this.indexWriter = indexWriter;
        this.analyzer = analyzer;
    }

    public IndexWriter getIndexWriter() {
        return indexWriter;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

}

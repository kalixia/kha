package com.kalixia.ha.dao.lucene;

import com.kalixia.ha.dao.UsersDao;
import com.kalixia.ha.model.Role;
import com.kalixia.ha.model.User;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.exceptions.Exceptions;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.apache.lucene.document.Field.Store;

/**
 * DAO for {@link User}s based on <a href="http://lucene.apache.org">Lucene</a>.
 */
public class LuceneUsersDao implements UsersDao {
    private final IndexWriter indexWriter;
    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_PASSWORD = "password";
    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_FIRST_NAME = "firstName";
    private static final String FIELD_LAST_NAME = "lastName";
    private static final String FIELD_ROLE = "role";
    private static final String FIELD_CREATION_DATE = "creationDate";
    private static final String FIELD_LAST_UPDATE_DATE = "lastUpdateDate";
    private static final String FIELD_TYPE = "type";
    private static final Term termType = new Term(FIELD_TYPE, "user");
    private static final Logger LOGGER = LoggerFactory.getLogger(LuceneUsersDao.class);

    @Inject
    public LuceneUsersDao(IndexWriter indexWriter) {
        this.indexWriter = indexWriter;
    }

    @Override
    public User findByUsername(String username) throws Exception {
        LOGGER.info("Searching for user '{}' in Lucene indexes", username);

        IndexSearcher indexSearcher = buildIndexSearcher();
        Term term = new Term("username", username);
        BooleanQuery q = new BooleanQuery();
        q.add(new TermQuery(term), BooleanClause.Occur.MUST);
        q.add(new TermQuery(termType), BooleanClause.Occur.MUST);
        TopDocs hits = indexSearcher.search(q, 1);
        if (hits.totalHits == 0) {
            LOGGER.warn("No user found with login '{}'", username);
            return null;            // no result found
        }
        ScoreDoc[] scoreDocs = hits.scoreDocs;
        ScoreDoc scoreDoc = scoreDocs[0];
        Document doc = indexSearcher.doc(scoreDoc.doc);
        return extractUserFromDoc(doc);
    }

    @Override
    public void save(User user) throws Exception {
        LOGGER.info("Saving user '{}' to Lucene indexes", user.getUsername());
        Document doc = new Document();
        doc.add(new StringField(FIELD_USERNAME, user.getUsername(), Store.YES));
        doc.add(new StringField(FIELD_PASSWORD, user.getPassword(), Store.YES));
        doc.add(new StringField(FIELD_EMAIL, user.getEmail(), Store.YES));
        doc.add(new StringField(FIELD_FIRST_NAME, user.getFirstName(), Store.YES));
        doc.add(new StringField(FIELD_LAST_NAME, user.getLastName(), Store.YES));
        user.getRoles().stream().forEach(role -> doc.add(new StringField(FIELD_ROLE, role.name(), Store.YES)));
        doc.add(new StoredField(FIELD_CREATION_DATE, user.getCreationDate().toString()));
        doc.add(new StoredField(FIELD_LAST_UPDATE_DATE, DateTime.now().toString()));
        doc.add(new StringField(FIELD_TYPE, "user", Store.NO));
        Term term = new Term(FIELD_USERNAME, user.getUsername());
        indexWriter.updateDocument(term, doc);
        indexWriter.commit();
    }

    @Override
    public Observable<User> findUsers() throws Exception {
        LOGGER.info("Searching for all users...");

        IndexSearcher indexSearcher = buildIndexSearcher();
        TermQuery q = new TermQuery(termType);
        TopDocs hits = indexSearcher.search(q, Integer.MAX_VALUE);
        if (hits.totalHits == 0) {
            LOGGER.warn("No user found");
            return Observable.empty();
        }
        return Observable.from(hits.scoreDocs)
                .map(scoreDoc -> {
                    try {
                        return indexSearcher.doc(scoreDoc.doc);
                    } catch (IOException e) {
                        throw Exceptions.propagate(e);
                    }
                })
                .map(this::extractUserFromDoc);
    }

    @Override
    public Long getUsersCount() throws Exception {
        IndexSearcher indexSearcher = buildIndexSearcher();
        TermQuery q = new TermQuery(termType);
        TopDocs hits = indexSearcher.search(q, Integer.MAX_VALUE);
        return (long) hits.totalHits;
    }

    private User extractUserFromDoc(Document doc) {
        String username = doc.get(FIELD_USERNAME);
        String password = doc.get(FIELD_PASSWORD);
        String email = doc.get(FIELD_EMAIL);
        String firstName = doc.get(FIELD_FIRST_NAME);
        String lastName = doc.get(FIELD_LAST_NAME);
        Set<Role> roles = Stream.of(doc.getFields(FIELD_ROLE))
                .map(field -> Role.valueOf(field.stringValue()))
                .collect(toSet());
        DateTime creationDate = DateTime.parse(doc.get(FIELD_CREATION_DATE));
        DateTime lastUpdateDate = DateTime.parse(doc.get(FIELD_LAST_UPDATE_DATE));
        return new User(username, password, email, firstName, lastName, roles, creationDate, lastUpdateDate);
    }

    private IndexSearcher buildIndexSearcher() throws IOException {
        return new IndexSearcher(DirectoryReader.open(indexWriter, true));
    }

}

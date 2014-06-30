package com.kalixia.ha.dao.lucene;

import com.kalixia.ha.dao.DevicesDao;
import com.kalixia.ha.dao.UsersDao;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.devices.Device;
import com.kalixia.ha.model.devices.DeviceBuilder;
import com.kalixia.ha.model.sensors.AggregatedSensor;
import com.kalixia.ha.model.sensors.BasicSensor;
import com.kalixia.ha.model.sensors.Sensor;
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
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.apache.lucene.document.Field.Store;

/**
 * DAO for {@link com.kalixia.ha.model.User}s based on <a href="http://lucene.apache.org">Lucene</a>.
 */
public class LuceneDevicesDao implements DevicesDao {
    private final IndexWriter indexWriter;
    private final UsersDao usersDao;
    private static final String FIELD_ID = "id";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_OWNER = "owner";
    private static final String FIELD_DEVICE_TYPE = "device-type";
    private static final String FIELD_CREATION_DATE = "creationDate";
    private static final String FIELD_LAST_UPDATE_DATE = "lastUpdateDate";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_SENSOR_DEVICE_ID = "device";
    private static final String FIELD_SENSOR_NAME = "name";
    private static final String FIELD_SENSOR_UNIT = "unit";
    private static final Term termDeviceType = new Term(FIELD_TYPE, "device");
    private static final Term termSensorType = new Term(FIELD_TYPE, "sensor");
    private static final int MAX_DEVICES_PER_USER = 1000;
    private static final int MAX_SENSORS_PER_DEVICE = 1000;
    private static final Logger LOGGER = LoggerFactory.getLogger(LuceneDevicesDao.class);

    @Inject
    public LuceneDevicesDao(IndexWriter indexWriter, UsersDao usersDao) {
        this.indexWriter = indexWriter;
        this.usersDao = usersDao;
    }

    @Override
    public Device findById(UUID id) {
        LOGGER.info("Searching for device {} in Lucene indexes", id);
        try {
            IndexSearcher indexSearcher = buildIndexSearcher();
            Term term = new Term(FIELD_ID, id.toString());
            BooleanQuery q = new BooleanQuery();
            q.add(new TermQuery(term), BooleanClause.Occur.MUST);
            q.add(new TermQuery(termDeviceType), BooleanClause.Occur.MUST);
            TopDocs hits = indexSearcher.search(q, 1);
            if (hits.totalHits == 0) {
                LOGGER.warn("No device found with ID {}", id);
                return null;            // no result found
            }
            ScoreDoc[] scoreDocs = hits.scoreDocs;
            ScoreDoc scoreDoc = scoreDocs[0];
            Document doc = indexSearcher.doc(scoreDoc.doc);
            return buildDeviceFromDocument(doc, indexSearcher);
        } catch (IOException e) {
            LOGGER.error("Unexpected Lucene error", e);
            return null;
        }
    }

    @Override
    public Device findByOwnerAndName(String ownerUsername, String name) {
        LOGGER.info("Searching for device '{}' of user '{}' in Lucene indexes", name, ownerUsername);
        try {
            IndexSearcher indexSearcher = buildIndexSearcher();
            BooleanQuery q = new BooleanQuery();
            q.add(new TermQuery(new Term(FIELD_NAME, name)), BooleanClause.Occur.MUST);
            q.add(new TermQuery(new Term(FIELD_OWNER, ownerUsername)), BooleanClause.Occur.MUST);
            q.add(new TermQuery(termDeviceType), BooleanClause.Occur.MUST);
            TopDocs hits = indexSearcher.search(q, 1);
            if (hits.totalHits == 0) {
                LOGGER.warn("No device found with name '{}' for user '{}'", name, ownerUsername);
                return null;            // no result found
            }
            ScoreDoc[] scoreDocs = hits.scoreDocs;
            ScoreDoc scoreDoc = scoreDocs[0];
            Document doc = indexSearcher.doc(scoreDoc.doc);
            return buildDeviceFromDocument(doc, indexSearcher);
        } catch (IOException e) {
            LOGGER.error("Unexpected Lucene error", e);
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Observable<? extends Device> findAllDevicesOfUser(String username) {
        LOGGER.info("Searching for all devices of user '{}' in Lucene indexes", username);
        IndexSearcher indexSearcher = buildIndexSearcher();

        return Observable.just(username)
                .flatMap(user -> {
                    Term term = new Term(FIELD_OWNER, user);
                    BooleanQuery q = new BooleanQuery();
                    q.add(new TermQuery(term), BooleanClause.Occur.MUST);
                    q.add(new TermQuery(termDeviceType), BooleanClause.Occur.MUST);
                    try {
                        TopDocs hits = indexSearcher.search(q, MAX_DEVICES_PER_USER);
                        return Observable.from(hits.scoreDocs);
                    } catch (IOException e) {
                        throw Exceptions.propagate(e);
                    }
                })
                .map(scoreDoc -> {
                    try {
                        return indexSearcher.doc(scoreDoc.doc);
                    } catch (IOException e) {
                        throw Exceptions.propagate(e);
                    }
                })
                .map(doc -> buildDeviceFromDocument(doc, indexSearcher));
    }

    @Override
    public void save(Device<?> device) {
        LOGGER.info("Saving device '{}' ({}) to Lucene indexes", device.getName(), device.getId());
        try {
            // store device information
            Document doc = new Document();
            String deviceID = device.getId().toString();
            doc.add(new StringField(FIELD_ID, deviceID, Store.YES));
            doc.add(new StringField(FIELD_NAME, device.getName(), Store.YES));
            doc.add(new StringField(FIELD_OWNER, device.getOwner().getUsername(), Store.YES));
            doc.add(new StringField(FIELD_DEVICE_TYPE, device.getType(), Store.YES));
            doc.add(new StoredField(FIELD_CREATION_DATE, device.getCreationDate().toString()));
            doc.add(new StoredField(FIELD_LAST_UPDATE_DATE, DateTime.now().toString()));
            doc.add(new StringField(FIELD_TYPE, "device", Store.NO));
            Term term = new Term(FIELD_ID, deviceID);
            indexWriter.updateDocument(term, doc);

            // store device's sensors information
            for (Sensor sensor : device.getSensors()) {
                Document sensorDoc = new Document();
                String sensorID = deviceID + '-' + sensor.getName();
                sensorDoc.add(new StringField(FIELD_ID, sensorID, Store.YES));
                sensorDoc.add(new StringField(FIELD_SENSOR_DEVICE_ID, deviceID, Store.NO));
                if (AggregatedSensor.class.isAssignableFrom(sensor.getClass())) {
                    AggregatedSensor<?> aggregatedSensor = (AggregatedSensor) sensor;
                    Set<? extends Sensor<? extends Quantity>> subSensors = aggregatedSensor.getSensors();
                    for (Sensor subSensor : subSensors) {
                        sensorDoc.add(new StringField(FIELD_SENSOR_NAME,
                                aggregatedSensor.getSensorsPrefix() + "/" + subSensor.getName(), Store.YES));
                        sensorDoc.add(new StringField(FIELD_SENSOR_UNIT, subSensor.getUnit().toString(), Store.YES));
                    }
                } else {
                    sensorDoc.add(new StringField(FIELD_SENSOR_NAME, sensor.getName(), Store.YES));
                    sensorDoc.add(new StringField(FIELD_SENSOR_UNIT, sensor.getUnit().toString(), Store.YES));
                }
                sensorDoc.add(new StringField(FIELD_TYPE, "sensor", Store.NO));
                term = new Term(FIELD_ID, sensorID);
                indexWriter.updateDocument(term, sensorDoc);
            }

            indexWriter.commit();
        } catch (IOException e) {
            LOGGER.error("Unexpected Lucene error", e);
        }
    }

    @Override
    public void delete(UUID id) {
        throw new UnsupportedOperationException();
    }

    private Device buildDeviceFromDocument(Document doc, IndexSearcher indexSearcher) {
        UUID id = UUID.fromString(doc.get(FIELD_ID));
        String type = doc.get(FIELD_DEVICE_TYPE);
        String name = doc.get(FIELD_NAME);
        String username = doc.get(FIELD_OWNER);
        User owner = null;
        try {
            owner = usersDao.findByUsername(username);
        } catch (Exception e) {
            throw new RuntimeException("Can't find user " + username, e);
        }
        DateTime creationDate = DateTime.parse(doc.get(FIELD_CREATION_DATE));
        DateTime lastUpdateDate = DateTime.parse(doc.get(FIELD_LAST_UPDATE_DATE));
        // create the appropriate device
        Device device = new DeviceBuilder()
                .ofType(type)
                .withID(id)
                .withName(name)
                .withOwner(owner)
                .withCreationDate(creationDate)
                .withLastUpdateDate(lastUpdateDate)
                .build();
        try {
            Sensor[] sensors = findSensorsOfDevice(id);
            if (sensors != null)
                device.addSensors(sensors);
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
        return device;
    }

    private Sensor[] findSensorsOfDevice(UUID id) throws IOException {
        IndexSearcher indexSearcher = buildIndexSearcher();
        Term term = new Term(FIELD_SENSOR_DEVICE_ID, id.toString());
        BooleanQuery q = new BooleanQuery();
        q.add(new TermQuery(term), BooleanClause.Occur.MUST);
        q.add(new TermQuery(termSensorType), BooleanClause.Occur.MUST);
        TopDocs hits = indexSearcher.search(q, MAX_SENSORS_PER_DEVICE);
        if (hits.totalHits == 0) {
            LOGGER.warn("No sensor found for device '{}'", id);
            return null;            // no result found
        }
        return Stream.of(hits.scoreDocs)
                .map(scoreDoc -> {
                    try {
                        return indexSearcher.doc(scoreDoc.doc);
                    } catch (IOException e) {
                        throw Exceptions.propagate(e);
                    }
                })
                .map(this::buildSensorFromDocument)
                .toArray(Sensor[]::new);
    }

    @SuppressWarnings("unchecked")
    private Sensor buildSensorFromDocument(Document doc) {
        String name = doc.get(FIELD_SENSOR_NAME);
        Unit unit = Unit.valueOf(doc.get(FIELD_SENSOR_UNIT));
        return new BasicSensor(name, unit);                 // TODO: make this handle various sensors types!
    }

    private IndexSearcher buildIndexSearcher() {
        try {
            return new IndexSearcher(DirectoryReader.open(indexWriter, true));
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }
}

package com.kalixia.ha.dao.cassandra;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.kalixia.ha.dao.DevicesDao;
import com.kalixia.ha.dao.UsersDao;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.devices.Device;
import com.kalixia.ha.model.devices.RGBLamp;
import com.kalixia.ha.model.sensors.MutableSensor;
import com.kalixia.ha.model.sensors.Sensor;
import org.joda.time.DateTime;
import rx.Observable;

import javax.measure.unit.Unit;
import java.util.Map;
import java.util.UUID;

public class CassandraDevicesDao implements DevicesDao {
    private final Session session;
    private final UsersDao usersDao;
    private final PreparedStatement psCreateDevice;
    private final PreparedStatement psFindDeviceById;
    private final PreparedStatement psFindDevicesOfUser;
    private final PreparedStatement psFindDeviceByOwnerAndName;
    private static final String COL_ID = "id";
    private static final String COL_OWNER = "owner";
    private static final String COL_NAME = "name";
    private static final String COL_SENSORS = "sensors";
    private static final String COL_CREATION_DATE = "creation_date";
    private static final String COL_LAST_UPDATE_DATE = "last_update_date";

    public CassandraDevicesDao(Session session, UsersDao usersDao) {
        this.session = session;
        this.usersDao = usersDao;
        psCreateDevice = session.prepare("INSERT INTO kha.devices (id, owner, name, sensors, creation_date, last_update_date) " +
                "VALUES (:id, :owner, :name, :sensors, :creation_date, :last_update_date);");
        psFindDeviceById = session.prepare("SELECT * from kha.devices where id = :id");
        psFindDevicesOfUser = session.prepare("SELECT * from kha.devices where owner = :owner");
        psFindDeviceByOwnerAndName = session.prepare("SELECT * from kha.devices where owner = :owner and name = :name" +
                " ALLOW FILTERING");  // TODO: figure out a better way to make such a query!
    }

    @Override
    public Device findById(UUID id) {
        BoundStatement boundStatement = new BoundStatement(psFindDeviceById)
                .setUUID(COL_ID, id);
        return Observable.from(session.executeAsync(boundStatement))
                .flatMap(result -> Observable.from(result.all()))
                .map(this::buildUserFromRow)
                .defaultIfEmpty(null)
                .toBlockingObservable().single();
    }

    @Override
    public Device findByOwnerAndName(String owner, String name) {
        BoundStatement boundStatement = new BoundStatement(psFindDeviceByOwnerAndName)
                .setString(COL_OWNER, owner)
                .setString(COL_NAME, name);
        return Observable.from(session.executeAsync(boundStatement))
                .flatMap(result -> Observable.from(result.all()))
                .map(this::buildUserFromRow)
                .defaultIfEmpty(null)
                .toBlockingObservable().single();
    }

    @Override
    public Observable<? extends Device> findAllDevicesOfUser(String username) {
        BoundStatement boundStatement = new BoundStatement(psFindDevicesOfUser)
                .setString(COL_OWNER, username);
        return Observable.from(session.executeAsync(boundStatement))
                .flatMap(result -> Observable.from(result.all()))
                .map(this::buildUserFromRow);
    }

    @Override
    public void save(Device device) {
        Map<String, String> sensorsData = device.getSensors().stream()
                .map(sensor -> ImmutableMap.<String, String>builder().put(sensor.getName(), sensor.getUnit().toString()).build())
                .collect(
                        Maps::newHashMap,
                        (sensorsMap, sensorMap) -> sensorsMap.putAll(sensorMap),
                        (left, right) -> ImmutableMap.<String, String>builder().putAll(left).putAll(right).build()
                );
        BoundStatement boundStatement = new BoundStatement(psCreateDevice)
                .setUUID(COL_ID, device.getId())
                .setString(COL_OWNER, device.getOwner().getUsername())
                .setString(COL_NAME, device.getName())
                .setMap(COL_SENSORS, sensorsData)
                .setDate(COL_CREATION_DATE, device.getCreationDate().toDate())
                .setDate(COL_LAST_UPDATE_DATE, device.getLastUpdateDate().toDate());
        session.execute(boundStatement);
    }

    @Override
    public void delete(UUID id) {
        throw new UnsupportedOperationException();  // TODO
    }

    @SuppressWarnings("unchecked")
    private Device buildUserFromRow(Row row) {
        UUID id = row.getUUID(COL_ID);
        User owner = usersDao.findByUsername(row.getString(COL_OWNER));
        String name = row.getString(COL_NAME);
        Sensor[] sensors = row.getMap(COL_SENSORS, String.class, String.class).entrySet().stream()
                .map(entry -> {
                    MutableSensor sensor = new MutableSensor();
                    sensor.setName(entry.getKey());
                    sensor.setUnit(Unit.valueOf(entry.getValue()));
                    return sensor;
                })
                .toArray(Sensor[]::new);
        DateTime creationDate = new DateTime(row.getDate(COL_CREATION_DATE));
        DateTime lastUpdateDate = new DateTime(row.getDate(COL_LAST_UPDATE_DATE));
        RGBLamp device = new RGBLamp(id, name, owner, creationDate, lastUpdateDate);
        device.addSensors(sensors);
        return device;
    }

}

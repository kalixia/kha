package com.kalixia.ha.dao.cassandra;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.kalixia.ha.dao.DevicesDao;
import com.kalixia.ha.dao.UsersDao;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.devices.Device;
import com.kalixia.ha.model.devices.DeviceBuilder;
import com.kalixia.ha.model.sensors.Sensor;
import com.kalixia.ha.model.sensors.SensorBuilder;
import org.joda.time.DateTime;
import rx.Observable;

import javax.measure.unit.Unit;
import java.util.List;
import java.util.UUID;

public class CassandraDevicesDao implements DevicesDao {
    private final Session session;
    private final UsersDao usersDao;
    private final PreparedStatement psCreateDevice;
    private final PreparedStatement psCreateSensor;
    private final PreparedStatement psFindDeviceById;
    private final PreparedStatement psFindDevicesOfUser;
    private final PreparedStatement psFindDeviceByOwnerAndName;
    private final PreparedStatement psFindSensorsByDevice;
    private static final String COL_ID = "id";
    private static final String COL_OWNER = "owner";
    private static final String COL_NAME = "name";
    private static final String COL_TYPE = "type";
    private static final String COL_CREATION_DATE = "creation_date";
    private static final String COL_LAST_UPDATE_DATE = "last_update_date";
    private static final String COL_SENSOR_DEVICE = "device";
    private static final String COL_SENSOR_NAME = "name";
    private static final String COL_SENSOR_UNIT = "unit";
    private static final String COL_SENSOR_TYPE = "type";

    public CassandraDevicesDao(Session session, UsersDao usersDao) {
        this.session = session;
        this.usersDao = usersDao;
        psCreateDevice = session.prepare("INSERT INTO kha.devices (id, type, owner, name, creation_date, last_update_date) " +
                "VALUES (:id, :type, :owner, :name, :creation_date, :last_update_date);");
        psCreateSensor = session.prepare("INSERT INTO kha.sensors (device, name, unit, type) " +
                "VALUES (:device, :name, :unit, :type);");
        psFindDeviceById = session.prepare("SELECT * from kha.devices where id = :id");
        psFindDevicesOfUser = session.prepare("SELECT * from kha.devices where owner = :owner");
        psFindDeviceByOwnerAndName = session.prepare("SELECT * from kha.devices where owner = :owner and name = :name" +
                " ALLOW FILTERING");  // TODO: figure out a better way to make such a query!
        psFindSensorsByDevice = session.prepare("SELECT * from kha.sensors where device = :device");
    }

    @Override
    public Device findById(UUID id) {
        BoundStatement boundStatement = new BoundStatement(psFindDeviceById)
                .setUUID(COL_ID, id);
        return Observable.from(session.executeAsync(boundStatement))
                .flatMap(result -> Observable.from(result.all()))
                .map(this::buildDeviceFromRow)
                .defaultIfEmpty(null)
                .toBlocking().single();
    }

    @Override
    public Device findByOwnerAndName(String owner, String name) {
        BoundStatement boundStatement = new BoundStatement(psFindDeviceByOwnerAndName)
                .setString(COL_OWNER, owner)
                .setString(COL_NAME, name);
        return Observable.from(session.executeAsync(boundStatement))
                .flatMap(result -> Observable.from(result.all()))
                .map(this::buildDeviceFromRow)
                .defaultIfEmpty(null)
                .toBlocking().single();
    }

    @Override
    public Observable<? extends Device> findAllDevicesOfUser(String username) {
        BoundStatement boundStatement = new BoundStatement(psFindDevicesOfUser)
                .setString(COL_OWNER, username);
        return Observable.from(session.executeAsync(boundStatement))
                .flatMap(result -> Observable.from(result.all()))
                .map(this::buildDeviceFromRow);
    }

    @Override
    public void save(Device<?> device) {
        BoundStatement boundStatement = new BoundStatement(psCreateDevice)
                .setUUID(COL_ID, device.getId())
                .setString(COL_OWNER, device.getOwner().getUsername())
                .setString(COL_NAME, device.getName())
                .setString(COL_TYPE, device.getType())
                .setDate(COL_CREATION_DATE, device.getCreationDate().toDate())
                .setDate(COL_LAST_UPDATE_DATE, device.getLastUpdateDate().toDate());
        session.execute(boundStatement);
        // TODO: remove old sensors!!
        device.getSensors().stream().forEach(sensor -> save(device, sensor));
    }

    private void save(Device<?> device, Sensor<?> sensor) {
        BoundStatement boundStatement = new BoundStatement(psCreateSensor)
                .setUUID(COL_SENSOR_DEVICE, device.getId())
                .setString(COL_SENSOR_NAME, sensor.getName())
                .setString(COL_SENSOR_UNIT, sensor.getUnit().toString())
                .setString(COL_SENSOR_TYPE, sensor.getType());
        session.execute(boundStatement);
    }

    @Override
    public void delete(UUID id) {
        throw new UnsupportedOperationException();  // TODO
    }

    @SuppressWarnings("unchecked")
    private Device buildDeviceFromRow(Row row) {
        UUID id = row.getUUID(COL_ID);
        User owner = usersDao.findByUsername(row.getString(COL_OWNER));
        String name = row.getString(COL_NAME);
        String type = row.getString(COL_TYPE);
        DateTime creationDate = new DateTime(row.getDate(COL_CREATION_DATE));
        DateTime lastUpdateDate = new DateTime(row.getDate(COL_LAST_UPDATE_DATE));
        // create the appropriate device
        Device device = new DeviceBuilder()
                .ofType(type)
                .withID(id)
                .withName(name)
                .withOwner(owner)
                .withCreationDate(creationDate)
                .withLastUpdateDate(lastUpdateDate)
                .build();
        device.addSensors(findSensorsOfDevice(device));
        return device;
    }

    private Sensor[] findSensorsOfDevice(Device<?> device) {
        BoundStatement boundStatement = new BoundStatement(psFindSensorsByDevice)
                .setUUID(COL_SENSOR_DEVICE, device.getId());
        List<? extends Sensor<?>> sensors = Observable.from(session.executeAsync(boundStatement))
                .flatMap(result -> Observable.from(result.all()))
                .map(row -> buildSensorFromRow(device, row))
                .toList()
                .toBlocking().single();
        return sensors.toArray(new Sensor[sensors.size()]);
    }

    @SuppressWarnings("unchecked")
    private Sensor<?> buildSensorFromRow(Device<?> device, Row row) {
        return new SensorBuilder()
                .forDevice(device)
                .ofType(row.getString(COL_SENSOR_TYPE))
                .withName(row.getString(COL_SENSOR_NAME))
                .withUnit(Unit.valueOf(row.getString(COL_SENSOR_UNIT)))
                .build();
    }

}

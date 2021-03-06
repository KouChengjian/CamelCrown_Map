package com.camel.redpenguin.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.camel.redpenguin.greendao.CamelDevice;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table CAMEL_DEVICE.
*/
public class CamelDeviceDao extends AbstractDao<CamelDevice, Long> {

    public static final String TABLENAME = "CAMEL_DEVICE";

    /**
     * Properties of entity CamelDevice.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property DeviceNike = new Property(1, String.class, "deviceNike", false, "DEVICE_NIKE");
        public final static Property DeviceAvatar = new Property(2, String.class, "deviceAvatar", false, "DEVICE_AVATAR");
        public final static Property DeviceLatitude = new Property(3, String.class, "deviceLatitude", false, "DEVICE_LATITUDE");
        public final static Property DeviceLongitude = new Property(4, String.class, "deviceLongitude", false, "DEVICE_LONGITUDE");
        public final static Property DeviceFrequency = new Property(5, String.class, "deviceFrequency", false, "DEVICE_FREQUENCY");
        public final static Property DeviceLocationMode = new Property(6, String.class, "deviceLocationMode", false, "DEVICE_LOCATION_MODE");
        public final static Property DeviceIdentify = new Property(7, String.class, "deviceIdentify", false, "DEVICE_IDENTIFY");
        public final static Property DeviceSelect = new Property(8, String.class, "deviceSelect", false, "DEVICE_SELECT");
        public final static Property DeviceLigature = new Property(9, String.class, "deviceLigature", false, "DEVICE_LIGATURE");
        public final static Property DeviceUpdateDate = new Property(10, String.class, "deviceUpdateDate", false, "DEVICE_UPDATE_DATE");
        public final static Property DeviceCreateDate = new Property(11, String.class, "deviceCreateDate", false, "DEVICE_CREATE_DATE");
    };


    public CamelDeviceDao(DaoConfig config) {
        super(config);
    }
    
    public CamelDeviceDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'CAMEL_DEVICE' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'DEVICE_NIKE' TEXT," + // 1: deviceNike
                "'DEVICE_AVATAR' TEXT," + // 2: deviceAvatar
                "'DEVICE_LATITUDE' TEXT," + // 3: deviceLatitude
                "'DEVICE_LONGITUDE' TEXT," + // 4: deviceLongitude
                "'DEVICE_FREQUENCY' TEXT," + // 5: deviceFrequency
                "'DEVICE_LOCATION_MODE' TEXT," + // 6: deviceLocationMode
                "'DEVICE_IDENTIFY' TEXT," + // 7: deviceIdentify
                "'DEVICE_SELECT' TEXT," + // 8: deviceSelect
                "'DEVICE_LIGATURE' TEXT," + // 9: deviceLigature
                "'DEVICE_UPDATE_DATE' TEXT," + // 10: deviceUpdateDate
                "'DEVICE_CREATE_DATE' TEXT);"); // 11: deviceCreateDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'CAMEL_DEVICE'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, CamelDevice entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String deviceNike = entity.getDeviceNike();
        if (deviceNike != null) {
            stmt.bindString(2, deviceNike);
        }
 
        String deviceAvatar = entity.getDeviceAvatar();
        if (deviceAvatar != null) {
            stmt.bindString(3, deviceAvatar);
        }
 
        String deviceLatitude = entity.getDeviceLatitude();
        if (deviceLatitude != null) {
            stmt.bindString(4, deviceLatitude);
        }
 
        String deviceLongitude = entity.getDeviceLongitude();
        if (deviceLongitude != null) {
            stmt.bindString(5, deviceLongitude);
        }
 
        String deviceFrequency = entity.getDeviceFrequency();
        if (deviceFrequency != null) {
            stmt.bindString(6, deviceFrequency);
        }
 
        String deviceLocationMode = entity.getDeviceLocationMode();
        if (deviceLocationMode != null) {
            stmt.bindString(7, deviceLocationMode);
        }
 
        String deviceIdentify = entity.getDeviceIdentify();
        if (deviceIdentify != null) {
            stmt.bindString(8, deviceIdentify);
        }
 
        String deviceSelect = entity.getDeviceSelect();
        if (deviceSelect != null) {
            stmt.bindString(9, deviceSelect);
        }
 
        String deviceLigature = entity.getDeviceLigature();
        if (deviceLigature != null) {
            stmt.bindString(10, deviceLigature);
        }
 
        String deviceUpdateDate = entity.getDeviceUpdateDate();
        if (deviceUpdateDate != null) {
            stmt.bindString(11, deviceUpdateDate);
        }
 
        String deviceCreateDate = entity.getDeviceCreateDate();
        if (deviceCreateDate != null) {
            stmt.bindString(12, deviceCreateDate);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public CamelDevice readEntity(Cursor cursor, int offset) {
        CamelDevice entity = new CamelDevice( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // deviceNike
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // deviceAvatar
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // deviceLatitude
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // deviceLongitude
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // deviceFrequency
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // deviceLocationMode
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // deviceIdentify
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // deviceSelect
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // deviceLigature
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // deviceUpdateDate
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11) // deviceCreateDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, CamelDevice entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDeviceNike(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDeviceAvatar(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDeviceLatitude(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDeviceLongitude(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setDeviceFrequency(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setDeviceLocationMode(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setDeviceIdentify(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setDeviceSelect(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setDeviceLigature(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setDeviceUpdateDate(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setDeviceCreateDate(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(CamelDevice entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(CamelDevice entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}

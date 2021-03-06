package com.camel.redpenguin.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.camel.redpenguin.greendao.DataRelFamily;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table DATA_REL_FAMILY.
*/
public class DataRelFamilyDao extends AbstractDao<DataRelFamily, Long> {

    public static final String TABLENAME = "DATA_REL_FAMILY";

    /**
     * Properties of entity DataRelFamily.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property DataRelSubject = new Property(1, String.class, "dataRelSubject", false, "DATA_REL_SUBJECT");
        public final static Property DataRelBranch = new Property(2, String.class, "dataRelBranch", false, "DATA_REL_BRANCH");
        public final static Property DataRelNick = new Property(3, String.class, "dataRelNick", false, "DATA_REL_NICK");
        public final static Property DataRelAccount = new Property(4, String.class, "dataRelAccount", false, "DATA_REL_ACCOUNT");
        public final static Property DataRelAdministrator = new Property(5, String.class, "dataRelAdministrator", false, "DATA_REL_ADMINISTRATOR");
    };


    public DataRelFamilyDao(DaoConfig config) {
        super(config);
    }
    
    public DataRelFamilyDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'DATA_REL_FAMILY' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'DATA_REL_SUBJECT' TEXT," + // 1: dataRelSubject
                "'DATA_REL_BRANCH' TEXT," + // 2: dataRelBranch
                "'DATA_REL_NICK' TEXT," + // 3: dataRelNick
                "'DATA_REL_ACCOUNT' TEXT," + // 4: dataRelAccount
                "'DATA_REL_ADMINISTRATOR' TEXT);"); // 5: dataRelAdministrator
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'DATA_REL_FAMILY'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, DataRelFamily entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String dataRelSubject = entity.getDataRelSubject();
        if (dataRelSubject != null) {
            stmt.bindString(2, dataRelSubject);
        }
 
        String dataRelBranch = entity.getDataRelBranch();
        if (dataRelBranch != null) {
            stmt.bindString(3, dataRelBranch);
        }
 
        String dataRelNick = entity.getDataRelNick();
        if (dataRelNick != null) {
            stmt.bindString(4, dataRelNick);
        }
 
        String dataRelAccount = entity.getDataRelAccount();
        if (dataRelAccount != null) {
            stmt.bindString(5, dataRelAccount);
        }
 
        String dataRelAdministrator = entity.getDataRelAdministrator();
        if (dataRelAdministrator != null) {
            stmt.bindString(6, dataRelAdministrator);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public DataRelFamily readEntity(Cursor cursor, int offset) {
        DataRelFamily entity = new DataRelFamily( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // dataRelSubject
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // dataRelBranch
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // dataRelNick
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // dataRelAccount
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // dataRelAdministrator
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, DataRelFamily entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDataRelSubject(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDataRelBranch(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDataRelNick(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDataRelAccount(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setDataRelAdministrator(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(DataRelFamily entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(DataRelFamily entity) {
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

package com.camel.redpenguin.bean;

import java.util.List;

import com.camel.redpenguin.CustomApplcation;
import com.camel.redpenguin.greendao.CamelDevice;
import com.camel.redpenguin.greendao.CamelDeviceDao;
import com.camel.redpenguin.greendao.CamelDeviceHistory;
import com.camel.redpenguin.greendao.CamelDeviceHistoryDao;
import com.camel.redpenguin.greendao.CamelDevicePush;
import com.camel.redpenguin.greendao.CamelDevicePushDao;
import com.camel.redpenguin.greendao.CamelUser;
import com.camel.redpenguin.greendao.CamelUserDao;
import com.camel.redpenguin.greendao.DaoSession;
import com.camel.redpenguin.greendao.DataRelFamily;
import com.camel.redpenguin.greendao.DataRelFamilyDao;
import com.camel.redpenguin.greendao.RelSafetyZone;
import com.camel.redpenguin.greendao.RelSafetyZoneDao;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;
import android.content.Context;


public class DBHelper {
	
	private static Context mContext;
    private static DBHelper instance;
    private static DaoSession daoSession;
    
    private DBHelper() {
    }
    
    /**
     *  初始化
     */
    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper();
            if (mContext == null) {
                mContext = context;
            }
            // 数据库对象
            daoSession = CustomApplcation.getDaoSession();
        }
        return instance;
    }


    /** User-添加数据 */
    public long addToUsersInfoTable(CamelUser item) {
        return daoSession.getCamelUserDao().insert(item);
    }

    /** User-更新数据 */
    public long updateToUsersInfoTable(CamelUser item){
    	return daoSession.getCamelUserDao().insertOrReplace(item);
    	//daoSession.getCamelUserDao().insertInTx(item);
    }
    
    /** User-查询表所有数据 */
    public List<CamelUser> getUsersInfoList() {
        QueryBuilder<CamelUser> qb = daoSession.getCamelUserDao().queryBuilder();
        return qb.list();
    }
    
    /** User-查询表当前用户信息 */
    public List<CamelUser> getCurrentUsers(String name) {
    	QueryBuilder<CamelUser> qb = daoSession.getCamelUserDao().queryBuilder();
        qb.where(CamelUserDao.Properties.UserAccount.eq(name));
        if (qb.list().size() > 0) {
            return qb.list();
        } else {
            return null;
        }
    }

    /** User-查询表所有数据  */
    public List<CamelUser> getUsersInfo() {
        return daoSession.getCamelUserDao().loadAll();
    }
    
    /** User-查询是否保存 */
    public boolean isUsersInfoSaved(int Id) {
        QueryBuilder<CamelUser> qb = daoSession.getCamelUserDao().queryBuilder();
        qb.where(CamelUserDao.Properties.Id.eq(Id));
        qb.buildCount().count();
        return qb.buildCount().count() > 0 ? true : false;
    }
    
    public boolean isUsersInfoSaved(String UserAccount) {
        QueryBuilder<CamelUser> qb = daoSession.getCamelUserDao().queryBuilder();
        qb.where(CamelUserDao.Properties.UserAccount.eq(UserAccount));
        qb.buildCount().count();
        return qb.buildCount().count() > 0 ? true : false;
    }

    /** User-删除 */
    public void deleteUsersInfoList(long Id) {
        QueryBuilder<CamelUser> qb = daoSession.getCamelUserDao().queryBuilder();
        DeleteQuery<CamelUser> bd = qb.where(CamelUserDao.Properties.Id.eq(Id)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }

    /** User-删除 全部*/
    public void clearUsersInfo() {
        daoSession.getCamelUserDao().deleteAll();
    }
    
    /** User-通过姓名查找其更新时间 */
    public String getUpdateDateByName(String name) {
        QueryBuilder<CamelUser> qb = daoSession.getCamelUserDao().queryBuilder();
        qb.where(CamelUserDao.Properties.UserAccount.eq(name));
        if (qb.list().size() > 0) {
            return qb.list().get(0).getUserUpdateDate();
        } else {
            return "";
        }
    }
    
    /** User-多重查询 */
    public List<CamelUser> getIphRegionList(String name, String sex) {
        QueryBuilder<CamelUser> qb = daoSession.getCamelUserDao().queryBuilder();
        qb.where(qb.and(CamelUserDao.Properties.UserAccount.eq(name), CamelUserDao.Properties.UserPassword.eq(sex)));
        qb.orderAsc(CamelUserDao.Properties.Id);// 排序依据
        return qb.list();
    }
    
    
    
    
    /** Device-添加数据 */
    public long addToDevicesInfoTable(CamelDevice item) {
        return daoSession.getCamelDeviceDao().insert(item);
    }
    /** Device-更新数据 */
    public long updateToDevicesInfoTable(CamelDevice item){
    	return daoSession.getCamelDeviceDao().insertOrReplace(item);
    }
    /** Device-查询表所有数据 */
    public List<CamelDevice> getDevicesInfoList() {
        QueryBuilder<CamelDevice> qb = daoSession.getCamelDeviceDao().queryBuilder();
        return qb.list();
    }
    /** Device-查询表当前用户信息 */
    public List<CamelDevice> getAssignDevices(String DeviceIdentify) {
    	QueryBuilder<CamelDevice> qb = daoSession.getCamelDeviceDao().queryBuilder();
        qb.where(CamelDeviceDao.Properties.DeviceIdentify.eq(DeviceIdentify));
        if (qb.list().size() > 0) {
            return qb.list();
        } else {
            return null;
        }
    }
    /** Device-查询是否保存 */
    public boolean isDevicesInfoSaved(String DeviceIdentify) {
        QueryBuilder<CamelDevice> qb = daoSession.getCamelDeviceDao().queryBuilder();
        qb.where(CamelDeviceDao.Properties.DeviceIdentify.eq(DeviceIdentify));
        qb.buildCount().count();
        return qb.buildCount().count() > 0 ? true : false;
    }
    /** Device-删除 全部*/
    public void clearDevicesInfo() {
        daoSession.getCamelDeviceDao().deleteAll();
    }
    /** Device-删除 */
    public void deleteDevicesInfoList(String DeviceIdentify) {
        QueryBuilder<CamelDevice> qb = daoSession.getCamelDeviceDao().queryBuilder();
        DeleteQuery<CamelDevice> bd = qb.where(CamelDeviceDao.Properties.DeviceIdentify.eq(DeviceIdentify)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }
    
    
    
    
    /** DevicePush-添加数据 */
    public long addToDevicePushInfoTable(CamelDevicePush item) {
        return daoSession.getCamelDevicePushDao().insert(item);
    }
    
    /** DevicePush-查询表当前用户信息 */
    public List<CamelDevicePush> getDevicePushMessage(String deviceIdentify) {
    	QueryBuilder<CamelDevicePush> qb = daoSession.getCamelDevicePushDao().queryBuilder();
        qb.where(CamelDevicePushDao.Properties.DevicePushIdentify.eq(deviceIdentify));
        if (qb.list().size() > 0) {
            return qb.list();
        } else {
            return null;
        }
    }
    /** DevicePush-多重查询 */
    public List<CamelDevicePush> getDevicePushMessage(String deviceIdentify, String time) {
        QueryBuilder<CamelDevicePush> qb = daoSession.getCamelDevicePushDao().queryBuilder();
        qb.where(qb.and(CamelDevicePushDao.Properties.DevicePushIdentify.eq(deviceIdentify), CamelDevicePushDao.Properties.DevicePushUpdated.eq(time)));
        qb.orderAsc(CamelDevicePushDao.Properties.Id);// 排序依据
        return qb.list();
    }
    /** DevicePush-查询是否保存 */
    public boolean isCamelDevicePushSaved(String DeviceIdentify) {
        QueryBuilder<CamelDevicePush> qb = daoSession.getCamelDevicePushDao().queryBuilder();
        qb.where(CamelDevicePushDao.Properties.DevicePushIdentify.eq(DeviceIdentify));
        qb.buildCount().count();
        return qb.buildCount().count() > 0 ? true : false;
    }
    
    
    
    
    
    /** DeviceHistory-添加数据 */
    public long addToDeviceHistoryInfoTable(CamelDeviceHistory item) {
        return daoSession.getCamelDeviceHistoryDao().insert(item);
    }
    
    /** DeviceHistory-查询表当前用户信息 */
    public List<CamelDeviceHistory> getDeviceHistoryMessage(String deviceIdentify) {
    	QueryBuilder<CamelDeviceHistory> qb = daoSession.getCamelDeviceHistoryDao().queryBuilder();
        qb.where(CamelDeviceHistoryDao.Properties.DeviceHistoryIdentify.eq(deviceIdentify));
        if (qb.list().size() > 0) {
            return qb.list();
        } else {
            return null;
        }
    }
    /** DeviceHistory-多重查询 */
    public List<CamelDeviceHistory> getDeviceHistoryMessage(String deviceIdentify, String time) {
        QueryBuilder<CamelDeviceHistory> qb = daoSession.getCamelDeviceHistoryDao().queryBuilder();
        qb.where(qb.and(CamelDeviceHistoryDao.Properties.DeviceHistoryIdentify.eq(deviceIdentify),  CamelDeviceHistoryDao.Properties.DeviceHistoryCreated.eq(time)));
        qb.orderAsc(CamelDeviceHistoryDao.Properties.Id);// 排序依据
        return qb.list();
    }
    /** DeviceHistory-多重查询 */
    public List<CamelDeviceHistory> getDeviceHistoryMessage(String deviceIdentify, String page ,String time) {
        QueryBuilder<CamelDeviceHistory> qb = daoSession.getCamelDeviceHistoryDao().queryBuilder();
        qb.where(qb.and(CamelDeviceHistoryDao.Properties.DeviceHistoryIdentify.eq(deviceIdentify), CamelDeviceHistoryDao.Properties.DeviceHistoryPage.eq(page) , CamelDeviceHistoryDao.Properties.DeviceHistoryCreated.eq(time)));
        qb.orderAsc(CamelDeviceHistoryDao.Properties.Id);// 排序依据
        return qb.list();
    }
    /** DeviceHistory-查询是否保存 */
    public boolean isDeviceHistorySaved(String DeviceIdentify) {
        QueryBuilder<CamelDeviceHistory> qb = daoSession.getCamelDeviceHistoryDao().queryBuilder();
        qb.where(CamelDeviceHistoryDao.Properties.DeviceHistoryIdentify.eq(DeviceIdentify));
        qb.buildCount().count();
        return qb.buildCount().count() > 0 ? true : false;
    }
    /** DeviceHistory-删除 */
    public void deleteDeviceHistoryList(long Id) {
        QueryBuilder<CamelDeviceHistory> qb = daoSession.getCamelDeviceHistoryDao().queryBuilder();
        DeleteQuery<CamelDeviceHistory> bd = qb.where(CamelDeviceHistoryDao.Properties.Id.eq(Id)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }
    
    
    
    
    /** RelZone-添加数据 */
    public long addToRelZoneTable(RelSafetyZone item) {
        return daoSession.getRelSafetyZoneDao().insert(item);
    }
    /** Device-更新数据 */
    public long updateToRelZoneTable(RelSafetyZone item){
    	return daoSession.getRelSafetyZoneDao().insertOrReplace(item);
    }
    /** RelZone-查询表当前设备下的账号 */
    public List<RelSafetyZone> getRelZoneIdentify(String deviceIdentify) {
    	QueryBuilder<RelSafetyZone> qb = daoSession.getRelSafetyZoneDao().queryBuilder();
        qb.where(RelSafetyZoneDao.Properties.RelZoneIdentify.eq(deviceIdentify));
        if (qb.list().size() > 0) {
            return qb.list();
        } else {
            return null;
        }
    }
    /** RelZone-查询是否保存 */
    public boolean isRelZoneSaved(String DeviceIdentify) {
        QueryBuilder<RelSafetyZone> qb = daoSession.getRelSafetyZoneDao().queryBuilder();
        qb.where(RelSafetyZoneDao.Properties.RelZoneIdentify.eq(DeviceIdentify));
        qb.buildCount().count();
        return qb.buildCount().count() > 0 ? true : false;
    }
    /** RelZone-删除 */
    public void deleteRelZoneList(String time) {
        QueryBuilder<RelSafetyZone> qb = daoSession.getRelSafetyZoneDao().queryBuilder();
        DeleteQuery<RelSafetyZone> bd = qb.where(RelSafetyZoneDao.Properties.RelZoneCreated.eq(time)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }
    
    
    
    
    /** DataRelFamily-查询表当前设备下的账号 */
    public List<DataRelFamily> getDataRelFamilyToIdentify(String deviceIdentify) {
    	QueryBuilder<DataRelFamily> qb = daoSession.getDataRelFamilyDao().queryBuilder();
        qb.where(DataRelFamilyDao.Properties.DataRelSubject.eq(deviceIdentify));
        if (qb.list().size() > 0) {
            return qb.list();
        } else {
            return null;
        }
    }
    /** DataRelFamily-查询是否保存 */
    public boolean isDataRelFamilySaved(String DeviceIdentify) {
        QueryBuilder<DataRelFamily> qb = daoSession.getDataRelFamilyDao().queryBuilder();
        qb.where(DataRelFamilyDao.Properties.DataRelSubject.eq(DeviceIdentify));
        qb.buildCount().count();
        return qb.buildCount().count() > 0 ? true : false;
    }
    /** DataRelFamily-删除 */
    public void deleteDataRelFamilyList(String DeviceIdentify) {
        QueryBuilder<DataRelFamily> qb = daoSession.getDataRelFamilyDao().queryBuilder();
        DeleteQuery<DataRelFamily> bd = qb.where(DataRelFamilyDao.Properties.DataRelSubject.eq(DeviceIdentify)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }
    /** DataRelFamily-添加数据 */
    public long addToDataRelFamilyTable(DataRelFamily item) {
        return daoSession.getDataRelFamilyDao().insert(item);
    }
    
    
    
    public void logout() {
        daoSession.getCamelUserDao().deleteAll();
        daoSession.getCamelDeviceDao().deleteAll();
        daoSession.getCamelDevicePushDao().deleteAll();
        daoSession.getCamelDeviceHistoryDao().deleteAll();
        daoSession.getRelSafetyZoneDao().deleteAll();
        daoSession.getDataRelFamilyDao().deleteAll();
    }
}

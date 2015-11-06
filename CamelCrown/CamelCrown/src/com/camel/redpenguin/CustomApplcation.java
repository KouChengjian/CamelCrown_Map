package com.camel.redpenguin;

import java.util.ArrayList;


import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.camel.redpenguin.config.Config;
import com.camel.redpenguin.greendao.CamelDevice;
import com.camel.redpenguin.greendao.CamelUser;
import com.camel.redpenguin.greendao.DaoMaster;
import com.camel.redpenguin.greendao.DaoMaster.OpenHelper;
import com.camel.redpenguin.greendao.DaoSession;
import com.camel.redpenguin.ui.MainActivity;
import com.camel.redpenguin.ui.SplashActivity;
import com.camel.redpenguin.util.LockPatternUtils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * @ClassName: CustomApplcation
 * @Description: 
 * @author: 
 * @date: 
 */
public class CustomApplcation extends Application{
	// 百度
	public LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;
	// 常在通知
	private NotificationManager mNotificationManager; // 通知管理器
	private PendingIntent mPendingIntent; // 通知显示内容
	private Notification mNotification;
	private int Notification_ID_BASE = 110;
	
	// 安全锁
	private String SAFETY_LOCK_STATE ;
	private LockPatternUtils mLockPatternUtils;
	// 数据库
	private static DaoMaster daoMaster;
    private static DaoSession daoSession;
	
	public static CustomApplcation mInstance;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		mLockPatternUtils = new LockPatternUtils(this);
		init();
	}
	
	public void init() {
		initBaidu();
		initIdentification();
	}
	
	/**
	 * 初始化百度相关sdk initBaidumap
	 * @Title: initBaidumap
	 * @Description: 
	 * @param
	 * @return void
	 * @throws
	 */
	private void initBaidu() {
		// 初始化地图Sdk
		SDKInitializer.initialize(this);
		// 初始化定位sdk
		initBaiduLocClient();
	}
	
	/**
	 * 初始化百度定位sdk
	 * 
	 * @Title: initBaiduLocClient
	 * @Description: 
	 * @param
	 * @return void
	 * @throws
	 */
	private void initBaiduLocClient() {
		mLocationClient = new LocationClient(this.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
	}
	
	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// Receive Location
			double latitude = location.getLatitude();
			double longtitude = location.getLongitude();
			if(TextUtils.isEmpty(getLatitude()) || TextUtils.isEmpty(getLongtitude())){
				setLatitude(String.valueOf(latitude));
				setLongtitude(String.valueOf(longtitude));
			} 
			if (getLatitude().equals(latitude) && getLongtitude().equals(longtitude)){
				Log.e("onReceiveLocation","两次获取坐标相同");
			}else{
				Log.e("onReceiveLocation",latitude+"写入经纬度" +longtitude);
				setLatitude(String.valueOf(latitude));
				setLongtitude(String.valueOf(longtitude));
			}
			mLocationClient.stop();
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
		}
	}
	
	/**
	 * @Title: initNotification
	 * @Description: 状态栏显示信息
	 * @param  1 - 电量 2- 报警  3- 两者   4 - 安全区域
	 * @return void
	 * @throws
	 */
	private void initNotification(int type ,String string) {
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// 新建状态栏通知
		mNotification = new Notification();
		if(!MainActivity.activityRun){
//			Intent intent = new Intent(this.getApplicationContext(), SplashActivity.class);
//			mPendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, 0);
//			mNotification.contentIntent = mPendingIntent;
//			// 点击'Clear'时，不清楚该通知(QQ的通知无法清除，就是用的这个)
//			mNotification.flags |= Notification.FLAG_NO_CLEAR;
		}
		mNotification.icon = R.drawable.logo;
		// 通知时在状态栏显示的内容
		mNotification.tickerText = string;
		if(type == 2 || type == 3){
			mNotification.defaults |= Notification.DEFAULT_SOUND;
		}
		mNotification.defaults |= Notification.DEFAULT_VIBRATE;
		mNotification.defaults |= Notification.DEFAULT_LIGHTS;
		// 让声音、振动无限循环，直到用户响应
//		mNotification.flags |= Notification.FLAG_INSISTENT;
		// 通知被点击后，自动消失
		mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
		// 点击'Clear'时，不清楚该通知(QQ的通知无法清除，就是用的这个)
//		mNotification.flags |= Notification.FLAG_NO_CLEAR;
		// 自定义界面
		RemoteViews rv = new RemoteViews(getPackageName(),R.layout.text3);
		rv.setTextViewText(R.id.tv_rv_text, string);
//		rv.setProgressBar(R.id.pb_rv, 80, 20, false);
		mNotification.contentView = rv;
		//mNotificationManager.notify(Notification_ID_BASE, mNotification);
	}
	
	/**
	 * @Title: initIdentification
	 * @Description: 获取设备标识，包括  手机 蓝牙 网络
	 * @param
	 * @return void
	 * @throws
	 */
	public void initIdentification(){
		TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE); 
		String szImei = TelephonyMgr.getDeviceId();	
//		WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE); 
//		String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();		
//		BluetoothAdapter m_BluetoothAdapter = null;     
//		m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();      
//		String m_szBTMAC = m_BluetoothAdapter.getAddress();
		setIdentification(szImei);
		//Log.e("Identification",szImei);
	}
	
	/**
	 *  结束状态栏通知
	 */
	public void notificationCancel(){
		if(mNotificationManager != null)
		    mNotificationManager.cancel(Notification_ID_BASE);
	}
	/**
	 *  开启状态栏通知
	 */
	public void notificationNotify(int type ,String string){
		initNotification(type , string);
		mNotificationManager.notify(Notification_ID_BASE, mNotification);
	}
	
	
	public static CustomApplcation getInstance() {
		return mInstance;
	}
	
	/** 
	 *  安全锁的工具类
	 */
	public LockPatternUtils getLockPatternUtils() {
		return mLockPatternUtils;
	}
	public void setSafetyLockState(String str){
		SAFETY_LOCK_STATE = str;
	}
	public String getSafetyLockState(){
		return SAFETY_LOCK_STATE ;
	}
	
	
	public final String CURRENTUSER = "currentUser";// 是否有当前用户
	private String currentUser = "";

	/**
	 * 获取当前用户
	 * 
	 * @return  null和0为没有 1为有
	 */
	public String getCurrentUser() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		currentUser = preferences.getString(CURRENTUSER, "");
		return currentUser;
	}

	/**
	 * 设置当前用户
	 * 
	 * @param 
	 */
	public void setCurrentUser(String lon) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		if (editor.putString(CURRENTUSER, lon).commit()) {
			currentUser = lon;
		}
	}
	
	public final String CITY = "city";// 城市
	private String city = "";

	/**
	 * 获取用户城市
	 * 
	 * @return
	 */
	public String getUserCity() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		city = preferences.getString(CITY, "");
		return city;
	}

	/**
	 * 设置用户城市
	 * 
	 * @param pwd
	 */
	public void setUserCity(String lon) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		if (editor.putString(CITY, lon).commit()) {
			city = lon;
		}
	}
	
	public final String PREF_LONGTITUDE = "longtitude";// 经度
	private String longtitude = "";

	/**
	 * 获取经度
	 * 
	 * @return
	 */
	public String getLongtitude() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		longtitude = preferences.getString(PREF_LONGTITUDE, "");
		return longtitude;
	}

	/**
	 * 设置经度
	 * 
	 * @param pwd
	 */
	public void setLongtitude(String lon) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		if (editor.putString(PREF_LONGTITUDE, lon).commit()) {
			longtitude = lon;
		}
	}
	
	public final String PREF_LATITUDE = "latitude";// 经度
	private String latitude = "";

	/**
	 * 获取纬度
	 * 
	 * @return
	 */
	public String getLatitude() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		latitude = preferences.getString(PREF_LATITUDE, "1");
		return latitude;
	}

	/**
	 * 设置维度
	 * 
	 * @param pwd
	 */
	public void setLatitude(String lat) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		if (editor.putString(PREF_LATITUDE, lat).commit()) {
			latitude = lat;
		}
	}
	
	public final String PREF_USER_NAME = "username";// 账号
	private String username = "";
	/**
	 * 获取账号
	 * 
	 * @return
	 */
	public String getUsername() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		username = preferences.getString(PREF_USER_NAME, "");
		return username;
	}

	/**
	 * 设置账号
	 * 
	 * @param pwd
	 */
	public void setUsername(String name) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		if (editor.putString(PREF_USER_NAME, name).commit()) {
			username = name;
		}
	}
	
	public final String PREF_USER_IDENTIFICATION = "identification";// 标识
	private String identification = "";
	/**
	 * 获取标识
	 * 
	 * @return
	 */
	public String getIdentification() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		identification = preferences.getString(PREF_USER_IDENTIFICATION, "");
		return identification;
	}

	/**
	 * 设置标识
	 * 
	 * @param pwd
	 */
	public void setIdentification(String identification) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		if (editor.putString(PREF_USER_IDENTIFICATION, identification).commit()) {
			this.identification = identification;
		}
	}
	
	public final String BAIDU_MAP_LOCATION_MODE = "baidulocationmode"; // 百度定位
	private String locationMode = "";
	/**
	 * 获取定位模式
	 * 
	 * @return
	 */
	public String getBaiduLocationMode() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		locationMode = preferences.getString(BAIDU_MAP_LOCATION_MODE, "");
		return locationMode;
	}

	/**
	 * 设置定位模式
	 * 
	 * @param pwd
	 */
	public void setBaiduLocationMode(String locationMode) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		if (editor.putString(BAIDU_MAP_LOCATION_MODE, locationMode).commit()) {
			this.locationMode = locationMode;
		}
	}
	
	private List<CamelDevice> contactList = new ArrayList<CamelDevice>();
	
	/**
	 * 获取内存中好友user list
	 * 
	 * @return
	 */
	public List<CamelDevice> getContactList() {
		return contactList;
	}

	/**
	 * 设置好友user list到内存中
	 * @param contactList
	 */
	public void setContactList(List<CamelDevice> contactList) {
		if (this.contactList != null) {
			this.contactList.clear();
		}
		this.contactList = contactList;
	}
	
	private CamelUser currentRegisterCamelUser = null; // 当前注册用户
	/**
	 * 保存当前注册用户
	 * 
	 * @return
	 */
	public CamelUser getRegisterCamelUser() {
		return currentRegisterCamelUser;
	}

	/**
	 * 设置当前注册用户
	 * @param contactList
	 */
	public void setRegisterCamelUser(CamelUser currentRegisterCamelUser) {
		this.currentRegisterCamelUser = currentRegisterCamelUser;
	}
	
	private CamelDevice currentCamelDevice = null; //new CamelDevice()
	
	/**
	 * 保存选择的设备
	 * 
	 * @return
	 */
	public CamelDevice getCurrentCamelDevice() {
		return currentCamelDevice;
	}

	/**
	 * 设置保存选择的设备
	 * @param contactList
	 */
	public void setCurrentCamelDevice(CamelDevice currentCamelDevice) {
		this.currentCamelDevice = currentCamelDevice;
	}
	
	/**
     * 取得DaoMaster
     * 
     * @param context
     * @return
     */
    public static DaoMaster getDaoMaster() {
        if (daoMaster == null) {
            OpenHelper helper = new DaoMaster.DevOpenHelper(mInstance, Config.DATABASE_NAME,
                    null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    /**
     * 取得DaoSession
     * 
     * @param context
     * @return
     */
    public static DaoSession getDaoSession() {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster();
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }
    
    
    /**
	 * 
	 *  @Description    : 这个包名的程序是否在运行
	 *  @Method_Name    : isRunningApp
	 *  @param context 上下文
	 *  @param packageName 判断程序的包名
	 *  @return 必须加载的权限     
	 *  @return         : boolean
	 *  @Creation Date  : 2014-10-31 下午1:14:15 
	 *  @version        : v1.00
	 *  @Author         : JiaBin
	 *  @Update Date    : 
	 *  @Update Author  : JiaBin
	 */
	public static boolean isRunningApp(Context context, String packageName) {
		boolean isAppRunning = false;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(100);
		for (RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals(packageName) && info.baseActivity.getPackageName().equals(packageName)) {
				isAppRunning = true;
				// find it, break
				break;
			}
		}
		return isAppRunning;
	}
	
	
}

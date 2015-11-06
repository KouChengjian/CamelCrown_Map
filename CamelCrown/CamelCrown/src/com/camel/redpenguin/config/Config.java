package com.camel.redpenguin.config;

import android.annotation.SuppressLint;

/**
 * @ClassName: Config 
 * @Description:  配置
 * @author: kcj
 * @date: 
 */
public class Config {
	
	public static final String SERVE_IP = "120.25.200.159"; 
	public static final int SERVE_PORT = 65431;  
	
	//public static final String SERVE_IP = "192.168.10.124";
	/*public static final String SERVE_IP = "192.168.10.106"; 
	public static final int SERVE_PORT = 60000; */ 
	
	// 短信验证
	public static final String APP_SMS_KEY = "5804790f60de"; 
	public static final String APP_SMS_SECRET = "ca673ad52eed1833d4303aed909bc727"; 
	
	/**
     * 历史页数
     */
    public static int HISTORY_PAGE = 1;
    /**
     * 历史数据长度
     */
    public static int HISTORY_PAGE_LENGTH = 0;
	/**
     * 数据库版本号
     */
    public static final int DATABASE_VERSION = 1;

    /**
     * 数据库名称
     */
    public static final String DATABASE_NAME = "greendao.db";
    
    
    /**
	 * 我的头像保存目录
	 */
	@SuppressLint("SdCardPath")
	public static String MyAvatarDir = "/sdcard/Camel/avatar/";
	/**
	 * 拍照回调
	 */
	public static final int REQUESTCODE_UPLOADAVATAR_CAMERA = 1;//拍照修改头像
	public static final int REQUESTCODE_UPLOADAVATAR_LOCATION = 2;//本地相册修改头像
	public static final int REQUESTCODE_UPLOADAVATAR_CROP = 3;//系统裁剪头像
	/**
	 * 添加设备的ID
	 */
	public static String DEVICE_IDENTIFY = null; // 添加设备和查询管理员的代码可以使用
	/**
	 * 添加设备的ID
	 */
	public static String DEVICE_ADMINISTRATOR = null; // 添加设备接收设备管理员账号
}

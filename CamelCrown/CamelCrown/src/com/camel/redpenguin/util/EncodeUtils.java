package com.camel.redpenguin.util;

import java.util.StringTokenizer;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.camel.redpenguin.CustomApplcation;
import com.camel.redpenguin.greendao.CamelUser;


/**
 * @ClassName: EncodeUtils
 * @Description: 数据编码
 * @author: kcj
 * @date: 2015-3-13
 */
public class EncodeUtils {

	private static Context mContext = CustomApplcation.getInstance();
	private static String orderEncodeStar = "#";
	private static String orderEncodeEnd = "^";
	
	/**
	 * @ClassName: registerDataEncode
	 * @Description: 注册
	 * @instruct: 1F
	 * @return: order
	 */
	public static String registerDataEncode(String account, String password){
		// 对CamelUser类进行数据填充
		CamelUser mCamelUser = new CamelUser();
		mCamelUser.setUserAccount(account);
		mCamelUser.setUserPassword(password);
		mCamelUser.setUserPhonetype("android");
		mCamelUser.setUserLatitude(CustomApplcation.getInstance().getLatitude());
		mCamelUser.setUserLongitude(CustomApplcation.getInstance().getLongtitude());
		mCamelUser.setUserNike("爸爸");
		mCamelUser.setUserPhoneIdentify(CustomApplcation.getInstance().getIdentification());
		mCamelUser.setUserAdministrator("0");
		CustomApplcation.getInstance().setRegisterCamelUser(mCamelUser);
		// 转换16进制 
		String order = "";
		String data = "";
		// 起始标识
		 order += HexadecimalConver.encode(orderEncodeStar); // 字符串转换ASCLL
		// 地址（也就是账号）
		order += paddingdata(Long.toHexString(Long.valueOf(mCamelUser.getUserAccount()).longValue()).toUpperCase(),10);
		// 指令
		order += "1F";  
		// 密码
		String mPassword = HexadecimalConver.encode(mCamelUser.getUserPassword());
		data += paddingdata(mPassword,12*2);
		// 型号
		String mModel = HexadecimalConver.encode(mCamelUser.getUserPhonetype());
		data += paddingdata(mModel,7*2);
		// 纬度
		String mLatitude = Integer.toHexString(Integer.valueOf(cnlatLonFormat(mCamelUser.getUserLatitude())).intValue());
		if(!TextUtils.isEmpty(mLatitude)){
			data += paddingdata(mLatitude,8).toUpperCase();
		}else{
			data += paddingdata("0",8);
		}
		// 经度
		String mLongtitude = Integer.toHexString(Integer.valueOf(cnlatLonFormat(mCamelUser.getUserLongitude())).intValue());
		if(!TextUtils.isEmpty(mLongtitude)){
			data += paddingdata(mLongtitude,8).toUpperCase();
		}else{
			data += paddingdata("0",8);
		}
		// 昵称
		String mNickname = HexadecimalConver.encode(mCamelUser.getUserNike());
		if(!TextUtils.isEmpty(mNickname)){
			data += paddingdata(mNickname,20*2);
		}else{
			data += paddingdata("0",20*2);
		}
		// 手机标识
		String mIdentification = HexadecimalConver.encode(mCamelUser.getUserPhoneIdentify());
		if(!TextUtils.isEmpty(mIdentification)){
			data += paddingdata(mIdentification,15*2);
		}else{
			data += paddingdata("0",15*2);
		}
		// 数据长度
		int length = data.length();
		order += paddingdata(Integer.toHexString(length),4).toUpperCase();// toUpperCase 大写
		// 加数据
		order += data;
		order += HexadecimalConver.encode(orderEncodeEnd);
		return order;
	}
	
	/**
	 * @ClassName: loginDataEncode
	 * @Description: 发登
	 * @instruct: 2F
	 * @return: order
	 */
	public static String loginDataEncode(String account , String password){
		// 转换16进制 
		String order = "";
		String data = "";
		// 起始标识
		order += HexadecimalConver.encode(orderEncodeStar);
		// 地址（也就是账号）
		order += paddingdata(Long.toHexString(Long.valueOf(account).longValue()).toUpperCase(),10);
		// 指令
		order += "2F"; 
		// 密码
		String mPassword = HexadecimalConver.encode(password);
		data += paddingdata(mPassword,12*2);
		// 数据长度
		int length = data.length();
		order += paddingdata(Integer.toHexString(length),4).toUpperCase(); // 大写
		// 加数据
		order += data;
		// 终止标识
		order += HexadecimalConver.encode(orderEncodeEnd);
		return order;
	}
	
	/**
	 * @ClassName: queryDeviceDataEncode
	 * @Description: 查询设备
	 * @author: kcj
	 * @instruct: 3A
	 * @return: order
	 */
	public static String queryDeviceDataEncode(String account ){
		// 转换16进制 
		String order = "";
		// 起始标识
		order += HexadecimalConver.encode(orderEncodeStar);
		// 地址（也就是账号）
		order += paddingdata(Long.toHexString(Long.valueOf(account).longValue()).toUpperCase(),10);
		// 指令
		order += "3A"; 
		// 加数据
		order += "0000";
		// 终止标识
		order += HexadecimalConver.encode(orderEncodeEnd);
		return order;
	}
	
	/**
	 * @ClassName: queryIsAdministratorDataEncode
	 * @Description: 查询是否存在管理员
	 * @author: kcj
	 * @instruct: 3B
	 * @return: order
	 */
    public static String queryIsAdministratorDataEncode(String content){
    	// 转换16进制 
    	String order = "";
    	String data = "";
    	// 起始标识
    	order += HexadecimalConver.encode(orderEncodeStar);
    	// 地址（也就是账号）
    	order += paddingdata(Long.toHexString(Long.valueOf(CustomApplcation.getInstance().getUsername()).longValue()).toUpperCase(),10);
    	// 指令
    	order += "3B"; 
    	// 设备ID
    	data += paddingdata(Long.toHexString(Long.valueOf(content).longValue()).toUpperCase(),10);
    	// 数据长度
    	int length = data.length();
    	order += paddingdata(Integer.toHexString(length),4).toUpperCase(); // 大写
    	// 加数据
    	order += data;
    	// 终止标识
    	order += HexadecimalConver.encode(orderEncodeEnd);
		return order;
    }
	
    /**
	 * @ClassName: addDeviceDataEncode
	 * @Description: 添加设备
	 * @author: kcj
	 * @instruct: 2A
	 * @return: order
	 */
    public static String addDeviceDataEncode(String content){
    	// 转换16进制 
    	String order = "";
    	String data = "";
    	// 起始标识
    	order += HexadecimalConver.encode(orderEncodeStar);
    	// 地址（也就是账号）
    	order += paddingdata(Long.toHexString(Long.valueOf(CustomApplcation.getInstance().getUsername()).longValue()).toUpperCase(),10);
    	// 指令
    	order += "2A"; 
    	// 设备ID
    	data += paddingdata(Long.toHexString(Long.valueOf(content).longValue()).toUpperCase(),10);
    	// 昵称
    	String mNickname = HexadecimalConver.encode("设备");
    	if(!TextUtils.isEmpty(mNickname)){
    		data += paddingdata(mNickname,10*2);
    	}else{
    		data += paddingdata("0",10*2);
    	}
    	// 数据长度
    	int length = data.length();
    	order += paddingdata(Integer.toHexString(length),4).toUpperCase(); // 大写
    	// 加数据
    	order += data;
    	// 终止标识
    	order += HexadecimalConver.encode(orderEncodeEnd);
		return order;
    }
	
	
    /**
	 * @ClassName: cancelDeviceDataEncode
	 * @Description: 取消关注
	 * @author: kcj
	 * @instruct: 3C
	 * @return: order
	 */
    public static String cancelDeviceDataEncode(String content){
    	// 转换16进制 
    	String order = "";
    	String data = "";
    	// 起始标识
    	order += HexadecimalConver.encode(orderEncodeStar);
    	// 地址（也就是账号）
    	order += paddingdata(Long.toHexString(Long.valueOf(CustomApplcation.getInstance().getUsername()).longValue()).toUpperCase(),10);
    	// 指令
    	order += "3C"; 
    	// 设备ID
    	data += paddingdata(Long.toHexString(Long.valueOf(content).longValue()).toUpperCase(),10);
    	// 数据长度
    	int length = data.length();
    	order += paddingdata(Integer.toHexString(length),4).toUpperCase(); // 大写
    	// 加数据
    	order += data;
    	// 终止标识
    	order += HexadecimalConver.encode(orderEncodeEnd);
    	return order;
    }
    
    /**
	 * @ClassName: setSelectorDeviceDataEncode
	 * @Description: 设置选择的设备
	 * @author: kcj
	 * @instruct: 1B
	 * @return: order
	 */
    public static String selectorDeviceDataEncode(String content){
    	// 转换16进制 
    	String order = "";
    	String data = "";
    	// 起始标识
    	order += HexadecimalConver.encode(orderEncodeStar);
    	// 地址（也就是账号）
    	order += paddingdata(Long.toHexString(Long.valueOf(CustomApplcation.getInstance().getUsername()).longValue()).toUpperCase(),10);
    	// 指令
    	order += "1B"; 
    	// 设备ID
    	data += paddingdata(Long.toHexString(Long.valueOf(content).longValue()).toUpperCase(),10);
    	// 数据长度
    	int length = data.length();
    	order += paddingdata(Integer.toHexString(length),4).toUpperCase(); // 大写
    	// 加数据
    	order += data;
    	// 终止标识
    	order += HexadecimalConver.encode(orderEncodeEnd);
    	return order;
    }
    
    /**
	 * @ClassName: heartbeatRateDataEncode
	 * @Description: 心跳速率
	 * @author: kcj
	 * @instruct: 1C
	 * @return: order
	 */
    public static String heartbeatRateDataEncode(){
    	// 转换16进制 
    	String order = "";
    	String data = "";
    	// 起始标识
    	order += HexadecimalConver.encode(orderEncodeStar);
    	// 地址（也就是账号）
    	if(TextUtils.isEmpty(CustomApplcation.getInstance().getUsername())){
    		order += "0000000000";
    	}else{
    		order += paddingdata(Long.toHexString(Long.valueOf(CustomApplcation.getInstance().getUsername()).longValue()).toUpperCase(),10);
    	}
    	// 指令
    	order += "1C"; 
    	// 心跳
    	data += "00";
    	// 数据长度
    	int length = data.length();
    	order += paddingdata(Integer.toHexString(length),4).toUpperCase(); // 大写
    	// 加数据
    	order += data;
    	// 终止标识
    	order += HexadecimalConver.encode(orderEncodeEnd);
    	return order;
    }
    
    /**
	 * @ClassName: locationModeDataEncode
	 * @Description: 设置定位模式
	 * @author: kcj
	 * @instruct: 1D
	 * @return: order
	 */
    public static String locationModeDataEncode(String frequency,String content){
    	// 转换16进制 
    	String order = "";
    	String data = "";
    	// 起始标识
    	order += HexadecimalConver.encode(orderEncodeStar);
    	// 地址（也就是账号）
    	order += paddingdata(Long.toHexString(Long.valueOf(CustomApplcation.getInstance().getUsername()).longValue()).toUpperCase(),10);
    	// 指令
    	order += "1D"; 
    	// 设备ID
    	data += paddingdata(Long.toHexString(Long.valueOf(content).longValue()).toUpperCase(),10);
    	// 频率
    	data += frequency;
    	// 数据长度
    	int length = data.length();
    	order += paddingdata(Integer.toHexString(length),4).toUpperCase(); // 大写
    	// 加数据
    	order += data;
    	// 终止标识
    	order += HexadecimalConver.encode(orderEncodeEnd);
    	return order;
    }
    
    /**
	 * @ClassName: realTimeLocationModeDataEncode
	 * @Description: 实时定位
	 * @author: kcj
	 * @instruct: 1E
	 * @return: order
	 */
    public static String realTimeLocationModeDataEncode(String content){
    	// 转换16进制 
    	String order = "";
    	String data = "";
    	// 起始标识
    	order += HexadecimalConver.encode(orderEncodeStar);
    	// 地址（也就是账号）
    	order += paddingdata(Long.toHexString(Long.valueOf(CustomApplcation.getInstance().getUsername()).longValue()).toUpperCase(),10);
    	// 指令
    	order += "1E";
    	// 设备ID
    	data += paddingdata(Long.toHexString(Long.valueOf(content).longValue()).toUpperCase(),10);
    	// 数据长度
    	int length = data.length();
    	order += paddingdata(Integer.toHexString(length),4).toUpperCase(); // 大写
    	// 加数据
    	order += data;
    	// 终止标识
    	order += HexadecimalConver.encode(orderEncodeEnd);
    	return order;
    }
    
    /**
	 * @ClassName: queryFamilyDataEncode
	 * @Description: 获取家庭成员
	 * @author: kcj
	 * @instruct: 3D
	 * @return: order
	 */
    public static String queryFamilyDataEncode(String content){
    	// 转换16进制 
    	String order = "";
    	String data = "";
    	// 起始标识
    	order += HexadecimalConver.encode(orderEncodeStar);
    	// 地址（也就是账号）
    	order += paddingdata(Long.toHexString(Long.valueOf(CustomApplcation.getInstance().getUsername()).longValue()).toUpperCase(),10);
    	// 指令
    	order += "3D"; 
    	// 设备ID
    	data += paddingdata(Long.toHexString(Long.valueOf(content).longValue()).toUpperCase(),10);
    	// 数据长度
    	int length = data.length();
    	order += paddingdata(Integer.toHexString(length),4).toUpperCase(); // 大写
    	// 加数据
    	order += data;
    	// 终止标识
    	order += HexadecimalConver.encode(orderEncodeEnd);
    	return order;
    }
    
    /**
	 * @ClassName: querydDeviceDataEncode
	 * @Description: 查询设备的状态信息
	 * @author: kcj
	 * @instruct: 3E
	 * @return: order
	 */
    public static String querydDeviceDataEncode(String content, String time ,int page){
    	// 转换16进制 
    	String order = "";
    	String data = "";
    	// 起始标识
    	order += HexadecimalConver.encode(orderEncodeStar);
    	// 地址（也就是账号）
    	order += paddingdata(Long.toHexString(Long.valueOf(CustomApplcation.getInstance().getUsername()).longValue()).toUpperCase(),10);
    	// 指令
    	order += "3E"; 
    	// 设备ID
    	data += paddingdata(Long.toHexString(Long.valueOf(content).longValue()).toUpperCase(),10);
    	// 起始时间
    	String[] array=time.split(" ");
    	String[] arraydate =  array[0].split("-");
    	data += paddingdata(Integer.toHexString(Integer.valueOf(arraydate[0].substring(2, 4)).intValue()),2).toUpperCase();
    	//Log.e("data", paddingdata(Integer.toHexString(Integer.valueOf(arraydate[0]).intValue()),2));
    	data += paddingdata(Integer.toHexString(Integer.valueOf(arraydate[1]).intValue()),2).toUpperCase();
    	//Log.e("data", paddingdata(Integer.toHexString(Integer.valueOf(arraydate[1]).intValue()),2));
    	data += paddingdata(Integer.toHexString(Integer.valueOf(arraydate[2]).intValue()),2).toUpperCase();
    	//Log.e("data", paddingdata(Integer.toHexString(Integer.valueOf(arraydate[2]).intValue()),2));
    	data += "000000";
    	// 页数
    	data += paddingdata(String.valueOf(Integer.toHexString(page)).toString(),2).toUpperCase();
    	// 数据长度
    	int length = data.length();
    	order += paddingdata(Integer.toHexString(length),4).toUpperCase(); // 大写
    	// 加数据
    	order += data;
    	// 终止标识
    	order += HexadecimalConver.encode(orderEncodeEnd);
    	return order;
    }
    
    /**
	 * @ClassName: amendPasswordDataEncode
	 * @Description: 修改密码
	 * @author: kcj
	 * @instruct: 3F
	 * @return: order
	 */
    public static String amendPasswordDataEncode(String acc,String content){
    	// 转换16进制 
    	String order = "";
    	String data = "";
    	// 起始标识
    	order += HexadecimalConver.encode(orderEncodeStar);
    	// 地址（也就是账号）
    	order += paddingdata(Long.toHexString(Long.valueOf(acc).longValue()).toUpperCase(),10);
    	// 指令
    	order += "3F"; 
    	// 密码
    	String mPassword = HexadecimalConver.encode(content);
    	data += paddingdata(mPassword,12*2);
    	// 数据长度
    	int length = data.length();
    	order += paddingdata(Integer.toHexString(length),4).toUpperCase(); // 大写
    	// 加数据
    	order += data;
    	// 终止标识
    	order += HexadecimalConver.encode(orderEncodeEnd);
    	return order;
    }
	
    /**
	 * @ClassName: newestMessageDataEncode
	 * @Description: 更新当前设备最新数据
	 * @author: kcj
	 * @instruct: 4A
	 * @return: order
	 */
    public static String newestMessageDataEncode(String content){
    	// 转换16进制 
    	String order = "";
    	String data = "";
    	// 起始标识
    	order += HexadecimalConver.encode(orderEncodeStar);
    	// 地址（也就是账号）
    	order += paddingdata(Long.toHexString(Long.valueOf(CustomApplcation.getInstance().getUsername()).longValue()).toUpperCase(),10);
    	// 指令
    	order += "4A"; 
    	// 设备ID
    	data += paddingdata(Long.toHexString(Long.valueOf(content).longValue()).toUpperCase(),10);
    	// 数据长度
    	int length = data.length();
    	order += paddingdata(Integer.toHexString(length),4).toUpperCase(); // 大写
    	// 加数据
    	order += data;
    	// 终止标识
    	order += HexadecimalConver.encode(orderEncodeEnd);
//    	Toast.makeText(mContext, order, 1).show();
    	return order;
    }
	
    /**
	 * @ClassName: queryLengthDataEncode
	 * @Description: 获取指定历史记录长度
	 * @author: kcj
	 * @instruct: 4B
	 * @return: order
	 */
    public static String queryLengthDataEncode(String content, String time){
    	// 转换16进制 
    	String order = "";
    	String data = "";
    	// 起始标识
    	order += HexadecimalConver.encode(orderEncodeStar);
    	// 地址（也就是账号）
    	order += paddingdata(Long.toHexString(Long.valueOf(CustomApplcation.getInstance().getUsername()).longValue()).toUpperCase(),10);
    	// 指令
    	order += "4B"; 
    	// 设备ID
    	data += paddingdata(Long.toHexString(Long.valueOf(content).longValue()).toUpperCase(),10);
    	// 起始时间
    	String[] array=time.split(" ");
    	String[] arraydate =  array[0].split("-");
    	data += paddingdata(Integer.toHexString(Integer.valueOf(arraydate[0].substring(2, 4)).intValue()),2).toUpperCase();
    	data += paddingdata(Integer.toHexString(Integer.valueOf(arraydate[1]).intValue()),2).toUpperCase();
    	data += paddingdata(Integer.toHexString(Integer.valueOf(arraydate[2]).intValue()),2).toUpperCase();
    	data += "000000";
    	// 数据长度
    	int length = data.length();
    	order += paddingdata(Integer.toHexString(length),4).toUpperCase(); // 大写
    	// 加数据
    	order += data;
    	// 终止标识
    	order += HexadecimalConver.encode(orderEncodeEnd);
    	return order;
    }
	
	/**
	 * @ClassName: paddingdata
	 * @Description: 对zifuc进行填充 0
	 * @return: 
	 */
	public static String paddingdata(String initiate , int limit){
		String str1 = initiate;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < limit - str1.length();i++){
			sb.append("0");
		}
		sb.append(str1);
		return sb.toString();
	}	
	
	/**
	 *  发送经纬度格式编码
	 */
	public static String cnlatLonFormat(String latitude){
		StringTokenizer token = new StringTokenizer(latitude ,".");
		String first= token.nextToken();
		String second=token.nextToken();
		StringBuilder sb = new StringBuilder();
		sb.append(second);
		for(int i = second.length() ; i < 6 ; i++){
			sb.append("0");
		}
		return first+sb;
	} 
}

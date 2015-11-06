package com.camel.redpenguin.util;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.camel.redpenguin.CustomApplcation;
import com.camel.redpenguin.bean.DBHelper;
import com.camel.redpenguin.bean.Packet;
import com.camel.redpenguin.config.Config;
import com.camel.redpenguin.config.SocketInit;
import com.camel.redpenguin.greendao.CamelDevice;
import com.camel.redpenguin.greendao.CamelDeviceHistory;
import com.camel.redpenguin.greendao.CamelDevicePush;
import com.camel.redpenguin.greendao.CamelUser;
import com.camel.redpenguin.greendao.DataRelFamily;
import com.camel.redpenguin.greendao.RelSafetyZone;
import com.camel.redpenguin.listener.EventListener;

/**
 * @ClassName: DecodeData
 * @Description: 数据解码
 * @author: kcj
 * @date: 2015-3-13
 */
public class DecodeUtils {
	
	private static Context mContext = CustomApplcation.getInstance();
	public static String mobilePhone = null;
	
	/**
	 * @ClassName: registerDataEncode
	 * @Description: 注册
	 * @instruct: 1F
	 * @return: order
	 */
    @SuppressLint("DefaultLocale")
	public static void decodeData(String read){
    	// 判断总长度
    	int length = read.length();
    	if(length < 20){
    		Log.i("DecodeData", "长度小于20");
    		return;
    	}
    	// 起始终止判断
    	String star = read.substring(0, 2);
    	String end = read.substring(read.length()-2, read.length());
    	if(!star.equals("23") || !end.equals("5E") ){ 
    		Log.i("DecodeData", "起始终止判断失败");
    		return ;
    	}
    	// 地址判断 (账号)
    	String account = read.substring(2, 12).replaceFirst("^0+", "");
    	TelNumMatch numMatch = new TelNumMatch(String.valueOf(Long.parseLong(account,16)));
    	if( numMatch.matchNum() == 5 || numMatch.matchNum() == 4){
    		Log.i("DecodeData", "账号格式错误");
    		return ;
    	}else{
    		mobilePhone = String.valueOf(Long.parseLong(account,16));
    	}
    	// 判断数据长度是否一致
		String dataLength = read.substring(14, 18).replaceFirst("^0+", ""); // 数据长度
	    String countData = read.substring(18, read.length()-2); // 获取数据计算长度
		if(!dataLength.equals(String.valueOf(Long.toHexString(Long.valueOf(countData.length()).longValue()).toUpperCase()))){
			Log.i("countDecodeData", "数据丢失，长度错误");
			return ;
		}
    	// 指令 
    	String headerType = read.substring(12, 14);
    	Log.e("指令", headerType);
    	if(headerType.equals("1F")){                          // 注册返回数据
    		int type = registerDataDecode(countData);
    		switch(type){
    		case -1:
    			EventListener.saveListener.onFailure(1, "注册失败");
    			break;
    		case 0 :
    			EventListener.saveListener.onSuccess();
    			break;
    		}
    	}else if(headerType.equals("2F")){                    // 登入返回数据  
    		int type = loginDataDecode(countData);
            switch(type){
            case -1 :
    			EventListener.saveListener.onFailure(type, "登入失败");
    			break;
            case 0 :
    			EventListener.saveListener.onSuccess();
    			break;
    		case 1:
    			EventListener.saveListener.onFailure(type, "登入失败(数据库)");
    			break;
    		case 2:
    			EventListener.saveListener.onFailure(type, "账号不存在");
    			break;
    		case 3:
    			EventListener.saveListener.onFailure(type, "密码错误");
    			break;
    		}
    	}else if(headerType.equals("3A")){                    // 查询设备信息
    		int type = queryUserDveiceDataDecode(countData);
    		 switch(type){
    		 case -1:
      			EventListener.findListener.onFailure(type, "获取失败");
      			break;
             case 0 :
            	EventListener.findListener.onFailure(type, "无设备");	
     			break;
     		case 1:
     			EventListener.findListener.onSuccess(CustomApplcation.getInstance().getContactList());
     			break;
     		case 2:
     			EventListener.findListener.onFailure(type, "设备已存在");
     			break;
     		}
    	}else if(headerType.equals("3B")){                    // 查询是否存在管理员，存在返回账号
    		int type = queryAdministratorDataDecode(countData);
    		 switch(type){
    		 case -1:
    			 EventListener.saveListener.onFailure(type, "查询管理员失败");
    			 break;
    		 case 0:
    			 break;
    		 case 1:
    			 EventListener.saveListener.onFailure(type, "存在管理员，下一步进行短信验证");
    			 break;
    		 case 2:
    			 EventListener.saveListener.onSuccess();
    			 break;
    		 case 3:
    			 EventListener.saveListener.onFailure(type, "已存在设备");
    			 break;
    		 }
    	}else if(headerType.equals("2A")){                    // 设备添加
    		int type = addDeviceDataDecode(countData);
    		 switch(type){
    		 case -1:
    			 EventListener.saveListener.onFailure(type, "添加设备失败");
    			 break;
    		 case 0:
    			 EventListener.saveListener.onSuccess();
    			 break;
    		 case 1:
    			 EventListener.saveListener.onFailure(type, "添加设备失败");
    			 break;
    		 case 9:
    			 EventListener.saveListener.onFailure(type, "已存在设备");
    			 break;
    		 }
    	}else if(headerType.equals("3C")){                   // 取消设备关注(删除)
    		int type = cancelDeviceDataDecode(countData);
    		switch(type){
    		case -1:
   			    EventListener.saveListener.onFailure(type, "取消关注失败");
   			    break;
   		    case 0:
   			    EventListener.saveListener.onSuccess();
   			    break;
   		    }
    	}else if(headerType.equals("1B")){                 // 选择当前设备
    		int type = selectorDeviceDataDecode(countData);
    		switch(type){
    		case -1:
   			    EventListener.saveListener.onFailure(type, "设备选择失败");
   			    break;
   		    case 0:
   			    EventListener.saveListener.onSuccess();
   			    break;
   		    }
    	}else if(headerType.equals("1E")){                     // 实时定位
    		int type = realTimeLocationDataDecode(countData);
    		switch(type){
    		case -1:
    			EventListener.saveListener.onFailure(type, "获取设备经纬度失败");
   			    break;
   		    case 0:
   			    EventListener.saveListener.onSuccess();
   			    break;
   		    case 1:
   		    	EventListener.saveListener.onFailure(type, "存储失败");
			    break;
   		    }
    	}else if(headerType.equals("1D")){                   // 设置定位模式
    		int type = locationModeDataDecode(countData);
    		switch(type){
    		case -1:
    			EventListener.saveListener.onFailure(type, "设定定位模式失败(服务端)");
   			    break;
    		case 0:
    			EventListener.saveListener.onSuccess();
   			    break;
   		    case 1:
   		    	EventListener.saveListener.onFailure(type, "设定定位模式失败(数据库)");
   			    break;
   		    }
    	}else if(headerType.equals("1A")){  
    		// 推送的信息
    		int type = pushDeviceDataDecode(countData);
    		Packet packet = new Packet();
    		switch(type){
    		case -1:
    			Log.e("DecodeUtils", "推送接收失败");
    			packet.pack("23045B3D846C1A0002FF5E");
    			SocketInit.getInstance().send(packet);
   			    break;
    		case 0:
    			Intent intent = new Intent(); 
				intent.setAction("action.main.push");  
				mContext.sendBroadcast(intent); 
    			Log.e("DecodeUtils", "接收推送成功");
    			packet.pack("23045B3D846C1A0002005E");
    			SocketInit.getInstance().send(packet);
   			    break;
   		    }
    	}else if(headerType.equals("1C")){                   // 心跳
    		int type = heartbeatRateDataDecode(countData);
    		switch(type){
    		case -1:
    			EventListener.heartbeatHzListener.onFailure(type, "设定定位模式失败(服务端)");
   			    break;
    		case 0:
    			EventListener.heartbeatHzListener.onSuccess();
   			    break;
   		    }
    	}else if(headerType.equals("3D")){                 // 查询家庭成员
    		int type = queryFamilyDataDecode(countData);
    		switch(type){
    		case -1:
    			EventListener.findFamilyListener.onFailure(type, "获取家庭成员失败(服务端)");
   			    break;
    		case 0:
    			EventListener.findFamilyListener.onSuccess();
   			    break;
   		    }
    	}else if(headerType.equals("3E")){                 // 查询设备历史位置信息信息
    		int type = querydDeviceDataDecode(countData);
    		switch(type){
    		case -1:
    			Log.e("DecodeUtils", "获取失败");
    			EventListener.findStateListener.onFailure(-1, "获取失败");;
   			    break;
    		case 0:
    			Log.e("DecodeUtils", "获取成功");
    			EventListener.findStateListener.onSuccess();
   			    break;
    		case 1:
    			Log.e("DecodeUtils", "数据接收完成");
    			EventListener.findStateListener.onFailure(1, "数据接收完成");
   			    break;
   		    }
    	}else if(headerType.equals("3F")){                 // 修改密码
    		int type = anewSetPwdDataDecode(countData);
    		switch(type){
    		case -1:
   			    EventListener.saveListener.onFailure(type, "设备选择失败");
   			    break;
   		    case 0:
   			    EventListener.saveListener.onSuccess();
   			    break;
   		    }
    	}else if(headerType.equals("4A")){                 // 接收一条最新的数据
//    		Toast.makeText(mContext, read, 1).show();
    		// 推送的信息
    		int type = pushDeviceDataDecode(countData);
    		switch(type){
    		case -1:
    			Log.e("DecodeUtils", "推送接收失败");
   			    break;
    		case 0:
    			Intent intent = new Intent(); 
				intent.setAction("action.main.push");  
				mContext.sendBroadcast(intent); 
    			Log.e("DecodeUtils", "接收推送成功");
   			    break;
   		    }
    	}else if(headerType.equals("4B")){                 // 判断历史查询
    		int type = queryLengthDataDecode(countData);
    		switch(type){
    		case -1:
    			EventListener.saveListener.onFailure(-1, "失败");
   			    break;
    		case 0:
    			EventListener.saveListener.onSuccess();
   			    break;
   		    }
    	}
    }
    
	/**
	 * @ClassName: registerDataEncode
	 * @Description: 注册
	 * @instruct: 1F
	 * @return: order
	 */
	public static int registerDataDecode(String data){
		Log.i("注册", data);
		String result = data.substring(0, 2);
		if(result.equals("FF")){
			return -1;
		}else if(result.equals("00")){ 
			return 0;
		}
		return -1;
	}
	
	/**
	 * @ClassName: loginDataDecode
	 * @Description: 登入
	 * @instruct: 2F
	 * @return: order
	 */
	public static int loginDataDecode(String data){
		Log.i("登入", data);
		String result = data.substring(0, 2);
		String nike = null;
		if(result.equals("FF")){ // if(result.equals("FF"))
			return -1;
		}else if(result.equals("00")){
			nike = data.substring(2, 42).replaceFirst("^0+", ""); // 昵称 - 40字节
			// 获取mCamelUser的对象  
			CamelUser mCamelUser = null;
			if(DBHelper.getInstance(mContext).isUsersInfoSaved(mobilePhone)){
				mCamelUser = DBHelper.getInstance(mContext).getCurrentUsers(mobilePhone).get(0);
				mCamelUser.setUserAccount(mobilePhone);
				mCamelUser.setUserNike(HexadecimalConver.decode(nike));
			}else{
				mCamelUser = new CamelUser();
				mCamelUser.setUserAccount(mobilePhone);
				mCamelUser.setUserPassword("");
				mCamelUser.setUserPhonetype("android");
				mCamelUser.setUserLatitude(CustomApplcation.getInstance().getLatitude());
				mCamelUser.setUserLongitude(CustomApplcation.getInstance().getLongtitude());
				mCamelUser.setUserNike("爸爸");
				mCamelUser.setUserPhoneIdentify(CustomApplcation.getInstance().getIdentification());
			}
			
			if(DBHelper.getInstance(mContext).isUsersInfoSaved(mCamelUser.getUserAccount())){
				CustomApplcation.getInstance().setUsername(mCamelUser.getUserAccount());
				CustomApplcation.getInstance().setCurrentUser("1");
				return DBHelper.getInstance(mContext).updateToUsersInfoTable(mCamelUser) == 1? 0:1;
			}else{
				CustomApplcation.getInstance().setUsername(mCamelUser.getUserAccount());
				CustomApplcation.getInstance().setCurrentUser("1");
				DBHelper.getInstance(mContext).addToUsersInfoTable(mCamelUser);
				return 0;
			}
			
		}else if(result.equals("01")){ // 账号不存在
			return 2;
		}
		else if(result.equals("02")){  // 密码错误
			return 3;
		}
		return -1;
	}
	
	/**
	 * @ClassName: queryUserDveiceDataDecode
	 * @Description: 查询当前用户设备
	 * @instruct: 3A
	 * @return: order
	 */
	public static int queryUserDveiceDataDecode(String data){
		Log.i("查询当前用户设备", data);
		String[] str = new String[16];
		List<CamelDevice> camelDeviceList = new ArrayList<CamelDevice>();
		str[0] = data.substring(0, 2);
		if(Integer.valueOf(str[0]).intValue() == 0){
			return 0;
		}else if(str[0].equals("FF")){
			return -1;
		}
		for(int i = 0 ; i < Integer.valueOf(str[0]).intValue() ; i++){
			switch(i){
			case 0:
				CamelDevice camelDevice = new CamelDevice();
				str[2] = data.substring(2, 12).replaceFirst("^0+", ""); // 设备标识
				str[3] = data.substring(12, 32).replaceFirst("^0+", ""); // 设备昵称
				str[4] = data.substring(32, 34); // 设备模式
				camelDevice.setDeviceIdentify(String.valueOf(Long.parseLong(str[2],16)));
				camelDevice.setDeviceNike(HexadecimalConver.decode(str[3]));
				camelDevice.setDeviceSelect("false");
				camelDevice.setDeviceFrequency(str[4]);
//				Log.e("频率1", str[4]);
				camelDeviceList.add(camelDevice);
				break;
			case 1:
				CamelDevice camelDevice1 = new CamelDevice();
				str[5] = data.substring(34, 44).replaceFirst("^0+", ""); // 设备标识
				str[6] = data.substring(44, 64).replaceFirst("^0+", ""); // 设备昵称
				str[7] = data.substring(64, 66); // 设备模式
				camelDevice1.setDeviceIdentify(String.valueOf(Long.parseLong(str[5],16)));
				camelDevice1.setDeviceNike(HexadecimalConver.decode(str[6]));
				camelDevice1.setDeviceSelect("false");
				camelDevice1.setDeviceFrequency(str[7]);
//				Log.e("频率1", str[7]);
				camelDeviceList.add(camelDevice1);
				break;
			case 2:
				CamelDevice camelDevice2 = new CamelDevice();
				str[8] = data.substring(66, 76).replaceFirst("^0+", ""); // 设备标识
				str[9] = data.substring(76, 96).replaceFirst("^0+", ""); // 设备昵称
				str[10] = data.substring(96, 98); // 设备模式
				camelDevice2.setDeviceIdentify(String.valueOf(Long.parseLong(str[8],16)));
				camelDevice2.setDeviceNike(HexadecimalConver.decode(str[9]));
				camelDevice2.setDeviceSelect("false");
				camelDevice2.setDeviceFrequency(str[10]);
//				Log.e("频率1", str[10]);
				camelDeviceList.add(camelDevice2);
				break;
			case 3:
				CamelDevice camelDevice3 = new CamelDevice();
				str[11] = data.substring(98, 108).replaceFirst("^0+", ""); // 设备标识
				str[12] = data.substring(108, 128).replaceFirst("^0+", ""); // 设备昵称
				str[13] = data.substring(128, 130); // 设备模式
				camelDevice3.setDeviceIdentify(String.valueOf(Long.parseLong(str[11],16)));
				camelDevice3.setDeviceNike(HexadecimalConver.decode(str[12]));
				camelDevice3.setDeviceSelect("false");
				camelDevice3.setDeviceFrequency(str[13]);
//				Log.e("频率1", str[13]);
				camelDeviceList.add(camelDevice3);
				break;
			case 4:
				CamelDevice camelDevice4 = new CamelDevice();
				str[14] = data.substring(130, 140).replaceFirst("^0+", ""); // 设备标识
				str[15] = data.substring(140, 160).replaceFirst("^0+", ""); // 设备昵称
				str[16] = data.substring(160, 162); // 设备模式
				camelDevice4.setDeviceIdentify(String.valueOf(Long.parseLong(str[14],16)));
				camelDevice4.setDeviceNike(HexadecimalConver.decode(str[15]));
				camelDevice4.setDeviceSelect("false");
				camelDevice4.setDeviceFrequency(str[16]);
//				Log.e("频率1", str[16]);
				camelDeviceList.add(camelDevice4);
				break;
			}
		}
		CustomApplcation.getInstance().setContactList(camelDeviceList);
		/** 以下为判断本地的和服务获取的数据，是否存在差异*/
		// 获取相同的
		List<CamelDevice> lists = new ArrayList<CamelDevice>();
		// 获取数据库中的所有设备
		List<CamelDevice> existList = DBHelper.getInstance(mContext).getDevicesInfoList();
		// 判断 假如数据库中没有数据，那么获取的数据第一条为选中
		if(existList.size() == 0){
			lists.addAll(camelDeviceList);
			CamelDevice camelDevice = new CamelDevice();
			camelDevice.setDeviceIdentify(lists.get(0).getDeviceIdentify());
			camelDevice.setDeviceNike(lists.get(0).getDeviceNike());
			camelDevice.setDeviceSelect("true");
			camelDevice.setDeviceFrequency(lists.get(0).getDeviceFrequency());
			lists.remove(0);
			lists.add(0, camelDevice);		
		}else if(existList.size() >= camelDeviceList.size()){ // 数据库的数据大于获取的数据
			lists.addAll(camelDeviceList);
			for(int i = 0 ; i < camelDeviceList.size() ; i++){
				for(int j = 0 ; j < existList.size(); j++){
					// 提取两者相等的替换
					if(camelDeviceList.get(i).getDeviceIdentify().equals(existList.get(j).getDeviceIdentify())){
						lists.remove(i);
						existList.get(j).setDeviceFrequency(camelDeviceList.get(i).getDeviceFrequency());
						lists.add(i, existList.get(j));	
					}
				}
			}
		}else if(existList.size() < camelDeviceList.size()){ // 数据库的数据小于获取的数据
			lists.addAll(camelDeviceList);
			for(int i = 0 ; i < camelDeviceList.size() ; i++){
				for(int j = 0 ; j < existList.size() ; j++){
					if(camelDeviceList.get(i).getDeviceIdentify().equals(existList.get(j).getDeviceIdentify())){
						lists.remove(i);
						existList.get(j).setDeviceFrequency(camelDeviceList.get(i).getDeviceFrequency());
						lists.add(i, existList.get(j));
					}
				}
			}
		}
		// 清除数据库
		DBHelper.getInstance(mContext).clearDevicesInfo();
		// 插入数据
		for(int total = 0 ; total < lists.size() ; total++){
			CamelDevice mCamelDevice = new CamelDevice();
			mCamelDevice.setDeviceAvatar(lists.get(total).getDeviceAvatar());
			mCamelDevice.setDeviceNike(lists.get(total).getDeviceNike());
			mCamelDevice.setDeviceIdentify(lists.get(total).getDeviceIdentify());
			mCamelDevice.setDeviceSelect(lists.get(total).getDeviceSelect());
			mCamelDevice.setDeviceFrequency(lists.get(total).getDeviceFrequency());
			mCamelDevice.setDeviceLigature(lists.get(total).getDeviceLigature());
//			Log.e("频率2", lists.get(total).getDeviceFrequency());
//			Log.e("频率3", mCamelDevice.getDeviceFrequency());
			DBHelper.getInstance(mContext).addToDevicesInfoTable(mCamelDevice);
		}
		return 1;
 	}

	/**
	 * @ClassName: queryAdministratorDataDecode
	 * @Description: 查询设备是否存在管理员
	 * @instruct: 3B
	 * @return: order
	 */
	public static int queryAdministratorDataDecode(String data){
		Log.i("查询设备是否存在管理员", data);
		String Account = null;
		String result = data.substring(0, 2);
		if(result.equals("FF")){
			return -1;
		}else if(result.equals("00")){ 
			String type = data.substring(2, 4); 
			if(type.equals("01")){   // 有管理员 和管理员号码
				Account = data.substring(4, 14).replaceFirst("^0+", "");
				Config.DEVICE_ADMINISTRATOR = String.valueOf(Long.parseLong(Account,16));
				return 1;
			}else if(type.equals("02")){  // 无管理员
				if(DBHelper.getInstance(mContext).isDevicesInfoSaved(Config.DEVICE_IDENTIFY)){
					return 3;
				}else{
					return 2;
				}
			}
		}
		return -1;
	}
	
	/**
	 * @ClassName: addDeviceDataDecode
	 * @Description: 设备添加
	 * @instruct: 2A
	 * @return: order
	 */
	public static int addDeviceDataDecode(String data){
		Log.i("设备添加", data);
		String result = data.substring(0, 2);
		if(result.equals("FF")){
			result = data.substring(2, 4).replaceFirst("^0+", ""); 
			return Integer.valueOf(result).intValue();
		}else if(result.equals("00")){
			CamelDevice mCamelDevice = null;
			if(DBHelper.getInstance(mContext).isDevicesInfoSaved(Config.DEVICE_IDENTIFY)){
				return 9;
			}else{
				mCamelDevice = new CamelDevice();
				mCamelDevice.setDeviceNike("设备");
				mCamelDevice.setDeviceIdentify(Config.DEVICE_IDENTIFY);
				// 待检测
				List<CamelDevice> camelDevice = DBHelper.getInstance(mContext).getDevicesInfoList();
				if(camelDevice == null || camelDevice.size() == 0){
					mCamelDevice.setDeviceSelect("true");
				}else{
					mCamelDevice.setDeviceSelect("false");
				}
				DBHelper.getInstance(mContext).addToDevicesInfoTable(mCamelDevice);
				return 0;
			}
		}
		return -1;	
	}
	
	/**
	 * @ClassName: cancelDeviceDataDecode
	 * @Description: 取消关注
	 * @instruct: 3C
	 * @return: order
	 */
    public static int cancelDeviceDataDecode(String data){
    	Log.i("取消关注", data);
		String result = data.substring(0, 2);
		if(result.equals("FF")){
			return -1;
		}else if(result.equals("00")){
			return 0;
		}
		return -1;
    }
    
    /**
   	 * @ClassName: pushDataDecode
   	 * @Description: 推送
   	 * @instruct: 1A  4A
   	 * @return: order
   	 */
    public static int pushDeviceDataDecode(String data){
    	Log.i("推送", data);
    	String result = data.substring(0, 2);
		if(result.equals("FF")){
			return -1;
		}else {
			CamelDevicePush camelDeviceState = null;
			if(CustomApplcation.getInstance().getCurrentCamelDevice() == null){
				return -1;
			}
			camelDeviceState = new CamelDevicePush();
			camelDeviceState.setDevicePushIdentify(String.valueOf(Long.parseLong(data.substring(0, 10).replaceFirst("^0+", ""),16)));
//			Log.e("推送", camelDeviceState.getDevicePushIdentify());
			camelDeviceState.setDevicePushWorkState(data.substring(12, 14)); //设备工作状态
//			Log.e("设备工作状态", camelDeviceState.getDevicePushWorkState());
			camelDeviceState.setDevicePushElectricMode(data.substring(14, 16)); // 电池状态(1:充电中  2:已充满 3:未充电)
//			Log.e("电池状态", camelDeviceState.getDevicePushElectricMode());
			camelDeviceState.setDevicePushElectric(String.valueOf(Integer.parseInt(data.substring(16, 20).replaceFirst("^0+", ""),16))); // 电量
//			Log.e("电量", camelDeviceState.getDevicePushElectric());
			camelDeviceState.setDevicePushSignal(String.valueOf(Integer.parseInt(data.substring(20, 22),16))); // 信号
//			Log.e("信号",camelDeviceState.getDevicePushSignal());
			camelDeviceState.setDevicePushUrgency(data.substring(22, 24)); // 报警
			if(camelDeviceState.getDevicePushUrgency().equals("01")){ // 低电报警
				CustomApplcation.getInstance().notificationNotify(1,"电量过低，请充电");
			}else if(camelDeviceState.getDevicePushUrgency().equals("02")){ // sos报警
				CustomApplcation.getInstance().notificationNotify(2,"设备SOS报警");
			}else if(camelDeviceState.getDevicePushUrgency().equals("03")){ // ++报警
				CustomApplcation.getInstance().notificationNotify(3,"电量过低，设备SOS报警");
			}
//			Log.e("报警", camelDeviceState.getDevicePushUrgency());
			if(data.substring(24, 32).equals("00000000")){
				camelDeviceState.setDevicePushStep("0");
			}else{
				camelDeviceState.setDevicePushStep(String.valueOf(Long.parseLong(data.substring(24, 32).replaceFirst("^0+", ""),16))); // 步数
			}
//			Log.e("步数", camelDeviceState.getDevicePushStep());
			camelDeviceState.setDevicePushLocaMode(data.substring(32, 34)); // 定位模式（设备WIFI ， 基站 ， GPS）
//			Log.e("设备定位模式", camelDeviceState.getDevicePushLocaMode());
            if(data.substring(34, 42).equals("00000000") && data.substring(42, 50).equals("00000000")){
            	return 0;
			}
			String longitude = deLatLonFormat(data.substring(34, 42).replaceFirst("^0+", ""));
			String latitude = deLatLonFormat(data.substring(42, 50).replaceFirst("^0+", ""));
			camelDeviceState.setDevicePushLatitude(latitude); // 纬度
			camelDeviceState.setDevicePushLongtitude(longitude); // 经度
//			Log.e("经纬度", camelDeviceState.getDevicePushLatitude() +"  "+camelDeviceState.getDevicePushLongtitude());
			// 经纬度处理
			if (DBHelper.getInstance(mContext).isRelZoneSaved(camelDeviceState.getDevicePushIdentify())) {
				List<RelSafetyZone> relSafetyZone = DBHelper.getInstance(mContext).getRelZoneIdentify(camelDeviceState.getDevicePushIdentify());
				for (int i = 0; i < relSafetyZone.size(); i++) {
					RelSafetyZone mRelSafetyZone = relSafetyZone.get(i);
					double distance = LatLngDistanceUtils.DistanceOfTwoPoints(
							Double.valueOf(camelDeviceState.getDevicePushLatitude()).doubleValue(), 
							Double.valueOf(camelDeviceState.getDevicePushLongtitude()).doubleValue(), 
							Double.valueOf(mRelSafetyZone.getRelZoneLatitude()).doubleValue(), 
							Double.valueOf(mRelSafetyZone.getRelZoneLongtitude()).doubleValue());
					if(distance < Double.valueOf(mRelSafetyZone.getRelZoneRadius()).doubleValue()){
						CustomApplcation.getInstance().notificationNotify(4,"  在安全区 :" +mRelSafetyZone.getRelZoneName());
					}
				}
			}
			
			// 时间处理  获取系统时间
			String year = String.valueOf(Integer.parseInt(data.substring(50, 52)+"",16));
			String month = paddingdata(String.valueOf(Integer.parseInt(data.substring(52, 54)+"",16)),2);
			String date = paddingdata(String.valueOf(Integer.parseInt(data.substring(54, 56)+"",16)),2);
			String hour = paddingdata(String.valueOf(Integer.parseInt(data.substring(56, 58)+"",16)),2);
			String minute = paddingdata(String.valueOf(Integer.parseInt(data.substring(58, 60)+"",16)),2);
			String second = paddingdata(String.valueOf(Integer.parseInt(data.substring(60, 62)+"",16)),2);
			camelDeviceState.setDevicePushCreated(20+year+"-"+month+"-"+date+" "+hour+":"+minute+":"+second); // 创建时间
			camelDeviceState.setDevicePushUpdated(20+year+"-"+month+"-"+date);
			Log.e("时间", camelDeviceState.getDevicePushCreated());
			// 添加推送的数据
			DBHelper.getInstance(mContext).addToDevicePushInfoTable(camelDeviceState);
			// 保存设备的定位频率
			if(DBHelper.getInstance(mContext).isDevicesInfoSaved(CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify())){
				CamelDevice camel = DBHelper.getInstance(mContext).getAssignDevices(CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify()).get(0);
				camel.setDeviceFrequency(data.substring(10, 12));
//				Log.e("设备定位频率", camel.getDeviceFrequency());
				DBHelper.getInstance(mContext).updateToDevicesInfoTable(camel);	
			}
			return 0;
		}
    }
    
    /**
	 * @ClassName: selectorDeviceDataDecode
	 * @Description: 设备选择
	 * @instruct: 1B
	 * @return: order
	 */
	public static int selectorDeviceDataDecode(String data){
		Log.i("设备选择", data);
		String result = data.substring(0, 2);
		if(result.equals("FF")){
			return -1;
		}else if(result.equals("00")){
			return 0;
		}
		return -1;
	}
    
    /**
   	 * @ClassName: heartbeatRateDataDecode
   	 * @Description: 心跳
   	 * @instruct: 1C
   	 * @return: order
   	 */
    public static int heartbeatRateDataDecode(String data){
    	Log.i("心跳", data);
    	String result = data.substring(0, 2);
		if(result.equals("FF")){
			return -1;
		}else if(result.equals("00")){
			return 0;
		}
		return -1;
    }
    
    
    /**
   	 * @ClassName: locationModeDataDecode
   	 * @Description: 当前设备定位模式
   	 * @instruct: 1D
   	 * @return: order
   	 */
    public static int locationModeDataDecode(String data){
    	Log.i("当前设备定位模式", data);
    	String result = data.substring(0, 2);
    	if(result.equals("FF")){
			return -1;
		}else {
			if(CustomApplcation.getInstance().getCurrentCamelDevice() == null){
				return -1;
			}
		    String id = CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify();
			String getId = String.valueOf(Long.parseLong(data.substring(0, 10).replaceFirst("^0+", ""),16));
		    if(getId.equals(id)){
		    	String type = data.substring(10, 12);
		    	if(type.equals("05") || type.equals("0A") || type.equals("02")){
				    if(DBHelper.getInstance(mContext).isDevicesInfoSaved(id)){
					    CamelDevice camel = DBHelper.getInstance(mContext).getAssignDevices(CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify()).get(0);
//					    Log.e("result", type);
					    camel.setDeviceFrequency(type);
//					    Log.e("TAG", camel.getDeviceFrequency());
					    return DBHelper.getInstance(mContext).updateToDevicesInfoTable(camel) != -1 ? 0 :1;
				    }
				}
		    }
			return -1;	
		}
    }
    
    /**
   	 * @ClassName: realTimeLocationDataDecode
   	 * @Description: 当前设备位置(实时定位)
   	 * @instruct: 1E
   	 * @return: order
   	 */
    public static int realTimeLocationDataDecode(String data){
    	Log.i("当前设备位置", data);
    	String result = data.substring(0, 2);
		if(result.equals("00")){
			return -1;
		}else {
			String latitude = deLatLonFormat(data.substring(10, 18).replaceFirst("^0+", ""));
	    	String longitude = deLatLonFormat(data.substring(2, 10).replaceFirst("^0+", ""));
	    	if(DBHelper.getInstance(mContext).isDevicesInfoSaved(CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify())){
	    		CamelDevice camel = DBHelper.getInstance(mContext).getAssignDevices(CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify()).get(0);
	    		camel.setDeviceLocationMode(result);
	    		camel.setDeviceLatitude(latitude);
	    		camel.setDeviceLongitude(longitude);
	    		// 更新设备中的经纬度
	    		return DBHelper.getInstance(mContext).updateToDevicesInfoTable(camel) != -1? 0:1;
	    	}
//			if(result.equals("01")){				
//			}
			return -1;
	    }
    }
    
    /**
	 * @ClassName: queryFamilyDataDecode
	 * @Description: 获取设备关联的用户(家庭成员)
	 * @instruct: 3D
	 * @return: order
	 */
    public static int queryFamilyDataDecode(String data){
    	Log.i("获取设备关联的用户", data);
		String result = data.substring(0, 2);
		List<DataRelFamily> dataRel = new ArrayList<DataRelFamily>();
		if(result.equals("FF")){
			return -1;
		}else {
			DataRelFamily mDataRelFamily;
			for(int i = 0 ; i < Integer.valueOf(result).intValue() ; i++){
				switch(i){
				case 0:
					mDataRelFamily = new DataRelFamily();
					mDataRelFamily.setDataRelSubject(CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify());
					mDataRelFamily.setDataRelBranch(String.valueOf(Long.parseLong(data.substring(2, 12).replaceFirst("^0+", ""),16)));
					mDataRelFamily.setDataRelAccount(String.valueOf(Long.parseLong(data.substring(2, 12).replaceFirst("^0+", ""),16)));
					mDataRelFamily.setDataRelNick(HexadecimalConver.decode(data.substring(12, 52).replaceFirst("^0+", "")));
					mDataRelFamily.setDataRelAdministrator(data.substring(52, 54));
					dataRel.add(mDataRelFamily);
					break;
				case 1:
					mDataRelFamily = new DataRelFamily();
					mDataRelFamily.setDataRelSubject(CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify());
					mDataRelFamily.setDataRelBranch(String.valueOf(Long.parseLong(data.substring(54, 64).replaceFirst("^0+", ""),16)));
					mDataRelFamily.setDataRelAccount(String.valueOf(Long.parseLong(data.substring(54, 64).replaceFirst("^0+", ""),16)));
					mDataRelFamily.setDataRelNick(HexadecimalConver.decode(data.substring(64, 104).replaceFirst("^0+", "")));
					mDataRelFamily.setDataRelAdministrator(data.substring(104, 106));
					dataRel.add(mDataRelFamily);
					break;
				case 2:
					mDataRelFamily = new DataRelFamily();
					mDataRelFamily.setDataRelSubject(CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify());
					mDataRelFamily.setDataRelBranch(String.valueOf(Long.parseLong(data.substring(106, 116).replaceFirst("^0+", ""),16)));
					mDataRelFamily.setDataRelAccount(String.valueOf(Long.parseLong(data.substring(106, 116).replaceFirst("^0+", ""),16)));
					mDataRelFamily.setDataRelNick(HexadecimalConver.decode(data.substring(116, 156).replaceFirst("^0+", "")));
					mDataRelFamily.setDataRelAdministrator(data.substring(156, 158));
					dataRel.add(mDataRelFamily);
					break;
				case 3:
					mDataRelFamily = new DataRelFamily();
					mDataRelFamily.setDataRelSubject(CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify());
					mDataRelFamily.setDataRelBranch(String.valueOf(Long.parseLong(data.substring(158, 168).replaceFirst("^0+", ""),16)));
					mDataRelFamily.setDataRelAccount(String.valueOf(Long.parseLong(data.substring(158, 168).replaceFirst("^0+", ""),16)));
					mDataRelFamily.setDataRelNick(HexadecimalConver.decode(data.substring(168, 208).replaceFirst("^0+", "")));
					mDataRelFamily.setDataRelAdministrator(data.substring(208, 210));
					dataRel.add(mDataRelFamily);
					break;
				case 4:
					mDataRelFamily = new DataRelFamily();
					mDataRelFamily.setDataRelSubject(CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify());
					mDataRelFamily.setDataRelBranch(String.valueOf(Long.parseLong(data.substring(210, 220).replaceFirst("^0+", ""),16)));
					mDataRelFamily.setDataRelAccount(String.valueOf(Long.parseLong(data.substring(210, 220).replaceFirst("^0+", ""),16)));
					mDataRelFamily.setDataRelNick(HexadecimalConver.decode(data.substring(220, 260).replaceFirst("^0+", "")));
					mDataRelFamily.setDataRelAdministrator(data.substring(260, 262));
					dataRel.add(mDataRelFamily);
					break;
				}
			}
			// 清除当前的数据
			if(DBHelper.getInstance(mContext).isDataRelFamilySaved(CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify())){
				DBHelper.getInstance(mContext).deleteDataRelFamilyList(CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify());
			}
		    for(int i = 0 ; i< dataRel.size() ; i++){
		    	DBHelper.getInstance(mContext).addToDataRelFamilyTable(dataRel.get(i));
		    }
		    return 0;
		}
    }
    
    /**
	 * @ClassName: querydDeviceDataDecode
	 * @Description: 查询设备历史位置信息信息  
	 * @instruct: 3E
	 * @return: order
	 */
    @SuppressWarnings("null")
	public static int querydDeviceDataDecode(String data){
    	Log.i("查询设备的状态信息", data);
		String result = data.substring(0, 2);
		if(result.equals("FF")){
			return -1;
		}else {
			if(CustomApplcation.getInstance().getCurrentCamelDevice() ==null){
				return -1;
			}
			String id = CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify();
			CamelDeviceHistory camelDeviceState = null;
			List<CamelDeviceHistory> deviceStateList = new ArrayList<CamelDeviceHistory>();
			String page = data.substring(0, 2).replaceFirst("^0+", "");
			int total = Integer.valueOf(Integer.parseInt(data.substring(2, 4)+"",16));
			if(data.substring(2, 4).equals("00")){
                return 1;
            }
			int start = 4;
			int end = 0;
			String year = null , month = null , date = null, hour, minute, second;
			String step;
			if(data.substring(data.length()-8, data.length()).equals("00000000") && data.substring(data.length()-8, data.length()).equals("00000000")){
				step =  "0";
			}else{
				step = String.valueOf(Long.parseLong(data.substring(data.length()-8, data.length()).replaceFirst("^0+", ""),16));
			}
			
			for(int i = 0 ; i < total ; i++){
				end = start + 28;
				camelDeviceState = new CamelDeviceHistory();
			    String getData = data.substring(start, end);
			    camelDeviceState.setDeviceHistoryIdentify(id);
                camelDeviceState.setDeviceHistoryPage(page);// 页数
                camelDeviceState.setDeviceHistoryStep(step);
                camelDeviceState.setDeviceHistoryLongtitude(deLatLonFormat(getData.substring(0, 8).replaceFirst("^0+", ""))); // 经度
                camelDeviceState.setDeviceHistoryLatitude(deLatLonFormat(getData.substring(8, 16).replaceFirst("^0+", ""))); // 纬度		
                // 时间处理  获取系统时间
                year = String.valueOf(Integer.parseInt(getData.substring(16, 18)+"",16));
                month =paddingdata(String.valueOf(Integer.parseInt(getData.substring(18, 20)+"",16)),2);
                date = paddingdata(String.valueOf(Integer.parseInt(getData.substring(20, 22)+"",16)),2);
                hour = paddingdata(String.valueOf(Integer.parseInt(getData.substring(22, 24)+"",16)),2);
                minute = paddingdata(String.valueOf(Integer.parseInt(getData.substring(24, 26)+"",16)),2);
                second = paddingdata(String.valueOf(Integer.parseInt(getData.substring(26, 28)+"",16)),2);
                camelDeviceState.setDeviceHistoryCreated(20+year+"-"+month+"-"+date); // 创建时间
                camelDeviceState.setDeviceHistoryTermination(hour+":"+minute+":"+second);
                deviceStateList.add(camelDeviceState);
//              Log.e("TAG", camelDeviceState.getDeviceHistoryCreated() +" "+camelDeviceState.getDeviceHistoryTermination());
                start += 28;
			}
			// 删除
			if(DBHelper.getInstance(mContext).isDeviceHistorySaved(id)){
				// 获取id下的所有信息
				List<CamelDeviceHistory> mAllListItems = DBHelper.getInstance(mContext).getDeviceHistoryMessage(id , page , deviceStateList.get(0).getDeviceHistoryCreated());
//				Log.e("TAG0", String.valueOf(mAllListItems.size()));
				if(mAllListItems != null || mAllListItems.size() != 0){
					// 删除当天
					for(int i = 0 ; i < mAllListItems.size() ;i++){
						CamelDeviceHistory state = mAllListItems.get(i);
						DBHelper.getInstance(mContext).deleteDeviceHistoryList(state.getId());
					}
				}
			}
			for(int i = 0 ; i < deviceStateList.size() ; i++){
				DBHelper.getInstance(mContext).addToDeviceHistoryInfoTable(deviceStateList.get(i));
			}
			return 0;
		}
    }
    
    /**
   	 * @ClassName: selectorDeviceDataDecode
   	 * @Description: 设备选择
   	 * @instruct: 3F
   	 * @return: order
   	 */
   	public static int anewSetPwdDataDecode(String data){
   		Log.i("设备选择", data);
   		String result = data.substring(0, 2);
   		if(result.equals("FF")){
   			return -1;
   		}else if(result.equals("00")){
   			return 0;
   		}
   		return -1;
   	}
	
   	/**
   	 * @ClassName: queryLengthDataDecode
   	 * @Description: 设备选择
   	 * @instruct: 4B
   	 * @return: order
   	 */
   	public static int queryLengthDataDecode(String data){
   		String result = data.substring(0, 2);
   		if(result.equals("FF")){
   			return -1;
   		}else {
			if(CustomApplcation.getInstance().getCurrentCamelDevice() == null){
				return -1;
			}
			String id = CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify();
			String netId = String.valueOf(Long.parseLong(data.substring(0, 10).replaceFirst("^0+", ""),16));
   		    if(id.equals(netId)){
   		    	Config.HISTORY_PAGE_LENGTH = Integer.parseInt(data.substring(22, 26).replaceFirst("^0+", ""),16);
   		    	return 0;
   		    }
   			return -1;
   		}
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
	 *  接收经纬度格式反编码
	 */
	public static String deLatLonFormat(String mlatitude){
		String latitude = Integer.valueOf(mlatitude,16).toString();
		String first= latitude.substring(0, latitude.length()-6);
		String second=latitude.substring(latitude.length()-6, latitude.length());
		return first+"."+second;
	}  
}

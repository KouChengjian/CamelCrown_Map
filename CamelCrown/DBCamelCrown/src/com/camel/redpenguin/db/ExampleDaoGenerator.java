package com.camel.redpenguin.db;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class ExampleDaoGenerator {

	public static void main(String[] args) throws Exception {
		Schema schema = new Schema(1, "com.camel.redpenguin.greendao");

		addUser(schema);
		addDevice(schema);
		addDeviceState(schema);
		addDataRelevance( schema);
		addDataRelevanceSafetyZone(schema);
		addDeviceHistory(schema);
		//addCustomerOrder(schema);

		new DaoGenerator().generateAll(schema, "../DBCamelCrown/src-gen");
	}
	
	private static void addUser(Schema schema) {
		Entity note = schema.addEntity("CamelUser");
		note.addIdProperty();
		note.addStringProperty("userAccount").notNull(); // 用户
		note.addStringProperty("userPassword").notNull();// 密码
		note.addStringProperty("userPhonetype"); // 设备类型
		note.addStringProperty("userLatitude");  // 纬度
		note.addStringProperty("userLongitude"); // 经度
		note.addStringProperty("userNike");      // 昵称
		note.addStringProperty("userPhoneIdentify");  // 手机标识
		note.addStringProperty("userAdministrator");  // 管理
		note.addStringProperty("userUpdateDate");  // 更新时间
		note.addStringProperty("userCreateDate");  // 创建时间
	}

	private static void addDevice(Schema schema) {
		Entity note = schema.addEntity("CamelDevice");
		note.addIdProperty();
		note.addStringProperty("deviceNike");  // 昵称
		note.addStringProperty("deviceAvatar"); // 头像
		//note.addStringProperty("deviceBirthday"); // 生日
		//note.addStringProperty("deviceSex");  // 性别
		//note.addStringProperty("deviceStature");  // 身高
		//note.addStringProperty("deviceWeight"); // 体重
		//note.addStringProperty("deviceGrade");  // 年级
		note.addStringProperty("deviceLatitude");  // 纬度
		note.addStringProperty("deviceLongitude"); // 经度
		note.addStringProperty("deviceFrequency"); // 设备定位频率 （安全，省电，紧急）
		note.addStringProperty("deviceLocationMode"); // 设备定位模式（设备WIFI ， 基站 ， GPS）
		note.addStringProperty("deviceIdentify"); // 设备标识
		note.addStringProperty("deviceSelect"); // 设备选择
		note.addStringProperty("deviceLigature"); // 是否连线
		note.addStringProperty("deviceUpdateDate"); // 创建时间
		note.addStringProperty("deviceCreateDate"); // 更新时间
	}
	
	/** 推送 */
	private static void addDeviceState(Schema schema) {
		Entity note = schema.addEntity("CamelDevicePush");
		note.addIdProperty();
		note.addStringProperty("devicePushWorkState");// 设备工作状态(正常，待机)
		note.addStringProperty("devicePushElectricMode");// 电池状态(充电，电量过低，满格)
		note.addStringProperty("devicePushElectric");// 电量
		note.addStringProperty("devicePushSignal"); // 信号
		note.addStringProperty("devicePushUrgency");// 报警
		note.addStringProperty("devicePushStep");// 步数
		note.addStringProperty("devicePushLocaMode");// 定位模式（设备WIFI ， 基站 ， GPS）
		note.addStringProperty("devicePushLatitude"); // 纬度
		note.addStringProperty("devicePushLongtitude"); // 经度
		note.addStringProperty("devicePushIdentify");// 设备标识
		note.addStringProperty("devicePushCreated"); // 创建时间
		note.addStringProperty("devicePushUpdated"); // 创建时间
	}
	/** 历史记录 */
	private static void addDeviceHistory(Schema schema) {
		Entity note = schema.addEntity("CamelDeviceHistory");
		note.addIdProperty();
		note.addStringProperty("deviceHistoryType"); // 头像(只在列表)
		note.addStringProperty("deviceHistoryLatitude"); // 纬度
		note.addStringProperty("deviceHistoryLongtitude"); // 经度
		note.addStringProperty("deviceHistoryIdentify");// 设备标识
		note.addStringProperty("deviceHistoryPage");// 设备标识
		note.addStringProperty("deviceHistoryStep");// 设备标识
		note.addStringProperty("deviceHistoryCreated"); // 创建时间
		note.addStringProperty("deviceHistoryTermination"); // 终止时间(只在列表)
		note.addStringProperty("deviceHistoryAllTime"); // 全长时间(只在列表)
	}
	
	/** 数据关联表格 -家庭成员*/
	private static void addDataRelevance(Schema schema) {
		Entity note = schema.addEntity("DataRelFamily");
		note.addIdProperty();
		note.addStringProperty("dataRelSubject");// 主体
		note.addStringProperty("dataRelBranch");// 分支
		note.addStringProperty("dataRelNick");// 昵称
		note.addStringProperty("dataRelAccount");// 账号
		note.addStringProperty("dataRelAdministrator");// 是否为管理员
	}
	
	/** 数据关联表格 -安全区域*/
	private static void addDataRelevanceSafetyZone(Schema schema) {
		Entity note = schema.addEntity("RelSafetyZone");
		note.addIdProperty();
		note.addStringProperty("relZoneIdentify"); // 设备的id
		//note.addStringProperty("relZoneIcon");  // 设备
		note.addStringProperty("relZoneName");  // 名称
		note.addStringProperty("relZoneAddr");  // 地址
		note.addStringProperty("relZoneLatitude"); // 纬度
		note.addStringProperty("relZoneLongtitude"); // 经度
		note.addStringProperty("relZoneRadius"); // 半径
		note.addStringProperty("relZoneCreated"); // 创建时间
	}
	
	private static void addCustomerOrder(Schema schema) {
		Entity customer = schema.addEntity("Customer");
		customer.addIdProperty();
		customer.addStringProperty("name").notNull();

		Entity order = schema.addEntity("Order");
		order.setTableName("ORDERS"); // "ORDER" is a reserved keyword
		order.addIdProperty();
		
		Property orderDate = order.addDateProperty("date").getProperty();
		Property customerId = order.addLongProperty("customerId").notNull().getProperty();
		order.addToOne(customer, customerId);

		ToMany customerToOrders = customer.addToMany(order, customerId);
		customerToOrders.setName("orders");
		customerToOrders.orderAsc(orderDate);
	}
	
}

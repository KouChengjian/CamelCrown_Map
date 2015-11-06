package com.camel.redpenguin.listener;


/**
 * @ClassName: EventListener
 * @Description: 监听数据返回处理事件
 * @author: kcj
 * @date: 2015-3-13
 */
public class EventListener {

	// 回调
	public static SaveListener saveListener = null;
	public static FindListener findListener = null;
	
	public static HeartbeatHzListener heartbeatHzListener = null;
	public static FindFamilyListener findFamilyListener = null;
	public static PushListener pushListener = null;
	public static FindStateListener findStateListener = null;
	public static FindNewMessageListener findNewMessageListener = null;
}

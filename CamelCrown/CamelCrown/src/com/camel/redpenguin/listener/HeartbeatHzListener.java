package com.camel.redpenguin.listener;

/**
 * @ClassName: HeartbeatHzListener
 * @Description: 心跳频率监听
 * @author kcj
 * @date 
 */
public abstract interface HeartbeatHzListener {
	public abstract void onSuccess();
	public abstract void onFailure(int paramInt, String paramString);
}

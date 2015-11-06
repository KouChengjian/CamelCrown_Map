package com.camel.redpenguin.listener;

/**
 * @ClassName: PushListener
 * @Description: 消息推送
 * @author kcj
 * @date 
 */
public abstract interface PushListener {
	public abstract void onSuccess();
	public abstract void onFailure(int paramInt, String paramString);
}

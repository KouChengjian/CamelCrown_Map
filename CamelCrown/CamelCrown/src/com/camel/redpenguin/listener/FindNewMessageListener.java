package com.camel.redpenguin.listener;


/**
 * 
 * @ClassName: FindNewMessageListener
 * @Description: TODO
 * @author smile
 * @date 
 * @param <E>
 */
public abstract interface FindNewMessageListener {
	public abstract void onSuccess(); 
	public abstract void onFailure(int paramInt, String paramString);
}

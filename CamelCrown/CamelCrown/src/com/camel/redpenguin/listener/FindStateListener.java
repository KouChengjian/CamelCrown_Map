package com.camel.redpenguin.listener;


/**
 * 
 * @ClassName: FindStateListener
 * @Description: TODO
 * @author 
 * @date 
 * @param <E>
 */
public abstract interface FindStateListener {
	public abstract void onSuccess(); 
	public abstract void onFailure(int paramInt, String paramString); 
}

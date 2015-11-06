package com.camel.redpenguin.listener;

/**
 * 
 * @ClassName: FindFamilyListener
 * @Description: TODO
 * @author smile
 * @date 
 * @param <E>
 */
public abstract interface  FindFamilyListener {
	public abstract void onSuccess(); 
	public abstract void onFailure(int paramInt, String paramString); 
}

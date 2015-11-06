package com.camel.redpenguin.listener;

import java.util.List;

import com.camel.redpenguin.greendao.CamelDevice;


/**
 * 基础的适配器
 * @ClassName: BaseListAdapter
 * @Description: TODO
 * @author smile
 * @date 2014-6-19 上午11:04:01
 * @param <E>
 */

public interface FindListener { 
	
	public abstract void onSuccess(List<CamelDevice> mCamelDevice); 
	public abstract void onFailure(int paramInt, String paramString); 
	
}

/**  
public abstract class FindListener<T> {
	
	public abstract void onSuccess(List<T> paramList);

	public abstract void onError(int paramInt, String paramString);
	public void onStart() {
	}
	public void onFinish() {
	}
}*/

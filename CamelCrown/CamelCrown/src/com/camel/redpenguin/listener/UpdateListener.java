package com.camel.redpenguin.listener;

/**
 * @ClassName: UpdateSuccee
 * @Description: 更新数据
 * @author kcj
 * @date 
 */
public abstract interface UpdateListener {
	public abstract void onSuccess();
	public abstract void onFailure(int paramInt, String paramString);
}

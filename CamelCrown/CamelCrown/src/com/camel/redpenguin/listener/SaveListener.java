package com.camel.redpenguin.listener;

/**
 * @ClassName: SaveListener
 * @Description: 保存-登入 注册
 * @author kcj
 * @date 
 */
public abstract interface SaveListener {
	public abstract void onSuccess();
	public abstract void onFailure(int paramInt, String paramString);
}

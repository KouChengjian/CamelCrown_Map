package com.camel.redpenguin.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import com.camel.redpenguin.R;
import com.camel.redpenguin.bean.OfflineItem;
import com.camel.redpenguin.ui.fragment.OfflineAllFragment;
import com.camel.redpenguin.ui.fragment.OfflineDownFragment;
import com.camel.redpenguin.view.HeaderLayout.onLeftImageButtonClickListener;
import com.camel.redpenguin.view.HeaderLayout.onLeftMiddleClickListener;
import com.camel.redpenguin.view.HeaderLayout.onRightMiddleClickListener;

/**
 * @ClassName: OfflineMapActivity
 * @Description: 离线地图
 * @author: kcj
 * @date: 
 */
public class OfflineMapActivity extends BaseActivity{
	
	private Fragment[] fragments;
	OfflineAllFragment offlineAllFragment;
	OfflineDownFragment offlineDownFragment;
	static OfflineMapActivity mInstance;
	
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_offline);
		mInstance = this;
		/** header */
		initTopBarForMiddleContainerLeft(R.drawable.base_action_bar_back_bg_selector,
				new onLeftImageButtonClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						finish();
					}
				}, "城市列表",new onLeftMiddleClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						selectLeftMiddle();
					}
				}, "下载管理",new onRightMiddleClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						selectRightMiddle();
					}
				});
		/** 初始化fragment */
		offlineAllFragment = new OfflineAllFragment();
		offlineDownFragment = new OfflineDownFragment();
		 fragments = new Fragment[] { offlineAllFragment, offlineDownFragment };
		// 添加显示第一个fragment
		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_offline_container, offlineAllFragment)
				.add(R.id.fragment_offline_container, offlineDownFragment)
				.hide(offlineDownFragment).show(offlineAllFragment).commit();
		/** 初始化百度离线信息 */
		
	}
	
	public static OfflineMapActivity getInstance() {
		return mInstance;
	}
	
	public void selectLeftMiddle(){
		setSelectLeftMiddle();
		FragmentTransaction trx = getSupportFragmentManager()
				.beginTransaction();
		trx.hide(fragments[1]);
		if (!fragments[0].isAdded()) {
			trx.add(R.id.fragment_offline_container, fragments[0]);
		}
		trx.show(fragments[0]).commit();
	}
    public void selectRightMiddle(){
    	setSelectRightMiddle();
		FragmentTransaction trx = getSupportFragmentManager()
				.beginTransaction();
		trx.hide(fragments[0]);
		if (!fragments[1].isAdded()) {
			trx.add(R.id.fragment_offline_container, fragments[1]);
		}
		trx.show(fragments[1]).commit();
	}
    
    /** 全局变量 */
    OfflineItem item;
    public void setOfflineItem (OfflineItem item){
    	this.item = item;
    }
    public OfflineItem getOfflineItem (){
    	return item;
    }
}

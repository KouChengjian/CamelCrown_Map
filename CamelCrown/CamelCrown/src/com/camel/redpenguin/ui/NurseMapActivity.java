package com.camel.redpenguin.ui;

import android.os.Bundle;
import android.view.View;

import com.camel.redpenguin.R;
import com.camel.redpenguin.view.HeaderLayout.onRightImageButtonClickListener;

/**
 * @ClassName: WatchMapActivity
 * @Description: 随身守护界面显示
 * @author kcj
 * @date
 */
public class NurseMapActivity extends BaseActivity{

	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nursemap);
		/** header */
		initTopBarForBoth("随身看护", R.drawable.base_action_bar_more_bg_selector,
				new onRightImageButtonClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
		
	}
	
}

package com.camel.redpenguin.ui;

import android.os.Bundle;

import com.camel.redpenguin.R;

/**
 * @ClassName: AboutActivity
 * @Description: 关于
 * @author: KCJ
 * @date: 2015-4
 */
public class AboutActivity extends BaseActivity{

	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		/** header */
		initTopBarForLeft("关于");
	}
	
}

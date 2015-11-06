package com.camel.redpenguin.ui;

import cn.smssdk.SMSSDK;

import com.camel.redpenguin.R;
import com.camel.redpenguin.view.HandyTextView;
import com.camel.redpenguin.view.HeaderLayout;
import com.camel.redpenguin.view.HeaderLayout.HeaderStyle;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;

/**
 * @ClassName: FindPwdTabsActivity
 * @Description: 忘记密码 设置界面
 * @author kcj
 * @date
 */
@SuppressWarnings("deprecation")
public class FindPwdTabsActivity extends TabActivity{
	private TabHost mTabHost;
	protected HeaderLayout mHeaderLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_findpwdtabs);
		/** 初始化header */
		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
		mHeaderLayout.setDefaultTitle("找回密码");
		/** initViews */
		mTabHost = getTabHost();
		/** initTabs */
		LayoutInflater inflater = LayoutInflater.from(FindPwdTabsActivity.this);
		
		View phoneView = inflater.inflate(
				R.layout.common_tabbar_item_lightblue, null);
		((HandyTextView) phoneView.findViewById(R.id.tabbar_item_htv_label))
				.setText("手机号码");
		TabHost.TabSpec phoneTabSpec = mTabHost.newTabSpec(FindPwdPhoneActivity.class.getName()).setIndicator(phoneView);
		phoneTabSpec.setContent(new Intent(FindPwdTabsActivity.this,FindPwdPhoneActivity.class));
		mTabHost.addTab(phoneTabSpec);

		/**  
		View emailView = inflater.inflate(
				R.layout.common_tabbar_item_lightblue, null);
		((HandyTextView) emailView.findViewById(R.id.tabbar_item_htv_label))
				.setText("电子邮箱");
		emailView.findViewById(R.id.tabbar_item_ligthblue_driver_left)
				.setVisibility(View.VISIBLE);
		TabHost.TabSpec emailTabSpec = mTabHost.newTabSpec(
				FindPwdEmailActivity.class.getName()).setIndicator(emailView);
		emailTabSpec.setContent(new Intent(FindPwdTabsActivity.this,
				FindPwdEmailActivity.class));
		mTabHost.addTab(emailTabSpec);*/
	}
	
	

}

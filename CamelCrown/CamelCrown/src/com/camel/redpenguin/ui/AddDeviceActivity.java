package com.camel.redpenguin.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.camel.redpenguin.R;
import com.camel.redpenguin.config.Config;
import com.camel.redpenguin.config.SocketInit;
import com.camel.redpenguin.listener.SaveListener;


/**
 * @ClassName: AddDeviceActivity
 * @Description: 设备添加
 * @author: KCJ
 * @date:
 */
public class AddDeviceActivity extends BaseActivity{

	private TextView tvAgain;
	private String deviceidentify = null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adddevice);
		initTopBarForLeft("设备添加");
		Intent intent = getIntent();
		deviceidentify = intent.getStringExtra("deviceidentify");
		initViews();
		initEvents();
		initDatas();
	}
	
	public void initViews(){
		tvAgain = (TextView)findViewById(R.id.tv_add_device_again);
	}
	
    public void initEvents(){
    	tvAgain.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				queryAdministrator();
			}
		});
    }
    
    public void initDatas(){
    	if(TextUtils.isEmpty(deviceidentify)){
			ShowToast("无序列号");
			return;
		}
		Config.DEVICE_IDENTIFY = deviceidentify;
		queryAdministrator();
    }
	
	/** 判断是否为管理员 */
	public void queryAdministrator(){
		showLoadingDialog("正在与设备匹配中，其耐心等待...");
		SocketInit.getInstance().isQueryAdministrator( new SaveListener(){
			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						addDevice(deviceidentify);
					}
				});
			}

			@Override
			public void onFailure(int paramInt, String paramString) {
				dismissLoadingDialog();
				ShowToast(paramString);
				Log.i("onLoginFailure", paramString.toString());
				if(paramInt == 1){
					startAnimActivity(AddDeviceVerifyActivity.class);
					finish();
				}
			}
		});
	}
	
	/** 添加设备 */
	public void addDevice(String str){
		SocketInit.getInstance().addDevice(new SaveListener(){
			@Override
			public void onSuccess() {
			Intent intent = new Intent(); 
			intent.setAction("action.main.refresh.2a");  
			sendBroadcast(intent); 
			finish();
			dismissLoadingDialog();
		}

		@Override
		public void onFailure(int paramInt, String paramString) {
				dismissLoadingDialog();
				ShowToast(paramString);
				Log.e("onLoginFailure", paramString.toString());
			}
		});
	}
}

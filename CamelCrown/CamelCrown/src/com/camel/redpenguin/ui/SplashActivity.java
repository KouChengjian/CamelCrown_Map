package com.camel.redpenguin.ui;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.camel.redpenguin.CustomApplcation;
import com.camel.redpenguin.CustomService;
import com.camel.redpenguin.R;
import com.camel.redpenguin.config.Config;
import com.camel.redpenguin.config.SocketInit;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * @ClassName: SplashActivity
 * @Description: 引导界面
 * @author: KCJ
 * @date: 
 */
public class SplashActivity extends BaseActivity implements OnClickListener{
	private static final int GO_HOME = 100;
	private static final int GO_LOGIN = 200;
	private SDKReceiver mReceiver;
	private LocationClient mLocationClient; // 定位获取当前用户的地理位置

	private LinearLayout mLinearCtrlbar;
	private Button mBtnRegister;
	private Button mBtnLogin;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		SocketInit.init(this ,Config.SERVE_IP, Config.SERVE_PORT);
		initViews();
		initEvents();
		initLocClient();
		if(!TextUtils.isEmpty(CustomApplcation.getInstance().getCurrentUser())){// 自动登入
			mHandler.sendEmptyMessageDelayed(GO_HOME, 2000);
		}else{
			mLinearCtrlbar.setVisibility(View.VISIBLE);
			showLoginAnimation();
		}
	}
	
	public void initViews(){
		mLinearCtrlbar = (LinearLayout) findViewById(R.id.welcome_linear_ctrlbar);
		mBtnRegister = (Button) findViewById(R.id.welcome_btn_register);
		mBtnLogin = (Button) findViewById(R.id.welcome_btn_login);
	}
	
    public void initEvents(){
    	mBtnRegister.setOnClickListener(this);
		mBtnLogin.setOnClickListener(this);
	}
    
    public void initLocClient() {
		mLocationClient = CustomApplcation.getInstance().mLocationClient;
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new SDKReceiver();
		registerReceiver(mReceiver, iFilter);
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_HOME:
				// 在此自动更新数据
				updateDeviceData();
				startAnimActivity(MainActivity.class);
				finish();			
				break;
		    case GO_LOGIN:
			    startAnimActivity(RegisterActivity.class);
			    finish();
			    break;
		    }
		}
	};

	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				Log.e("SplashActivity","key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
//				ShowToast("key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
			} else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				Log.e("SplashActivity","网络出错");
//				ShowToast("网络出错");
			}
		}
	}

	public void onResume() {
		super.onResume();
		Intent startIntent = new Intent(SplashActivity.this, CustomService.class);  
        startService(startIntent);
	}

	public void onPause() {
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.stop();
		}
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.welcome_btn_register:
			startAnimActivity(RegisterActivity.class);
			finish();
			break;
		case R.id.welcome_btn_login:
			startAnimActivity(LoginActivity.class);
			finish();
			break;
		}
	}
	
	private void showLoginAnimation() {
		Animation animation = AnimationUtils.loadAnimation(
				SplashActivity.this, R.anim.login_ctrl_bar_slideup);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				//mLinearCtrlbar.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						//mLinearCtrlbar.setVisibility(View.VISIBLE);
					}
				}, 800);
			}
		});
		mLinearCtrlbar.startAnimation(animation);
	}
}

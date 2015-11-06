package com.camel.redpenguin.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.camel.redpenguin.R;
import com.camel.redpenguin.config.SocketInit;
import com.camel.redpenguin.listener.SaveListener;
import com.camel.redpenguin.util.NetworkUtil;
import com.camel.redpenguin.util.TextUtil;
import com.camel.redpenguin.view.BaseDialog;
import com.camel.redpenguin.view.HandyTextView;

/**
 * @ClassName: LoginActivity
 * @Description: 登入
 * @author: kcj
 * @date: 
 */
public class LoginActivity extends BaseActivity implements OnClickListener{

	private Button mBtnBack;
	private Button mBtnLogin;
	private EditText mEtAccount;
	private EditText mEtPwd;
	private HandyTextView mHtvForgotPassword;
	// 对话框
	private BaseDialog mBackDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		/** header */
		initTopBarForOnlyTitle("登入");
		/** 初始化UI */
		mEtAccount = (EditText) findViewById(R.id.login_et_account);
		mEtPwd = (EditText) findViewById(R.id.login_et_pwd);
		mHtvForgotPassword = (HandyTextView) findViewById(R.id.login_htv_forgotpassword);
		TextUtil.addUnderlineText(this, mHtvForgotPassword, 0,
				mHtvForgotPassword.getText().length());
		mBtnBack = (Button) findViewById(R.id.login_btn_back);
		mBtnLogin = (Button) findViewById(R.id.login_btn_login);
		mHtvForgotPassword.setOnClickListener(this);
		mBtnBack.setOnClickListener(this);
		mBtnLogin.setOnClickListener(this);
		/** 初始化对话框*/
		mBackDialog = BaseDialog.getDialog(LoginActivity.this, "提示",
				"确认要放弃登入么?", "确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				}, "取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		mBackDialog.setButton1Background(R.drawable.btn_default_popsubmit);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_htv_forgotpassword:
			startAnimActivity(FindPwdTabsActivity.class);
			finish();
			break;
		case R.id.login_btn_back:
			mBackDialog.show();
			break;
		case R.id.login_btn_login:
            login();
			break;
		}
	}
	
	public void login(){
		String name = mEtAccount.getText().toString();
		String password = mEtPwd.getText().toString();

		if(!NetworkUtil.isNetworkAvailable(mContext)){
			ShowToast("无网络，请链接。");
			return ;
		}
		if (TextUtils.isEmpty(name)) {
			ShowToast(R.string.toast_error_username_null);
			return;
		}
		if (TextUtils.isEmpty(password)) {
			ShowToast(R.string.toast_error_password_null);
			return;
		}
		showLoadingDialog("正在登陆...");
		SocketInit.getInstance().login(name , password, new SaveListener(){
			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showLoadingDialog("正在获取设备列表...");
					}
				});
				//更新用户的地理位置以及好友的资料
		        updateDeviceData(); 
				dismissLoadingDialog();
				startAnimActivity(MainActivity.class);
				finish();
			}

			@Override
			public void onFailure(int paramInt, String paramString) {
				dismissLoadingDialog();
				ShowToast(paramString);
				Log.i("onLoginFailure", paramString.toString());
			}
			
		});
	}
}

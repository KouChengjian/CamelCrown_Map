package com.camel.redpenguin.view.pwd;

import java.util.List;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.camel.redpenguin.CustomApplcation;
import com.camel.redpenguin.R;
import com.camel.redpenguin.bean.DBHelper;
import com.camel.redpenguin.config.SocketInit;
import com.camel.redpenguin.greendao.CamelDevice;
import com.camel.redpenguin.listener.FindListener;
import com.camel.redpenguin.listener.SaveListener;
import com.camel.redpenguin.ui.FindPwdPhoneActivity;
import com.camel.redpenguin.ui.MainActivity;
import com.camel.redpenguin.util.NetworkUtil;
import com.camel.redpenguin.view.register.RegisterStep;

public class StepPwd extends PwdStep{

	private EditText mEtPwd;
	private EditText mEtRePwd;

	private boolean mIsChange = true;
	
	public StepPwd(FindPwdPhoneActivity activity, View contentRootView) {
		super(activity, contentRootView);
	}

	@Override
	public void initViews() {
		mEtPwd = (EditText) findViewById(R.id.reg_setpwd_et_pwd);
		mEtRePwd = (EditText) findViewById(R.id.reg_setpwd_et_repwd);
	}

	@Override
	public void initEvents() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean validate() {
		String pwd = null;
		String rePwd = null;
		if (isNull(mEtPwd)) {
			showCustomToast("请输入密码");
			mEtPwd.requestFocus();
			return false;
		} else {
			pwd = mEtPwd.getText().toString().trim();
			if (pwd.length() < 6) {
				showCustomToast("密码不能小于6位");
				mEtPwd.requestFocus();
				return false;
			}
			if (pwd.length() > 12) {
				showCustomToast("密码不能大于13位");
				mEtPwd.requestFocus();
				return false;
			}
		}
		if (isNull(mEtRePwd)) {
			showCustomToast("请重复输入一次密码");
			mEtRePwd.requestFocus();
			return false;
		} else {
			rePwd = mEtRePwd.getText().toString().trim();
			if (!pwd.equals(rePwd)) {
				showCustomToast("两次输入的密码不一致");
				mEtRePwd.requestFocus();
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isChange() {
		// TODO Auto-generated method stub
		return mIsChange;
	}

	public void doNext() {
		showLoadingDialog("请稍后,正在提交...");
		SocketInit.getInstance().anewSetPwd(getPhoneNumber(), mEtPwd.getText().toString(), new SaveListener(){
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				dismissLoadingDialog();// 
				// 保存当前状态
				CustomApplcation.getInstance().setCurrentUser("1");
				// 保存账号
				CustomApplcation.getInstance().setUsername(getPhoneNumber());
				// 更新当前设备
				updateDeviceData();
				// 界面跳转
				Intent intent = new Intent(mContext,MainActivity.class);
				mContext.startActivity(intent);
				mActivity.finish();
			}

			@Override
			public void onFailure(int paramInt, String paramString) {
				// TODO Auto-generated method stub
				ShowToast(paramString);
				dismissLoadingDialog();
				Log.e("onFailure", paramString.toString());
			}
		});
	}
	
	public void updateDeviceData(){
        SocketInit.getInstance().queryCurrentContactList(new FindListener(){
			@Override
			public void onSuccess(List<CamelDevice> mCamelDevice) {
				// TODO Auto-generated method stub
				CustomApplcation.getInstance().setContactList(mCamelDevice);
				Intent intent = new Intent(); 
				intent.setAction("action.main.refresh.base");  
				mContext.sendBroadcast(intent); 
			}

			@Override
			public void onFailure(int paramInt, String paramString) {
				// TODO Auto-generated method stub
				
			}
		});  
	}
	
	public void next() {

	}
	public void nextFailure() {

	}
}

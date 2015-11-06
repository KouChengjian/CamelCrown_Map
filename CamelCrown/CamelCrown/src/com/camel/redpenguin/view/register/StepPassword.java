package com.camel.redpenguin.view.register;

import com.camel.redpenguin.CustomApplcation;
import com.camel.redpenguin.R;
import com.camel.redpenguin.bean.DBHelper;
import com.camel.redpenguin.bean.Packet;
import com.camel.redpenguin.config.SocketInit;
import com.camel.redpenguin.listener.SaveListener;
import com.camel.redpenguin.ui.MainActivity;
import com.camel.redpenguin.ui.RegisterActivity;
import com.camel.redpenguin.util.EncodeUtils;
import com.camel.redpenguin.util.NetworkUtil;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * @ClassName: StepPhone
 * @Description: 注册 -设置密码
 * @author: kcj
 * @date: 
 */
public class StepPassword extends RegisterStep implements TextWatcher{

	private EditText mEtPwd;
	private EditText mEtRePwd;

	private boolean mIsChange = true;
	
	public StepPassword(RegisterActivity activity, View contentRootView) {
		super(activity, contentRootView);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		mEtPwd = (EditText) findViewById(R.id.reg_setpwd_et_pwd);
		mEtRePwd = (EditText) findViewById(R.id.reg_setpwd_et_repwd);
	}

	@Override
	public void initEvents() {
		// TODO Auto-generated method stub
		mEtPwd.addTextChangedListener(this);
		mEtRePwd.addTextChangedListener(this);
	}

	@Override
	public void doNext() {
//		if(!NetworkUtil.isNetworkAvailable(mContext)){
//			ShowToast("无网络，请链接。");
//			return ;
//		}
//		if(!SocketInit.getInstance().isSocketConnected()){
//			ShowToast("无服务...");
//			return;
//		}
		showLoadingDialog("请稍后,正在提交...");
		String str = EncodeUtils.registerDataEncode(getPhoneNumber(), mEtPwd.getText().toString());
		// 传输
		Packet packet = new Packet();
		packet.pack(str);
		SocketInit.getInstance().register(packet, new SaveListener(){
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				dismissLoadingDialog();// 
				// 保存当前状态
				CustomApplcation.getInstance().setCurrentUser("1");
				// 保存账号
				CustomApplcation.getInstance().setUsername(getPhoneNumber());
				// 在数据库中插入数据
				if(!DBHelper.getInstance(mContext).isUsersInfoSaved(getPhoneNumber())){
					long type = DBHelper.getInstance(mContext).addToUsersInfoTable(CustomApplcation.getInstance().getRegisterCamelUser());
					if(type ==1){
						Log.e("TAG", "成功对数据库添加数据");
						//ShowToast("成功对数据库添加数据");
					}else{
						Log.e("TAG", "失败对数据库添加数据");
						//ShowToast("失败对数据库添加数据");
					}	
				}
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
	
	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
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

	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		mIsChange = true;
	}
}

package com.camel.redpenguin.ui;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.camel.redpenguin.R;
import com.camel.redpenguin.config.Config;
import com.camel.redpenguin.config.SocketInit;
import com.camel.redpenguin.listener.SaveListener;
import com.camel.redpenguin.view.BaseDialog;
import com.camel.redpenguin.view.HandyTextView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @ClassName: AddDeviceVerifyActivity
 * @Description: 设备添加存在管理员短信验证
 * @author: kcj
 * @date:
 */
@SuppressLint("HandlerLeak")
public class AddDeviceVerifyActivity extends BaseActivity implements OnClickListener{

	private HandyTextView mHtvPhoneNumber;
	private EditText mEtVerifyCode;
	private Button mBtnResend;
	private TextView mtvConfirm;
	// 验证发生时间
	private int mReSendTime = 60;
	private static final String PROMPT = "验证码已经发送到 * ";
	private BaseDialog mBaseDialog;
	private String mVerifyCode;
	private boolean mIsChange = true;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deviceverify);
		/** header */
		initTopBarForLeft("短信验证");
		/** UI */
		mHtvPhoneNumber = (HandyTextView) findViewById(R.id.reg_verify_htv_phonenumber);
		mEtVerifyCode = (EditText) findViewById(R.id.reg_verify_et_verifycode);
		mBtnResend = (Button) findViewById(R.id.reg_verify_btn_resend);
		mtvConfirm = (TextView) findViewById(R.id.tv_add_device_verify_again);
		mHtvPhoneNumber.setText(PROMPT + Config.DEVICE_ADMINISTRATOR);
		mBtnResend.setEnabled(false);
		mBtnResend.setText("重发(60)");
		mBtnResend.setOnClickListener(this);
		mtvConfirm.setOnClickListener(this);
		handlerDate.sendEmptyMessage(0);
		/** 初始化短信验证SDK */
		initSMS();
		/** 发送短信 */
		if(!TextUtils.isEmpty(Config.DEVICE_ADMINISTRATOR)){
			ShowToast("短信已发送");
			SMSSDK.getVerificationCode("86",Config.DEVICE_ADMINISTRATOR);
		}
	}

	
	public void initSMS(){
		SMSSDK.initSDK(this,Config.APP_SMS_KEY,Config.APP_SMS_SECRET);
		EventHandler eh=new EventHandler(){
			@Override
			public void afterEvent(int event, int result, Object data) {
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}
		};
		SMSSDK.registerEventHandler(eh);
	}
	
	/** 短信监听-线程 */
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int event = msg.arg1;
			int result = msg.arg2;
			Object data = msg.obj;
			if (result == SMSSDK.RESULT_COMPLETE) {
				// 提交验证码成功
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
					sendAddDevice();
					ShowToast("提交验证码成功");
				}else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
					ShowToast("验证码已经发送");
					
				}else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){//返回支持发送验证码的国家列表
					//Toast.makeText(getApplicationContext(), "获取国家列表成功", Toast.LENGTH_SHORT).show();
				}
			}else{
				failure();
				((Throwable) data).printStackTrace();
			}
		}
	};
	
	Handler handlerDate = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (mReSendTime > 1) {
				mReSendTime--;
				mBtnResend.setEnabled(false);
				mBtnResend.setText("重发(" + mReSendTime + ")");
				handlerDate.sendEmptyMessageDelayed(0, 1000);
			} else {
				mReSendTime = 60;
				mBtnResend.setEnabled(true);
				mBtnResend.setText("重    发");
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.reg_verify_btn_resend:
			handlerDate.sendEmptyMessage(0);
			break;
		case R.id.tv_add_device_verify_again:
			disposeVerifyCode();
			break;
		}
	}
	
	public void disposeVerifyCode(){
		mVerifyCode = mEtVerifyCode.getText().toString().trim();
		if(TextUtils.isEmpty(mVerifyCode)){
			ShowToast("验证码为空");
			return;
		}
		showLoadingDialog("正在验证,请稍后...");
		if(!TextUtils.isEmpty(mVerifyCode)){
			SMSSDK.submitVerificationCode("86", Config.DEVICE_ADMINISTRATOR , mVerifyCode);
		}
	}
	
	public void sendAddDevice(){
		showLoadingDialog("正在与设备匹配中，其耐心等待...");
		SocketInit.getInstance().addDevice(new SaveListener(){
			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dismissLoadingDialog();
					}
				});
				finish();
			}

			@Override
			public void onFailure(int paramInt, String paramString) {
				dismissLoadingDialog();
				ShowToast(paramString);
				Log.e("onLoginFailure", paramString.toString());
			}
		});
	}
	
	public void failure() {
		dismissLoadingDialog();
		mBaseDialog = BaseDialog.getDialog(mContext, "提示", "验证码错误",
				"确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						mEtVerifyCode.requestFocus();
						dialog.dismiss();
					}

				});
		mBaseDialog.show();
	}
}

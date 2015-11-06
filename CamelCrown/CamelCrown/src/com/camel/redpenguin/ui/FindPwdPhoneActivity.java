package com.camel.redpenguin.ui;

import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.camel.redpenguin.R;
import com.camel.redpenguin.adapter.SimpleListDialogAdapter;
import com.camel.redpenguin.config.Config;
import com.camel.redpenguin.view.BaseDialog;
import com.camel.redpenguin.view.HandyTextView;
import com.camel.redpenguin.view.SimpleListDialog;
import com.camel.redpenguin.view.SimpleListDialog.onSimpleListItemClickListener;
import com.camel.redpenguin.view.pwd.PwdStep;
import com.camel.redpenguin.view.pwd.StepPhone;
import com.camel.redpenguin.view.pwd.StepPwd;
import com.camel.redpenguin.view.pwd.StepVerify;
import com.camel.redpenguin.view.register.RegisterStep.onNextActionListener;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewFlipper;

/**
 * @ClassName: FindPwdPhoneActivity
 * @Description: 忘记密码 -手机修改
 * @author: kcj
 * @date: 
 */
public class FindPwdPhoneActivity extends BaseActivity implements OnClickListener,onNextActionListener{
	
	private Button mBtnPrevious;
	private Button mBtnNext;
	private ViewFlipper mVfFlipper;
	// ViewFlipper
	private PwdStep mCurrentStep;
	private int mCurrentStepIndex = 1;
	private StepPhone mStepPhone;
	private StepVerify mStepVerify;
	private StepPwd mStepSetPassword;
	// 对话框
	private BaseDialog mBackDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_findpwdphone);
		/** 初始化UI */
		mVfFlipper = (ViewFlipper) findViewById(R.id.findpwdphone_vf_viewflipper);
		mVfFlipper.setDisplayedChild(0);
		mBtnPrevious = (Button) findViewById(R.id.findpwdphone_btn_back);
		mBtnNext = (Button) findViewById(R.id.findpwdphone_btn_next);
		mBtnPrevious.setOnClickListener(this);
		mBtnNext.setOnClickListener(this);
		/** 初始化注册类 */
		mCurrentStep = initStep();
		mCurrentStep.setOnNextActionListener(this);
		/** 初始化对话框 */
		mBackDialog = BaseDialog.getDialog(FindPwdPhoneActivity.this, "提示",
				"确认要放弃注册么?", "确认", new DialogInterface.OnClickListener() {

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
		/** 初始化短信验证SDK */
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
	
	private PwdStep initStep(){
		switch (mCurrentStepIndex) {
		case 1:
			if (mStepPhone == null) {
				mStepPhone = new StepPhone(FindPwdPhoneActivity.this, mVfFlipper.getChildAt(0));
			}
			mBtnPrevious.setText("返    回");
			mBtnNext.setText("下一步");
			return mStepPhone;
		case 2:
			if (mStepVerify == null) {
				mStepVerify = new StepVerify(this, mVfFlipper.getChildAt(1));
			}
			mBtnPrevious.setText("上一步");
			mBtnNext.setText("下一步");
			return mStepVerify;
		case 3:
			if (mStepSetPassword == null) {
				mStepSetPassword = new StepPwd(this,mVfFlipper.getChildAt(2));
			}
			mBtnPrevious.setText("上一步");
			mBtnNext.setText("注    册");
			return mStepSetPassword;
		}
		return null;
	}

	

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.findpwdphone_btn_back:
			if (mCurrentStepIndex <= 1) {
				mBackDialog.show();
			} else {
				doPrevious();
			}
			break;

		case R.id.findpwdphone_btn_next:
			if (mCurrentStepIndex < 3) {
				doNext();
			} else {
				if (mCurrentStep.validate()) {
					mCurrentStep.doNext();
				}
			}
			break;
		}
	}
	
	private void doPrevious() {
		mCurrentStepIndex--;
		mCurrentStep = initStep();
		mCurrentStep.setOnNextActionListener(this);
		mVfFlipper.setInAnimation(this, R.anim.push_right_in);
		mVfFlipper.setOutAnimation(this, R.anim.push_right_out);
		mVfFlipper.showPrevious();
	}
	
	private void doNext() {
		if (mCurrentStep.validate()) {
			if (mCurrentStep.isChange()) {
				mCurrentStep.doNext();
			} else {
				next();
			}
		}
	}
	
	@Override
	public void next() {
		mCurrentStepIndex++;
		mCurrentStep = initStep();
		mCurrentStep.setOnNextActionListener(this);
		mVfFlipper.setInAnimation(this, R.anim.push_left_in);
		mVfFlipper.setOutAnimation(this, R.anim.push_left_out);
		mVfFlipper.showNext();
	}
	
	
	@Override
	public void showLoadingDialog(String text) {
		super.showLoadingDialog(text);
	}

	@Override
	public void dismissLoadingDialog() {
		super.dismissLoadingDialog();
	}
	
	public String getPhoneNumber() {
		if (mStepPhone != null) {
			return mStepPhone.getPhoneNumber();
		}
		return "";
	}
	@Override
	public void showCustomToast(String text) {
		super.showCustomToast(text);
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
					mCurrentStep.next();
					//Toast.makeText(getApplicationContext(), "提交验证码成功", Toast.LENGTH_SHORT).show();
				}else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
					mCurrentStep.next();
					//Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();
				}else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){//返回支持发送验证码的国家列表
					//Toast.makeText(getApplicationContext(), "获取国家列表成功", Toast.LENGTH_SHORT).show();
				}
			}else{
				mCurrentStep.nextFailure();
				((Throwable) data).printStackTrace();
			}
		}
	};
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		SMSSDK.unregisterAllEventHandler();
	}
}

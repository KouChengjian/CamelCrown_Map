package com.camel.redpenguin.view.pwd;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import cn.smssdk.SMSSDK;

import com.camel.redpenguin.R;
import com.camel.redpenguin.ui.FindPwdPhoneActivity;
import com.camel.redpenguin.view.BaseDialog;
import com.camel.redpenguin.view.HandyTextView;


public class StepVerify extends PwdStep implements OnClickListener{

	private HandyTextView mHtvPhoneNumber;
	private EditText mEtVerifyCode;
	private Button mBtnResend;
	private static final String PROMPT = "验证码已经发送到 * ";
	// 验证发生时间
	private int mReSendTime = 60;
	private BaseDialog mBaseDialog;
	private String mVerifyCode;
	private boolean mIsChange = true;
	
	public StepVerify(FindPwdPhoneActivity activity, View contentRootView) {
		super(activity, contentRootView);
		handler.sendEmptyMessage(0);
	}

	@Override
	public void initViews() {
		mHtvPhoneNumber = (HandyTextView) findViewById(R.id.reg_verify_htv_phonenumber);
		mHtvPhoneNumber.setText(PROMPT + getPhoneNumber());
		mEtVerifyCode = (EditText) findViewById(R.id.reg_verify_et_verifycode);
		mBtnResend = (Button) findViewById(R.id.reg_verify_btn_resend);
		mBtnResend.setEnabled(false);
		mBtnResend.setText("重发(60)");
		
	}

	@Override
	public void initEvents() {
		mBtnResend.setOnClickListener(this);
	}

	@Override
	public boolean validate() {
		if (isNull(mEtVerifyCode)) {
			showCustomToast("请输入验证码");
			mEtVerifyCode.requestFocus();
			return false;
		}
		mVerifyCode = mEtVerifyCode.getText().toString().trim();
		return true;
	}

	@Override
	public boolean isChange() {
		// TODO Auto-generated method stub
		return mIsChange;
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (mReSendTime > 1) {
				mReSendTime--;
				mBtnResend.setEnabled(false);
				mBtnResend.setText("重发(" + mReSendTime + ")");
				handler.sendEmptyMessageDelayed(0, 1000);
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
			handler.sendEmptyMessage(0);
			break;
		}
	}
	
	public void doNext() {
		showLoadingDialog("正在验证,请稍后...");
		if(!TextUtils.isEmpty(mVerifyCode)){
			SMSSDK.submitVerificationCode("86", getPhoneNumber(), mVerifyCode);
		}
	}
	
	public void next() {
		dismissLoadingDialog();
		mIsChange = false;
		mOnNextActionListener.next();
	}
	
	public void nextFailure() {
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

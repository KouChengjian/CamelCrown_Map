package com.camel.redpenguin.view.register;

import cn.smssdk.SMSSDK;

import com.camel.redpenguin.R;
import com.camel.redpenguin.ui.RegisterActivity;
import com.camel.redpenguin.view.BaseDialog;
import com.camel.redpenguin.view.HandyTextView;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * @ClassName: StepPhone
 * @Description: 注册 -短信验证
 * @author kcj
 * @date
 */
public class StepVerify extends RegisterStep implements OnClickListener,TextWatcher{

	private HandyTextView mHtvPhoneNumber;
	private EditText mEtVerifyCode;
	private Button mBtnResend;
	// 验证发生时间
	private int mReSendTime = 60;
	private static final String PROMPT = "验证码已经发送到 * ";
	private BaseDialog mBaseDialog;
	private String mVerifyCode;
	private boolean mIsChange = true;
	
	public StepVerify(RegisterActivity activity, View contentRootView) {
		super(activity, contentRootView);
		// TODO Auto-generated constructor stub
		handler.sendEmptyMessage(0);
	}
	
	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		mHtvPhoneNumber = (HandyTextView) findViewById(R.id.reg_verify_htv_phonenumber);
		mHtvPhoneNumber.setText(PROMPT + getPhoneNumber());
		mEtVerifyCode = (EditText) findViewById(R.id.reg_verify_et_verifycode);
		mBtnResend = (Button) findViewById(R.id.reg_verify_btn_resend);
		mBtnResend.setEnabled(false);
		mBtnResend.setText("重发(60)");
	}

	@Override
	public void initEvents() {
		// TODO Auto-generated method stub
		mBtnResend.setOnClickListener(this);
		mEtVerifyCode.addTextChangedListener(this);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.reg_verify_btn_resend:
			handler.sendEmptyMessage(0);
			break;
		}
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

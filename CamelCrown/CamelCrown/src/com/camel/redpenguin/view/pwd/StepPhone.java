package com.camel.redpenguin.view.pwd;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import cn.smssdk.SMSSDK;

import com.camel.redpenguin.R;
import com.camel.redpenguin.ui.FindPwdPhoneActivity;
import com.camel.redpenguin.util.TelNumMatch;
import com.camel.redpenguin.util.TextUtil;
import com.camel.redpenguin.view.HandyTextView;
import com.camel.redpenguin.view.SimpleListDialog.onSimpleListItemClickListener;
import com.camel.redpenguin.view.register.RegisterStep;


/**
 * @ClassName: StepPhone
 * @Description: 修改密码 -账号(手机号码)
 * @author: kcj
 * @date:
 */
public class StepPhone extends PwdStep implements OnClickListener{

	private EditText mEtPhone;
	private HandyTextView mHtvAreaCode;
	private HandyTextView mHtvNotice;
	private HandyTextView mHtvNote;
	
	private String mPhone;
	// edit发生改变
	private boolean mIsChange = true;
	
	public StepPhone(FindPwdPhoneActivity activity, View contentRootView) {
		super(activity, contentRootView);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initViews() {
		mHtvAreaCode = (HandyTextView) findViewById(R.id.reg_phone_htv_areacode);
		mEtPhone = (EditText) findViewById(R.id.reg_phone_et_phone);
		mHtvNotice = (HandyTextView) findViewById(R.id.reg_phone_htv_notice);
		mHtvNote = (HandyTextView) findViewById(R.id.reg_phone_htv_note);
		mHtvNotice.setVisibility(View.GONE);
		mHtvNote.setVisibility(View.GONE);
	}

	@Override
	public void initEvents() {
		// TODO Auto-generated method stub
		
	}

	/**
	 *  判断手机号码是否正确
	 */
	@Override
	public boolean validate() {
		mPhone = null;
		if (isNull(mEtPhone)) { // 是否为空
			Toast.makeText(mContext, "请填写手机号码",Toast.LENGTH_LONG).show();
			mEtPhone.requestFocus();
			return false;
		}
		String phone = mEtPhone.getText().toString().trim();
		TelNumMatch numMatch = new TelNumMatch(phone);
		if( numMatch.matchNum() == 5 || numMatch.matchNum() == 4){
			Toast.makeText(mContext, "手机号码格式不正确",Toast.LENGTH_LONG).show(); 
			return false;
		}else{
			mPhone = phone;
			return true;
		}
	}

	@Override
	public boolean isChange() {
		// TODO Auto-generated method stub
		return mIsChange;
	}

	/** 下一步 */
	public void doNext() {
		showLoadingDialog("正在验证手机号,请稍后...");
		if(!TextUtils.isEmpty(mEtPhone.getText().toString())){
			SMSSDK.getVerificationCode("86",mEtPhone.getText().toString());
		}
	}
	
	public void next() {
		dismissLoadingDialog();
		mIsChange = false;
		mOnNextActionListener.next();
	}
	
	public String getPhoneNumber() {
		return  mPhone;
	}
}

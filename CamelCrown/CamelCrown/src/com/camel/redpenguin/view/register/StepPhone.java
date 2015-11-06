package com.camel.redpenguin.view.register;


import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import cn.smssdk.SMSSDK;

import com.camel.redpenguin.R;
import com.camel.redpenguin.adapter.SimpleListDialogAdapter;
import com.camel.redpenguin.ui.RegisterActivity;
import com.camel.redpenguin.util.TelNumMatch;
import com.camel.redpenguin.util.TextUtil;
import com.camel.redpenguin.view.HandyTextView;
import com.camel.redpenguin.view.SimpleListDialog;
import com.camel.redpenguin.view.SimpleListDialog.onSimpleListItemClickListener;


/**
 * @ClassName: StepPhone
 * @Description: 注册 -账号(手机号码)
 * @author kcj
 * @date
 */
public class StepPhone extends RegisterStep implements OnClickListener,TextWatcher
, onSimpleListItemClickListener{
	
	private EditText mEtPhone;
	private HandyTextView mHtvAreaCode;
	private HandyTextView mHtvNotice;
	private HandyTextView mHtvNote;
	
	// edit发生改变
	private boolean mIsChange = true;
	// 手机号码国家归属
	private String mAreaCode = "+86";
	private String[] mCountryCodes;
	private SimpleListDialog mSimpleListDialog;
	
	private String mPhone;
	//private static final String DEFAULT_PHONE = "+8618710627436";

	public StepPhone(RegisterActivity activity, View contentRootView) {
		super(activity, contentRootView);
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		mHtvAreaCode = (HandyTextView) findViewById(R.id.reg_phone_htv_areacode);
		mEtPhone = (EditText) findViewById(R.id.reg_phone_et_phone);
		mHtvNotice = (HandyTextView) findViewById(R.id.reg_phone_htv_notice);
		mHtvNote = (HandyTextView) findViewById(R.id.reg_phone_htv_note);
		TextUtil.addHyperlinks(mHtvNote, 8, 15, this);
	}

	@Override
	public void initEvents() {
		// TODO Auto-generated method stub
		mHtvAreaCode.setOnClickListener(this);
		mEtPhone.addTextChangedListener(this);
	}

	/**
	 *  判断手机号码是否正确
	 */
	public boolean validate() {
		// TODO Auto-generated method stub
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.reg_phone_htv_areacode:
			mCountryCodes = mContext.getResources().getStringArray(R.array.country_codes);
			mSimpleListDialog = new SimpleListDialog(mContext);
			mSimpleListDialog.setTitle("选择国家区号");
			mSimpleListDialog.setTitleLineVisibility(View.GONE);
			mSimpleListDialog.setAdapter(new SimpleListDialogAdapter(mContext,
					mCountryCodes));
			mSimpleListDialog.setOnSimpleListItemClickListener(StepPhone.this);
			mSimpleListDialog.show();
			break;
		default:
            Toast.makeText(mContext , "为添加", Toast.LENGTH_LONG).show();
			break;
		}
	}

	/**
	 *  国家对话框点击
	 */
	@Override
	public void onItemClick(int position) {
		// TODO Auto-generated method stub
		String text = TextUtil.getCountryCodeBracketsInfo(
				mCountryCodes[position], mAreaCode);
		mAreaCode = text;
		mHtvAreaCode.setText(text);
	}
	
	
	/**
	 *  edit发生改变
	 */
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
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		mIsChange = true;
		if (s.toString().length() > 0) {
			mHtvNotice.setVisibility(View.VISIBLE);
			char[] chars = s.toString().toCharArray();
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < chars.length; i++) {
				if (i % 4 == 2) {
					buffer.append(chars[i] + "  ");
				} else {
					buffer.append(chars[i]);
				}
			}
			mHtvNotice.setText(buffer.toString());
		} else {
			mHtvNotice.setVisibility(View.GONE);
		}
	}

	@Override
	public void doNext() {
		showLoadingDialog("正在验证手机号,请稍后...");
		if(!TextUtils.isEmpty(mEtPhone.getText().toString())){
			SMSSDK.getVerificationCode("86",mEtPhone.getText().toString());
		}
		/**  
		putAsyncTask(new AsyncTask<Void, Void, Boolean>() {
		
			protected void onPreExecute() {
				super.onPreExecute();
				showLoadingDialog("正在验证手机号,请稍后...");
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					Thread.sleep(2000);
					if(!TextUtils.isEmpty(mEtPhone.getText().toString())){
						SMSSDK.getVerificationCode("86",mEtPhone.getText().toString());
						return true;
					}
				} catch (InterruptedException e) {

				}
				return false;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				dismissLoadingDialog();
				if (result) {
					mIsChange = false;
					mOnNextActionListener.next();
				} else {
					showCustomToast("手机号码不可用或已被注册");
				}
			}
		});*/
	}
	
	public void next(){
		dismissLoadingDialog();
		mIsChange = false;
		mOnNextActionListener.next();
	}
	
	public String getPhoneNumber() {
		//return "(" + mAreaCode + ")" + mPhone;
		return  mPhone;
	}

}

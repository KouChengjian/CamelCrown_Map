package com.camel.redpenguin.ui;

import com.camel.redpenguin.R;
import com.camel.redpenguin.util.TextUtil;
import com.camel.redpenguin.view.HandyTextView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


/**
 * @ClassName: ResetPwdPhoneActivity
 * @Description: 重新设置密码
 * @author kcj
 * @date
 */
public class ResetPwdPhoneActivity extends BaseActivity implements OnClickListener{

	private HandyTextView mHtvContent;
	private EditText mEtValidateCode;
	private EditText mEtNewPwd;
	private EditText mEtReNewPwd;
	private Button mBtnBack;
	private Button mBtnSubmit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resetpwdphone);
		/** header */
		initTopBarForOnlyTitle("密码重置");
		/** initViews */
		mHtvContent = (HandyTextView) findViewById(R.id.resetpwdphone_htv_content);
		TextUtil.addHyperlinks(mHtvContent, 23, 35, this);
		mEtValidateCode = (EditText) findViewById(R.id.resetpwdphone_et_validatecode);
		mEtNewPwd = (EditText) findViewById(R.id.resetpwdphone_et_newpwd);
		mEtReNewPwd = (EditText) findViewById(R.id.resetpwdphone_et_renewpwd);
		mBtnBack = (Button) findViewById(R.id.resetpwdphone_btn_back);
		mBtnSubmit = (Button) findViewById(R.id.resetpwdphone_btn_submit);
		/** initEvents */
		mBtnBack.setOnClickListener(this);
		mBtnSubmit.setOnClickListener(this);
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.resetpwdphone_btn_back:
			finish();
			break;

		case R.id.resetpwdphone_btn_submit:
			//submit();
			break;

		default:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setType("vnd.android-dir/mms-sms");
			intent.putExtra("address", "106902291602");
			intent.putExtra("sms_body", "MMCZ");
			startActivity(intent);
			break;
		}
	}

}

package com.camel.redpenguin.view.pwd;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.camel.redpenguin.ui.FindPwdPhoneActivity;
import com.camel.redpenguin.ui.RegisterActivity;
import com.camel.redpenguin.view.register.RegisterStep.onNextActionListener;

public abstract class PwdStep {

	protected FindPwdPhoneActivity mActivity;
	protected Context mContext;
	private View mContentRootView;

	protected onNextActionListener mOnNextActionListener;

	public PwdStep(FindPwdPhoneActivity activity, View contentRootView) {
		mActivity = activity;
		mContext = (Context) mActivity;
		mContentRootView = contentRootView;
		initViews();
		initEvents();
	}

	public abstract void initViews();

	public abstract void initEvents();

	public abstract boolean validate();

	public abstract boolean isChange();

	public View findViewById(int id) {
		return mContentRootView.findViewById(id);
	}

	public void setOnNextActionListener(onNextActionListener listener) {
		mOnNextActionListener = listener;
	}

	// 是否为空
	protected boolean isNull(EditText editText) {
		String text = editText.getText().toString().trim();
		if (text != null && text.length() > 0) {
			return false;
		}
		return true;
	}

	protected void showLoadingDialog(String text) {
		mActivity.showLoadingDialog(text);
	}
	protected void dismissLoadingDialog() {
		mActivity.dismissLoadingDialog();
	}
	protected String getPhoneNumber() {
		return mActivity.getPhoneNumber();
	}
	public void showCustomToast(String text) {
		mActivity.showCustomToast(text);
	}
	public void ShowToast(String text) {
		mActivity.ShowToast(text);
	}
	
	public void doNext() {

	}
	
	public void next() {

	}
	public void nextFailure() {

	}
}

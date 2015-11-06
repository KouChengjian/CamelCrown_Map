package com.camel.redpenguin.view;

import android.content.Context;

import com.camel.redpenguin.R;

public class EditViewDialog extends BaseDialog{

	private DeletableEditText mDeletableEditText;
	
	public EditViewDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init() {
		setDialogContentView(R.layout.common_dialog_edit_view);
		mDeletableEditText = (DeletableEditText) findViewById(R.id.edt_import_identify);
	}
	
	public String getDeletableEditText(){
		return mDeletableEditText.getText().toString();
	}
	public void setDeletableEditText(String name){
		mDeletableEditText.setText(name);
	}
}

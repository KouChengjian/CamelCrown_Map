package com.camel.redpenguin.view;

import com.camel.redpenguin.R;

import android.content.Context;
import android.content.DialogInterface;

import android.widget.Button;
import android.widget.EditText;

/**
 * @ClassName: EditTextDialog
 * @Description: edit对话框
 * @author kcj
 * @date
 */
public class EditTextDialog extends BaseDialog {
	
	EditText editText;
	Button btnOk;
	Button btnClose;
	
	public EditTextDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		setDialogContentView(R.layout.include_watch_dialog);
		editText = (EditText)findViewById(R.id.etUserName);
		

		//btnOk = (Button)findViewById(R.id.btn_dialog_ok);
		//btnClose = (Button)findViewById(R.id.btn_dialog_close);
		
	}
	
	
	
	public String getEditText(){
		return editText.getText().toString();
	}
}

package com.camel.redpenguin.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.camel.redpenguin.R;
import com.camel.redpenguin.view.DeletableEditText;

/**
 * @ClassName: ImportIdentifyActivity
 * @Description: 手动输入设备标识
 * @author: KCJ
 * @date: 
 */
public class ImportIdentifyActivity extends BaseActivity{

	TextView tvNext;
	DeletableEditText edtImport;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_importid);
		initTopBarForLeft("手动输入序列号");
		initViews();
		initEvents();
	}
	
	public void initViews(){
		tvNext = (TextView)findViewById(R.id.tv_import_identify_next);
		edtImport = (DeletableEditText)findViewById(R.id.edt_import_identify);
	}
	
    public void initEvents(){
    	tvNext.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				final String str = edtImport.getText().toString();
				if (TextUtils.isEmpty(str)) {
					ShowToast("序列号不为空");
					return;
				}
				Intent intent = new Intent(ImportIdentifyActivity.this,AddDeviceActivity.class);
				intent.putExtra("deviceidentify", str);
				startActivity(intent);
				finish();
			}
		});
	}
}

package com.camel.redpenguin.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;

import com.camel.redpenguin.R;
import com.camel.redpenguin.view.HeaderLayout.onRightImageButtonClickListener;

/**
 * @ClassName: NurseActivity
 * @Description: 随身看护
 * @author kcj
 * @date
 */
public class NurseActivity extends BaseActivity {

	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nurse);
		/** header */
		initTopBarForBoth("随身看护", R.drawable.base_action_bar_more_bg_selector,
				new onRightImageButtonClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
					}
				});

	}

	protected void dialog() {
		AlertDialog.Builder builder = new Builder(NurseActivity.this);
		builder.setMessage("确认退出吗？");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				NurseActivity.this.finish();
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

}

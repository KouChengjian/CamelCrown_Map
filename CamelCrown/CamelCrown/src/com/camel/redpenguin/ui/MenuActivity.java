package com.camel.redpenguin.ui;

import com.camel.redpenguin.R;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @ClassName: MenuActivity
 * @Description:
 * @author: kcj
 * @date: 
 */
public class MenuActivity extends LinearLayout {

	private Context mContext;

	public MenuActivity(Context context) {
		super(context);
		this.mContext = context;
		initViews();
	}

	public MenuActivity(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initViews();
	}

	private void initViews() {
		LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
		View main = (View) inflater.inflate(R.layout.fragment_main_menu, null);
		this.addView(main);
	}
}

package com.camel.redpenguin.ui;

import com.camel.redpenguin.R;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * @ClassName: MainActivity
 * @Description:
 * @author kcj
 * @date
 */
public class ContentActivity extends LinearLayout {

	private Context mContext;

	public ContentActivity(Context context) {
		super(context);
		this.mContext = context;
		initViews();
	}

	public ContentActivity(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initViews();
	}

	private void initViews() {
		LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
		ViewGroup main = (ViewGroup) inflater.inflate(R.layout.fragment_map_display, null);
		this.addView(main);
	}
	
}

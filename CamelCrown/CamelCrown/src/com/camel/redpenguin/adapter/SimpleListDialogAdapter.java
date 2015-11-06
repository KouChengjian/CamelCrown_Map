package com.camel.redpenguin.adapter;

import java.util.List;

import com.camel.redpenguin.R;
import com.camel.redpenguin.adapter.base.BaseArrayListAdapter;
import com.camel.redpenguin.view.HandyTextView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;


/**
 * @ClassName: BaseArrayListAdapter
 * @Description: 注册 -账号(手机号码)国家归属
 * @author kcj
 * @date
 */
public class SimpleListDialogAdapter extends BaseArrayListAdapter{
	
	public SimpleListDialogAdapter(Context context, List<String> datas) {
		super(context, datas);
	}

	public SimpleListDialogAdapter(Context context, String... datas) {
		super(context, datas);
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		if (arg1 == null) {
			arg1 = mInflater.inflate(R.layout.listitem_dialog, null);
		}
		((HandyTextView) arg1.findViewById(R.id.listitem_dialog_text))
				.setText((CharSequence) getItem(arg0));
		return arg1;
	}
}

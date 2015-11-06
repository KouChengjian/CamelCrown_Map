package com.camel.redpenguin.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.camel.redpenguin.R;
import com.camel.redpenguin.adapter.base.BaseContentAdapter;
import com.camel.redpenguin.greendao.CamelDeviceHistory;


/**
 * @ClassName: HistoryContentAdapter
 * @Description: 历史记录
 * @author kcj
 * @date
 */
public class HistoryContentAdapter extends BaseContentAdapter<CamelDeviceHistory>{

	public HistoryContentAdapter(Context context, List<CamelDeviceHistory> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getConvertView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_history, null);
			viewHolder.historyicon = (ImageView)convertView.findViewById(R.id.img_history_icon);
			viewHolder.historyAddress = (TextView)convertView.findViewById(R.id.tv_history_address);
			viewHolder.historyTime = (TextView)convertView.findViewById(R.id.tv_history_time);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		final CamelDeviceHistory entity = dataList.get(position);
		viewHolder.historyTime.setText(entity.getDeviceHistoryCreated());
		return convertView;
	}
	
	public static class ViewHolder{
		public ImageView historyicon;
		public TextView  historyAddress;
		public TextView  historyTime;
	}

}

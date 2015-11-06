package com.camel.redpenguin.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.camel.redpenguin.R;
import com.camel.redpenguin.adapter.base.BaseContentAdapter;
import com.camel.redpenguin.greendao.RelSafetyZone;
import com.camel.redpenguin.util.PixelUtil;

/**
 * @ClassName: HistoryContentAdapter
 * @Description: 安全区域
 * @author kcj
 * @date
 */
public class WatchContentAdapter extends BaseContentAdapter<RelSafetyZone>{

	public WatchContentAdapter(Context context, List<RelSafetyZone> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getConvertView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_watch, null);
			viewHolder.historyicon = (ImageView)convertView.findViewById(R.id.img_watch_icon);
			viewHolder.historyName = (TextView)convertView.findViewById(R.id.tv_watch_name);
			viewHolder.historyAddress = (TextView)convertView.findViewById(R.id.tv_watch_address);
			viewHolder.historyRadius = (TextView)convertView.findViewById(R.id.tv_watch_radius);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		final RelSafetyZone entity = dataList.get(position);
		viewHolder.historyAddress.setText(entity.getRelZoneAddr());
		viewHolder.historyRadius.setText("方园("+entity.getRelZoneRadius()+"米)");
		viewHolder.historyName.setText(entity.getRelZoneName());
		if(entity.getRelZoneName().equals("家")){
			Bitmap btm1 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.watch_sign_home_n);
    		Bitmap mBitmap = Bitmap.createScaledBitmap(btm1, PixelUtil.dp2px(50), PixelUtil.dp2px(50), true);
    		viewHolder.historyicon.setImageBitmap(mBitmap);
		}else if(entity.getRelZoneName().equals("学校")){
			Bitmap btm1 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.watch_sign_school_n);
    		Bitmap mBitmap = Bitmap.createScaledBitmap(btm1, PixelUtil.dp2px(50), PixelUtil.dp2px(50), true);
    		viewHolder.historyicon.setImageBitmap(mBitmap);
		}else if(entity.getRelZoneName().equals("娱乐")){
			Bitmap btm1 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.watch_sign_game_n);
    		Bitmap mBitmap = Bitmap.createScaledBitmap(btm1, PixelUtil.dp2px(50), PixelUtil.dp2px(50), true);
    		viewHolder.historyicon.setImageBitmap(mBitmap);
		}else if(entity.getRelZoneName().equals("其他")){
			Bitmap btm1 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.watch_sign_collect_n);
    		Bitmap mBitmap = Bitmap.createScaledBitmap(btm1, PixelUtil.dp2px(50), PixelUtil.dp2px(50), true);
    		viewHolder.historyicon.setImageBitmap(mBitmap);
		}	
		return convertView;
	}

	public static class ViewHolder{
		public ImageView historyicon;
		public TextView  historyName;
		public TextView  historyAddress;
		public TextView  historyRadius;
	}
}

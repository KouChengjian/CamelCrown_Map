package com.camel.redpenguin.adapter;

import java.io.File;

import java.util.List;

import com.camel.redpenguin.R;

import com.camel.redpenguin.greendao.CamelDevice;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @ClassName: UserDeviceAdapter
 * @Description: 用户下的设备
 * @author: kcj
 * @date: 
 */
public class UserDeviceAdapter extends BaseAdapter{

	private Context ct;
	private List<CamelDevice> data;

	public UserDeviceAdapter(Context ct, List<CamelDevice> datas) {
		this.ct = ct;
		this.data = datas;
	}
	
	/** 当ListView数据发生变化时,调用此方法来更新ListView
	  * @Title: updateListView
	  * @Description: TODO
	  * @param @param list 
	  * @return void
	  * @throws
	  */
	public void updateListView(List<CamelDevice> list) {
		this.data = list;
		notifyDataSetChanged();
	}

	public void remove(CamelDevice user){
		this.data.remove(user);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(ct).inflate(R.layout.item_user_device, null);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.tv_device_name);
			viewHolder.avatar = (ImageView) convertView.findViewById(R.id.img_device_avatar);
			viewHolder.btnSelect = (Button) convertView.findViewById(R.id.btn_device_select);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		CamelDevice friend = data.get(position);
		final String name = friend.getDeviceNike();
		final String avatar = friend.getDeviceAvatar();
		if (!TextUtils.isEmpty(avatar)) {
			File mFile=new File(avatar);
	        //若该文件存在
	        if (mFile.exists()) {
	            Bitmap bitmap=BitmapFactory.decodeFile(avatar);
	            viewHolder.avatar.setImageBitmap(bitmap);
	        }
		} else {
			viewHolder.avatar.setImageDrawable(ct.getResources().getDrawable(R.drawable.default_head));
		}
		viewHolder.name.setText(name);
		
		if(friend.getDeviceSelect().equals("true")){
			viewHolder.btnSelect.setSelected(true);
		}else{
			viewHolder.btnSelect.setSelected(false);
		}
		viewHolder.btnSelect.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(data.get(position).getDeviceSelect().equals("true")){
					return;
				}
				Intent intent = new Intent(); 
				intent.putExtra("position", String.valueOf(position));
				intent.setAction("action.main.refresh.1b");  
				ct.sendBroadcast(intent); 
			}
		});
		return convertView;
	}

	static class ViewHolder {
		ImageView avatar;
		TextView name;
		Button btnSelect;
	}
}

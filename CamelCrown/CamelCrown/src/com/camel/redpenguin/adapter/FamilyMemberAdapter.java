package com.camel.redpenguin.adapter;

import java.util.List;

import com.camel.redpenguin.R;
import com.camel.redpenguin.adapter.UserDeviceAdapter.ViewHolder;
import com.camel.redpenguin.greendao.CamelDevice;
import com.camel.redpenguin.greendao.DataRelFamily;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FamilyMemberAdapter extends BaseAdapter{

	private Context ct;
	private List<DataRelFamily> data;

	public FamilyMemberAdapter(Context ct, List<DataRelFamily> datas) {
		this.ct = ct;
		this.data = datas;
	}

	public void updateListView(List<DataRelFamily> list) {
		this.data = list;
		notifyDataSetChanged();
	}

	public void remove(DataRelFamily user){
		this.data.remove(user);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(ct).inflate(R.layout.item_family_member, null);
			viewHolder = new ViewHolder();
			viewHolder.nick = (TextView) convertView.findViewById(R.id.tv_family_member_nick);
			viewHolder.avatar = (ImageView) convertView.findViewById(R.id.iv_family_member_icon);
			viewHolder.administrator = (TextView) convertView.findViewById(R.id.tv_family_member_administrator);
			viewHolder.account = (TextView) convertView.findViewById(R.id.tv_family_member_account);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		DataRelFamily rel = data.get(position);
		viewHolder.account.setText(rel.getDataRelAccount());
		viewHolder.nick.setText(rel.getDataRelNick());
		if(rel.getDataRelAdministrator().equals("00")){
			viewHolder.administrator.setVisibility(View.GONE);
		}else if(rel.getDataRelAdministrator().equals("01")){
			viewHolder.administrator.setVisibility(View.VISIBLE);
		}
		return convertView;
	}
	
	static class ViewHolder {
		ImageView avatar;
		TextView nick;
		TextView administrator;
		TextView account;
	}
}

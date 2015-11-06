package com.camel.redpenguin.adapter;

import java.util.List;

import com.camel.redpenguin.R;
import com.camel.redpenguin.bean.OfflineItem;






import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @ClassName: OfflineExpandAdapter
 * @Description: 离线地图 - 获取所有城市
 * @author kcj
 * @date
 */
public class OfflineExpandAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private LayoutInflater mInflater = null;
	private String[] mGroupStrings = null;
	private List<List<OfflineItem>> mData = null;
	
	public OfflineExpandAdapter(Context ctx, List<List<OfflineItem>> list) {
		mContext = ctx;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData = list;
        mGroupStrings = new String[list.size()];
        for(int i = 0 ; i < list.size() ; i++){
        	mGroupStrings[i] = list.get(i).get(0).getCityName();
        }
	}
	
	public void setData(List<List<OfflineItem>> list) {
        mData = list;
    }
	
	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}
	
	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return mData.get(groupPosition).size();
	}
	
	@Override
    public List<OfflineItem> getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return mData.get(groupPosition);
    }
	
	@Override
    public OfflineItem getChild(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return mData.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }



	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_offline_group, null);
        }
		GroupViewHolder holder = new GroupViewHolder();
        holder.mGroupName = (TextView) convertView.findViewById(R.id.group_name);
        holder.mGroupCount = (TextView) convertView.findViewById(R.id.group_count);
        holder.mGroupName.setText(mGroupStrings[groupPosition]);
        holder.mGroupCount.setText("[" + mData.get(groupPosition).size() + "]");
        return convertView;
	}


	@Override
	public View getChildView(int groupPosition, int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_offline_child, null);
        }
		ChildViewHolder holder = new ChildViewHolder();
		holder.mChildIcon = (ImageView) convertView.findViewById(R.id.tv_chlid_icon);
		holder.mChildName = (TextView) convertView.findViewById(R.id.tv_chlid_name);
		holder.mChildSize = (TextView) convertView.findViewById(R.id.tv_chlid_size);
		holder.mChildName.setText(getChild(groupPosition, childPosition).getCityName());
		holder.mChildSize.setText(String.valueOf(getChild(groupPosition, childPosition).getSize()));
		return convertView;
	}
	
	private class GroupViewHolder {
        TextView mGroupName;
        TextView mGroupCount;
    }
	
	private class ChildViewHolder {
        ImageView mChildIcon;
        TextView mChildName;
        TextView mChildSize;
    }

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return true;
	}

}

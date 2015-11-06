package com.camel.redpenguin.ui.fragment;


import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.camel.redpenguin.R;
import com.camel.redpenguin.adapter.OfflineExpandAdapter;
import com.camel.redpenguin.bean.OfflineItem;
import com.camel.redpenguin.ui.FragmentBase;
import com.camel.redpenguin.ui.OfflineMapActivity;




/**
 * @ClassName: OfflineAllFragment
 * @Description: 显示所有有离线地图的城市
 * @author kcj
 * @date
 */
@SuppressLint("DefaultLocale")
public class OfflineAllFragment extends FragmentBase implements OnChildClickListener ,MKOfflineMapListener{
	
	private View viewOfflineall ;
	private ExpandableListView mListView = null;
	private OfflineExpandAdapter mOfflineExpandAdapter = null;
	private List<List<OfflineItem>> mOfflineItemData = new ArrayList<List<OfflineItem>>();
	
	ArrayList<OfflineItem> hotCities = new ArrayList<OfflineItem>(); // 热门城市
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		viewOfflineall = inflater.inflate(R.layout.fragment_offlineall, container, false);
		return viewOfflineall;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initOfflineAllFragment(viewOfflineall);
	}
	
	public void initOfflineAllFragment(View v){
        /** 百度离线 */
		//mOffline = new MKOfflineMap();
		mOffline.init(this);
		// 获取热闹城市列表
		ArrayList<MKOLSearchRecord> records = mOffline.getHotCityList();
		if (records != null) {
			for (MKOLSearchRecord r : records) {
				OfflineItem mOfflineItem = new OfflineItem();
				mOfflineItem.setCityID(r.cityID);
				mOfflineItem.setCityName(r.cityName);
				mOfflineItem.setCityType(r.cityType);
				mOfflineItem.setSize(formatDataSize(r.size));
				hotCities.add(mOfflineItem);
			}
		}
		// 获取所有支持离线地图的城市
		ArrayList<MKOLSearchRecord> records2 = mOffline.getOfflineCityList();
		if (records2 != null) {
			for (MKOLSearchRecord r : records2) {
				List<OfflineItem> list = new ArrayList<OfflineItem>();
				OfflineItem mOfflineItem = new OfflineItem();
				mOfflineItem.setCityID(r.cityID);
				mOfflineItem.setCityName(r.cityName);
				mOfflineItem.setCityType(r.cityType);
				mOfflineItem.setSize(formatDataSize(r.size));
				list.add(mOfflineItem);
				// 是否有子城市
				if(r.cityType == 1){
					ArrayList<MKOLSearchRecord> childCities2 = r.childCities;
					for (MKOLSearchRecord r1 : childCities2) {
						OfflineItem mChildOfflineItem = new OfflineItem();
						mChildOfflineItem.setCityID(r1.cityID);
						mChildOfflineItem.setCityName(r1.cityName);
						mChildOfflineItem.setCityType(r1.cityType);
						mChildOfflineItem.setSize(formatDataSize(r1.size));
						list.add(mChildOfflineItem);
					}
					mOfflineItemData.add(list);
				}
			}
		}
		/** UI */
		mListView = (ExpandableListView) v.findViewById(R.id.lv_expandable_offline);
		
		mListView.setGroupIndicator(getResources().getDrawable(
                R.drawable.selector_expander_floder));
		mOfflineExpandAdapter = new OfflineExpandAdapter(getActivity(), mOfflineItemData);
        mListView.setAdapter(mOfflineExpandAdapter);
        mListView.setDescendantFocusability(ExpandableListView.FOCUS_AFTER_DESCENDANTS);
        mListView.setOnChildClickListener(this);
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
            int groupPosition, int childPosition, long id) {
		// TODO Auto-generated method stub
		OfflineItem item = mOfflineExpandAdapter.getChild(groupPosition, childPosition);
		OfflineMapActivity.getInstance().setOfflineItem(item);
		OfflineMapActivity.getInstance().selectRightMiddle();
		Intent intent = new Intent();
		intent.setAction("action.offlinemapdownload");
		getActivity().sendBroadcast(intent);
		return true;
	}
	
	
	
	private boolean hidden;
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if(!hidden){
			refresh();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(!hidden){
			refresh();
		}
	}
	
	public void refresh(){
		try {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					//queryOffline();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void queryOffline(){

	}
	
	@SuppressLint("DefaultLocale")
	public String formatDataSize(int size) {
		String ret = "";
		if (size < (1024 * 1024)) {
			ret = String.format("%dK", size / 1024);
		} else {
			ret = String.format("%.1fM", size / (1024 * 1024.0));
		}
		return ret;
	}

	@Override
	public void onGetOfflineMapState(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	
	
		
}

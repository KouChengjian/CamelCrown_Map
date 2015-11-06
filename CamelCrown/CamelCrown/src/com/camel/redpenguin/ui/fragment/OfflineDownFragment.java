package com.camel.redpenguin.ui.fragment;

import java.util.ArrayList;







import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;


import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;






import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.camel.redpenguin.R;
import com.camel.redpenguin.bean.OfflineItem;
import com.camel.redpenguin.ui.FragmentBase;
import com.camel.redpenguin.ui.OfflineMapActivity;
import com.camel.redpenguin.view.BaseDialog;

/**
 * @ClassName: OfflineDownFragment
 * @Description: 显示已下载城市的列表
 * @author kcj
 * @date
 */
public class OfflineDownFragment extends FragmentBase implements  MKOfflineMapListener{
	View viewOfflineall;
	private BaseDialog mBackDialog;
	// 已下载的离线地图信息列表
	private ArrayList<MKOLUpdateElement> localMapList = null;
	private LocalMapAdapter lAdapter = null;
	OfflineItem mOfflineItem = null;
	
	MKOLUpdateElement element;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		viewOfflineall = inflater.inflate(R.layout.fragment_offlinedown, container, false);
		return viewOfflineall;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initOfflineDownUi(viewOfflineall);
		initBroadcastReceiver();
	}
	
	public void initOfflineDownUi(View v){
		/** 百度离线 */
		mOffline.init(this);
		localMapList = mOffline.getAllUpdateInfo();
		if (localMapList == null) {
			localMapList = new ArrayList<MKOLUpdateElement>();
		}
		/** listview */
		ListView localMapListView = (ListView) v.findViewById(R.id.lv_offline_download);
		lAdapter = new LocalMapAdapter();
		localMapListView.setAdapter(lAdapter);
		/**  
		localMapListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				element = (MKOLUpdateElement) localMapList.get(arg2);
				mBackDialog.show();
			}
		});*/
		localMapListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				element = (MKOLUpdateElement) localMapList.get(position);
				mBackDialog.show();
				return false;
			}
			
		});
		/** 初始化对话框*/
		mBackDialog = BaseDialog.getDialog(getActivity(), "提示",
				"确认要删除离线地图吗么?", "确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						mOffline.remove(element.cityID);
						updateView();
					}
				}, "取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		mBackDialog.setButton1Background(R.drawable.btn_default_popsubmit);
	}
	
	
	/**
	 * 离线地图管理列表适配器
	 */
	public class LocalMapAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return localMapList.size();
		}

		@Override
		public Object getItem(int index) {
			return localMapList.get(index);
		}

		@Override
		public long getItemId(int index) {
			return index;
		}

		@Override
		public View getView(int index, View view, ViewGroup arg2) {
			// TODO Auto-generated method stub
			MKOLUpdateElement e = (MKOLUpdateElement) getItem(index);
			view = View.inflate(getActivity(),R.layout.item_offline_download, null);
			initViewItem(view, e);
			return view;
		}
		
		void initViewItem(View view, final MKOLUpdateElement e) {
			Button display = (Button) view.findViewById(R.id.display);
			Button remove = (Button) view.findViewById(R.id.remove);
			TextView title = (TextView) view.findViewById(R.id.title);
			TextView update = (TextView) view.findViewById(R.id.update);
			TextView ratio = (TextView) view.findViewById(R.id.ratio);
			ratio.setText(e.ratio + "%");
			title.setText(e.cityName);
			if (e.update) {
				update.setText("可更新");
			} else {
				update.setText("最新");
			}
			if (e.ratio != 100) {
				display.setEnabled(true);
			} else {
				display.setEnabled(false);
			}
			// 停止
			remove.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					stop(e.cityID);
					//mOffline.remove(e.cityID);
					//updateView();
				}
			});
			// 开始
			display.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					start(e.cityID);
					//Intent intent = new Intent();
					//intent.putExtra("x", e.geoPt.longitude);
					//intent.putExtra("y", e.geoPt.latitude);
					//intent.setClass(OfflineDemo.this, BaseMapDemo.class);
					//startActivity(intent);
				}
			});
		}
	}
	
	/**
	 *  广播事件
	 */ 
	public void initBroadcastReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("action.offlinemapdownload");
		getActivity().registerReceiver(mRefreshBroadcastReceiver, intentFilter);
	}
	
	/**
	 *  广播接收
	 */
	private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("action.offlinemapdownload")) {
				mOfflineItem = OfflineMapActivity.getInstance().getOfflineItem();
				start(mOfflineItem.getCityID());
			}
		}
	};
	
	/**
	 * 开始下载
	 * 
	 * @param view
	 */
	public void start(int id) {
		int cityid = Integer.parseInt(String.valueOf(id));
		mOffline.start(cityid);
		updateView();
	}
	
	/**
	 * 暂停下载
	 * 
	 * @param view
	 */
	public void stop(int id) {
		int cityid = Integer.parseInt(String.valueOf(id));
		mOffline.pause(cityid);
		updateView();
	}
	
	/**
	 * 更新状态显示
	 */
	public void updateView() {
		localMapList = mOffline.getAllUpdateInfo();
		if (localMapList == null) {
			localMapList = new ArrayList<MKOLUpdateElement>();
		}
		lAdapter.notifyDataSetChanged();
	}

	@Override
	public void onGetOfflineMapState(int type, int state) {
		// TODO Auto-generated method stub
		switch (type) {
		case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
			MKOLUpdateElement update = mOffline.getUpdateInfo(state);
			// 处理下载进度更新提示
			if (update != null) {
				//stateView.setText(String.format("%s : %d%%", update.cityName,update.ratio));
				updateView();
			}
		}
			break;
		case MKOfflineMap.TYPE_NEW_OFFLINE:
			// 有新离线地图安装
			Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
			break;
		case MKOfflineMap.TYPE_VER_UPDATE:
			// 版本更新提示
			// MKOLUpdateElement e = mOffline.getUpdateInfo(state);

			break;
		}
	}
	
	protected void dialog() {
		
	}
}

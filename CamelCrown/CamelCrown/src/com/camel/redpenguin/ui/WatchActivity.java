package com.camel.redpenguin.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.camel.redpenguin.CustomApplcation;
import com.camel.redpenguin.R;
import com.camel.redpenguin.adapter.WatchContentAdapter;
import com.camel.redpenguin.bean.DBHelper;
import com.camel.redpenguin.greendao.RelSafetyZone;
import com.camel.redpenguin.view.BaseDialog;
import com.camel.redpenguin.view.HeaderLayout.onRightImageButtonClickListener;
import com.camel.redpenguin.view.pulltorefresh.library.PullToRefreshBase;
import com.camel.redpenguin.view.pulltorefresh.library.PullToRefreshListView;
import com.camel.redpenguin.view.pulltorefresh.library.PullToRefreshBase.Mode;
import com.camel.redpenguin.view.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.camel.redpenguin.view.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.camel.redpenguin.view.pulltorefresh.library.PullToRefreshBase.State;

/**
 * @ClassName: WatchActivity
 * @Description: 远程守护列表
 * @author kcj
 * @date
 */
public class WatchActivity extends BaseActivity {

	// private static final String relZoneAddr = null;
	private ListView actualListView;
	private ArrayList<RelSafetyZone> mListItems;
	private WatchContentAdapter mAdapter;
	private PullToRefreshListView mPullRefreshListView;

	int position;
	private BaseDialog mBackDialog;

	public static RelSafetyZone mIntentTransmit = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watch);
		mIntentTransmit = null;
		/** header */
		initTopBarForBoth("远程守护", R.drawable.base_action_bar_more_bg_selector,
				new onRightImageButtonClickListener() {
					@Override
					public void onClick(View v) {
						startAnimActivity(WatchMapActivity.class);
					}
				});
		/** 初始化listView */
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_watch_list);
		mPullRefreshListView.setMode(Mode.DISABLED); // BOTH
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {

					}

					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {

					}
				});
		mPullRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
					@Override
					public void onLastItemVisible() {

					}
				});
		actualListView = mPullRefreshListView.getRefreshableView();
		mListItems = new ArrayList<RelSafetyZone>();
		mAdapter = new WatchContentAdapter(mContext, mListItems);
		actualListView.setAdapter(mAdapter);
		if (mListItems.size() == 0) {
			// fetchData();
		}
		mPullRefreshListView.setState(State.RELEASE_TO_REFRESH, true);
		actualListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				RelSafetyZone mRelSafetyZone = mListItems.get(position - 1);
				mIntentTransmit = mRelSafetyZone;
				/*mIntentTransmit = new IntentTransmit();
				mIntentTransmit.setRelZoneAddr(mRelSafetyZone.getRelZoneAddr());
				// Log.e("TAG1", mIntentTransmit.getRelZoneAddr());
				mIntentTransmit.setRelZoneIdentify(mRelSafetyZone
						.getRelZoneIdentify());
				// Log.e("TAG1", mIntentTransmit.getRelZoneIdentify());
				mIntentTransmit.setRelZoneName(mRelSafetyZone.getRelZoneName());
				// Log.e("TAG1", mIntentTransmit.getRelZoneName());
				mIntentTransmit.setRelZoneLatitude(mRelSafetyZone
						.getRelZoneLatitude());
				// Log.e("TAG1", mIntentTransmit.getRelZoneLatitude());
				mIntentTransmit.setRelZoneLongtitude(mRelSafetyZone
						.getRelZoneLongtitude());
				// Log.e("TAG1", mIntentTransmit.getRelZoneLongtitude());
				mIntentTransmit.setRelZoneRadius(mRelSafetyZone
						.getRelZoneRadius());
				// Log.e("TAG1", mIntentTransmit.getRelZoneRadius());
*/				Intent intent = new Intent();
				intent.setClass(WatchActivity.this, WatchMapActivity.class);
				startActivity(intent);
			}
		});

		actualListView
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int position, long arg3) {
						WatchActivity.this.position = position - 1;
						mBackDialog.show();
						return true;
					}

				});
	}

	@Override
	protected void onResume() {
		super.onResume();
		mIntentTransmit = null;
		refresh();
		initDialog();
	}

	public void refresh() {
		try {
			this.runOnUiThread(new Runnable() {
				public void run() {
					queryMyDevices();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void queryMyDevices() {
		if (CustomApplcation.getInstance().getCurrentCamelDevice() == null) {
			ShowToast("无选择设备");
			return;
		}
		String id = CustomApplcation.getInstance().getCurrentCamelDevice()
				.getDeviceIdentify();
		if (!DBHelper.getInstance(mContext).isRelZoneSaved(id)) {
			ShowToast("无选择设备的区域");
			return;
		}
		List<RelSafetyZone> relSafetyZone = DBHelper.getInstance(mContext)
				.getRelZoneIdentify(id);
		fetchData(relSafetyZone);
		if (mAdapter == null) {
			mAdapter = new WatchContentAdapter(mContext, mListItems);
			actualListView.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}
	}

	public void fetchData(List<RelSafetyZone> relSafetyZone) {
		mListItems.clear();
		for (int i = 0; i < relSafetyZone.size(); i++) {
			RelSafetyZone mRelSafetyZone = relSafetyZone.get(i);
			RelSafetyZone relSafetyZones = new RelSafetyZone();
			relSafetyZones.setRelZoneAddr(mRelSafetyZone.getRelZoneAddr());
			relSafetyZones.setRelZoneIdentify(mRelSafetyZone
					.getRelZoneIdentify());
			relSafetyZones.setRelZoneName(mRelSafetyZone.getRelZoneName());
			relSafetyZones.setRelZoneLatitude(mRelSafetyZone
					.getRelZoneLatitude());
			relSafetyZones.setRelZoneLongtitude(mRelSafetyZone
					.getRelZoneLongtitude());
			relSafetyZones.setRelZoneRadius(mRelSafetyZone.getRelZoneRadius());
			relSafetyZones
					.setRelZoneCreated(mRelSafetyZone.getRelZoneCreated());
			mListItems.add(relSafetyZones);
		}
	}

	/**
	 * 初始化对话框
	 */
	public void initDialog() {
		mBackDialog = BaseDialog.getDialog(this, "提示", "确认要删除此区域吗?", "确认",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DBHelper.getInstance(mContext).deleteRelZoneList(mListItems.get(position).getRelZoneCreated());
						mListItems.remove(position);
						mAdapter.notifyDataSetChanged();
						dialog.dismiss();
					}
				}, "取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		mBackDialog.setButton1Background(R.drawable.btn_default_popsubmit);
	}
}

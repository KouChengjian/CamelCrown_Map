package com.camel.redpenguin.ui;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mirko.android.datetimepicker.date.DatePickerDialog;
import mirko.android.datetimepicker.date.DatePickerDialog.OnDateSetListener;

import com.camel.redpenguin.CustomApplcation;
import com.camel.redpenguin.R;
import com.camel.redpenguin.adapter.HistoryContentAdapter;
import com.camel.redpenguin.bean.DBHelper;
import com.camel.redpenguin.bean.HistoryItem;
import com.camel.redpenguin.greendao.CamelDeviceHistory;
import com.camel.redpenguin.util.LatLngDistanceUtils;
import com.camel.redpenguin.view.HeaderLayout.onRightImageButtonClickListener;
import com.camel.redpenguin.view.pulltorefresh.library.PullToRefreshBase;
import com.camel.redpenguin.view.pulltorefresh.library.PullToRefreshBase.Mode;
import com.camel.redpenguin.view.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.camel.redpenguin.view.pulltorefresh.library.PullToRefreshBase.State;
import com.camel.redpenguin.view.pulltorefresh.library.PullToRefreshListView;
import com.camel.redpenguin.view.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @ClassName: HistoryActivity
 * @Description:
 * @author: kcj
 * @date: 
 */
public class HistoryActivity extends BaseActivity {

	private ProgressBar progressbar;
	private ListView actualListView;
	private ArrayList<CamelDeviceHistory> mListItems;
	private HistoryContentAdapter mAdapter;
	private PullToRefreshListView mPullRefreshListView;

	private ArrayList<HistoryItem> mHistoryItem  = new ArrayList<HistoryItem>();
	
	// 日期控件
	//private TextView tvDisplayDate;
	private final Calendar mCalendar = Calendar.getInstance();
	//private int day = mCalendar.get(Calendar.DAY_OF_MONTH);
	//private int month = mCalendar.get(Calendar.MONTH);
	//private int year = mCalendar.get(Calendar.YEAR);

	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		/** header */
		initTopBarForBoth("今天", R.drawable.base_action_bar_more_bg_selector,
				new onRightImageButtonClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						startAnimActivity(HistoryMapActivity.class);
					}
				});
		/** 初始化日历 */
		initDate();
		/** 初始化listView */
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		progressbar = (ProgressBar) findViewById(R.id.pull_refresh_progressBar);
		mPullRefreshListView.setMode(Mode.PULL_FROM_START); // BOTH
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub
						mPullRefreshListView.setState(State.RESET, true);

					}

					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub
					}
				});
		mPullRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						// TODO Auto-generated method stub

					}
				});
		actualListView = mPullRefreshListView.getRefreshableView();
		mListItems = new ArrayList<CamelDeviceHistory>();
		mAdapter = new HistoryContentAdapter(mContext, mListItems);
		actualListView.setAdapter(mAdapter);
		if (mListItems.size() == 0) {
			//refresh();
		}
		mPullRefreshListView.setState(State.RELEASE_TO_REFRESH, true);
		actualListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		refresh();
	}

	public void refresh(){
		try {
			this.runOnUiThread(new Runnable() {
				public void run() {
					queryCurrentDevicesInfo();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	private void queryCurrentDevicesInfo(){
		if(CustomApplcation.getInstance().getCurrentCamelDevice() == null){
			ShowToast("无选择设备");
			return;
		}
		String id = CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify();
		if(!DBHelper.getInstance(mContext).isDeviceHistorySaved(id)){
			ShowToast("无选择历史信息");
			return;
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd");       
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
		// 获取id下的所有信息
		List<CamelDeviceHistory> deviceState = DBHelper.getInstance(mContext).getDeviceHistoryMessage(id);
		fetchData(deviceState,formatter.format(curDate));
		if(mAdapter == null){
			mAdapter = new HistoryContentAdapter(mContext, mListItems);
			actualListView.setAdapter(mAdapter);
		}else{
			mAdapter.notifyDataSetChanged();
		}
	}
	
	public void fetchData(List<CamelDeviceHistory> datas,String time) {
		mListItems.clear();
		// 提取当天时间的数据
		List<CamelDeviceHistory> mAllListItems = new ArrayList<CamelDeviceHistory>();
		for(int i = 0 ; i < datas.size() ;i++){
			CamelDeviceHistory camelDeviceState = datas.get(i);
	    	if(camelDeviceState.getDeviceHistoryCreated().contains(time)){
	    		mAllListItems.add(camelDeviceState);
	    	}	
	    }
		// 对当天数据进行整理
		for(int i = 0 ; i < mAllListItems.size() ;i++){
		    CamelDeviceHistory camelDevice = mAllListItems.get(i);
			if(i == 0 ){
				mListItems.add(camelDevice);
			}else{
				CamelDeviceHistory camelDeviceNext = mAllListItems.get(i-1);
				double distance = LatLngDistanceUtils.DistanceOfTwoPoints(
						Double.valueOf(camelDevice.getDeviceHistoryLatitude()).doubleValue(), 
						Double.valueOf(camelDevice.getDeviceHistoryLongtitude()).doubleValue(), 
						Double.valueOf(camelDeviceNext.getDeviceHistoryLatitude()).doubleValue(), 
						Double.valueOf(camelDeviceNext.getDeviceHistoryLongtitude()).doubleValue());
				if(distance > 50){
					mListItems.add(camelDevice);
				}
			}
		}
		
		
/*		// 对当天数据进行整理
		List<CamelDeviceHistory> mdisposeItems = new ArrayList<CamelDeviceHistory>();
		for(int i = 0 ; i < mAllListItems.size() ;i++){
			CamelDeviceHistory camelDevice = mAllListItems.get(i);
//			Log.e("TAG", camelDevice.getDeviceHistoryIdentify());
//			Log.e("TAG", camelDevice.getDeviceHistoryCreated());	
			if(i == 0 ){
				String star = camelDevice.getDeviceHistoryCreated().split(" ")[1];
				camelDevice.setDeviceHistoryCreated(star);
				mdisposeItems.add(camelDevice);

			}else{
				CamelDeviceHistory camelDeviceNext = mAllListItems.get(i-1);
				double distance = LatLngDistanceUtils.DistanceOfTwoPoints(
						Double.valueOf(camelDevice.getDeviceHistoryLatitude()).doubleValue(), 
						Double.valueOf(camelDevice.getDeviceHistoryLongtitude()).doubleValue(), 
						Double.valueOf(camelDeviceNext.getDeviceHistoryLatitude()).doubleValue(), 
						Double.valueOf(camelDeviceNext.getDeviceHistoryLongtitude()).doubleValue());
				if(distance > 50){
					String star = camelDevice.getDeviceHistoryCreated().split(" ")[1];
					camelDevice.setDeviceHistoryCreated(star);
					mdisposeItems.add(camelDevice);
				}else{
					if(mdisposeItems.size() > 0){
						int id = mdisposeItems.size()-1;
						Log.e("TAG", String.valueOf(id));
						// 获取上一个数据
						CamelDeviceHistory mCamelDeviceHistory = mdisposeItems.get(id);
					    if(mCamelDeviceHistory != null){
					    	String star = mCamelDeviceHistory.getDeviceHistoryCreated();
						    String end = camelDevice.getDeviceHistoryCreated().split(" ")[1];
						    int _star1 = Integer.valueOf(star.split(":")[0]).intValue();
						    int _star2 = Integer.valueOf(star.split(":")[1]).intValue();
						    int _end1 = Integer.valueOf(end.split(":")[0]).intValue();
						    int _end2 = Integer.valueOf(end.split(":")[1]).intValue();
						    int hour = _end1 - _star1 ;
						    int minute = 0 ;
						    if(_end2 >= _star2){
						    	minute = _end2 - _star2;
						    }else{
						    	minute = _star2 - _end2;
						    }
						    
						    Log.e("hour", String.valueOf(hour));
						    Log.e("minute", String.valueOf(minute));
						    mCamelDeviceHistory.setDeviceHistoryCreated(star);
						    mCamelDeviceHistory.setDeviceHistoryTermination(end);
						    if(hour == 0){
						    	mCamelDeviceHistory.setDeviceHistoryAllTime(String.valueOf(minute)+"分钟");
						    }else{
						    	mCamelDeviceHistory.setDeviceHistoryAllTime(String.valueOf(hour)+"小时"+String.valueOf(minute)+"分钟");
						    }
						    Log.e("AllTime", mCamelDeviceHistory.getDeviceHistoryAllTime());
						    mdisposeItems.remove(id);
						    mdisposeItems.add(id, mCamelDeviceHistory);	
					    }    
					}
				}
			}
		}
		 Log.e("整合修改", String.valueOf(mdisposeItems.size()));
		// 数据填充
		for(int i = 0 ; i < mdisposeItems.size() ; i++){
			Log.e("mListItems", "1");
			mListItems.add(mdisposeItems.get(i));
		}*/
		
	}

	
	/**
	 *  初始化时间控件
	 */
	public void initDate(){
		setTopBarForOnlyTitle("今天");
		// setTopBarForOnlyTitle(new StringBuilder().append(pad(year)).append("-").append(pad(month + 1)).append("-").append(pad(day)).toString());
		final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
				new OnDateSetListener() {
					@SuppressLint("InlinedApi")
					public void onDateSet(DatePickerDialog datePickerDialog,
							int year, int month, int day) {
						setTopBarForOnlyTitle(new StringBuilder()
						.append(pad(year)).append("-")
						.append(pad(month + 1)).append("-")
						.append(pad(day)).toString());
					}

				}, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
				mCalendar.get(Calendar.DAY_OF_MONTH));

		findViewById(R.id.iv_history_date).setOnClickListener(
				new OnClickListener() {

					private String tag;

					@TargetApi(Build.VERSION_CODES.HONEYCOMB)
					@SuppressLint("NewApi")
					@Override
					public void onClick(View v) {
						datePickerDialog.show(getFragmentManager(), tag);
					}
				});
	}
	
	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}
}

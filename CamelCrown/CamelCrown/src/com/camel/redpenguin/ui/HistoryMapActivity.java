package com.camel.redpenguin.ui;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import mirko.android.datetimepicker.date.DatePickerDialog;
import mirko.android.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ZoomControls;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.camel.redpenguin.CustomApplcation;
import com.camel.redpenguin.R;
import com.camel.redpenguin.bean.DBHelper;
import com.camel.redpenguin.config.Config;
import com.camel.redpenguin.config.SocketInit;
import com.camel.redpenguin.greendao.CamelDevice;
import com.camel.redpenguin.greendao.CamelDeviceHistory;
import com.camel.redpenguin.greendao.RelSafetyZone;
import com.camel.redpenguin.listener.FindStateListener;
import com.camel.redpenguin.listener.SaveListener;
import com.camel.redpenguin.view.HeaderLayout.onRightImageButtonClickListener;

/**
 * @ClassName: HistoryMapActivity
 * @Description: 历史轨迹查看
 * @author: KCJ
 * @date: 
 */
public class HistoryMapActivity extends BaseActivity {

	private double latitude = 0;
	private double longtitude = 0;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private String id;
	private TextView mapStep;
	private List<LatLng> latlngList = new ArrayList<LatLng>();
	
	// 时间
	private final Calendar mCalendar = Calendar.getInstance();
	private DatePickerDialog datePickerDialog;
	private String tag;

	private CamelDevice camel = null;
	private String selectTime = null;
//	private Button btnIcon;
	private TextView popupText = null;// 泡泡view
	private BitmapDescriptor safetySign;
	private int nodeIndex = -1;// 节点索引,供浏览节点时使用
	private List<CamelDeviceHistory> deviceState = new ArrayList<CamelDeviceHistory>();

	@SuppressLint("SimpleDateFormat")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_historymap);
		latitude = Double.valueOf(CustomApplcation.getInstance().getLatitude());
		longtitude = Double.valueOf(CustomApplcation.getInstance().getLongtitude());
		/** header */
		initTopBarForBoth("历史数据", R.drawable.selector_date_time,new onRightImageButtonClickListener() {
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
//				mBaiduMap.clear();
				datePickerDialog.show(getFragmentManager(), tag);
			}
		});
		initView();
		initBaidu();
		initDate();
	}

	public void initView(){
		mapStep = (TextView)findViewById(R.id.tv_map_step);
	}
	
	public void initBaidu(){
		mMapView = (MapView) findViewById(R.id.mv_map_display);
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 移动至位置
		LatLng ll = new LatLng(latitude, longtitude);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		mBaiduMap.animateMapStatus(u); // 以动画方式更新地图状态，动画耗时 300 ms
		// 图层
		float zoomLevel = Float.parseFloat("14");
		MapStatusUpdate u1 = MapStatusUpdateFactory.zoomTo(zoomLevel);
		mBaiduMap.animateMapStatus(u1);
		// 隐藏百度地图自带的放大缩小
		int childCount = mMapView.getChildCount();// 隐藏缩放控件
		View zoom = null;
		for (int i = 0; i < childCount; i++) {
			View child = mMapView.getChildAt(i);
			if (child instanceof ZoomControls) {
				zoom = child;
				break;
			}
		}
		zoom.setVisibility(View.GONE);
		// 删除百度地图LOGO
		mMapView.removeViewAt(1);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// activity 暂停时同时暂停地图控件
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// activity 恢复时同时恢复地图控件
		mMapView.onResume();
		refresh();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// activity 销毁时同时销毁地图控件
		mMapView.onDestroy();
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

	@SuppressLint({ "ResourceAsColor", "SimpleDateFormat" })
	public void queryMyDevices() {
		if (CustomApplcation.getInstance().getCurrentCamelDevice() == null) {
			ShowToast("无选择设备");
			return;
		}
		nodeIndex = -1;
		deviceState.clear();
		mBaiduMap.clear();
		id = CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify();
		if (DBHelper.getInstance(mContext).isRelZoneSaved(id)) {
			/** 先显示安全区域内容 */
			List<RelSafetyZone> relSafetyZone = DBHelper.getInstance(mContext)
					.getRelZoneIdentify(id);
			for (int i = 0; i < relSafetyZone.size(); i++) {
				RelSafetyZone mRelSafetyZone = relSafetyZone.get(i);
				LatLng latlng = new LatLng(Double.valueOf(
						mRelSafetyZone.getRelZoneLatitude()).doubleValue(), Double
						.valueOf(mRelSafetyZone.getRelZoneLongtitude())
						.doubleValue());
				if (mRelSafetyZone.getRelZoneName().equals("家")) {
					safetySign = BitmapDescriptorFactory
							.fromResource(R.drawable.watch_location_icon_home_text);
				} else if (mRelSafetyZone.getRelZoneName().equals("学校")) {
					safetySign = BitmapDescriptorFactory
							.fromResource(R.drawable.watch_location_icon_school_text);
				} else if (mRelSafetyZone.getRelZoneName().equals("娱乐")) {
					safetySign = BitmapDescriptorFactory
							.fromResource(R.drawable.watch_location_icon_game_text);
				} else if (mRelSafetyZone.getRelZoneName().equals("其他")) {
					safetySign = BitmapDescriptorFactory
							.fromResource(R.drawable.watch_location_icon_collect_text);
				}
				OverlayOptions ooA = new MarkerOptions().position(latlng).icon(safetySign);
				mBaiduMap.addOverlay(ooA);
				// 添加圆
				OverlayOptions ooCircle = new CircleOptions().fillColor(0x20468fdd).center(latlng).stroke(new Stroke(2, 0xff0f81d9))
						.radius(Integer.valueOf(mRelSafetyZone.getRelZoneRadius()).intValue());
				mBaiduMap.addOverlay(ooCircle);
			}
		}
		/** 历史信息 */
		String presentTime = null;
		if(TextUtils.isEmpty(selectTime)){
			return ;
		}
		presentTime = selectTime;
		setTopBarForOnlyTitle(presentTime);
		// 获取id下的所有信息
		deviceState = DBHelper.getInstance(mContext).getDeviceHistoryMessage(id ,presentTime);
		/*Log.e("deviceState.size", String.valueOf(deviceState.size()));
		// 提取当天时间的数据
		for (int i = 0; i < deviceState.size(); i++) {
			CamelDeviceHistory camelDeviceState = deviceState.get(i);
			Log.e("mAllListItems", camelDeviceState.getDeviceHistoryCreated() + "   " +camelDeviceState.getDeviceHistoryTermination());
		}*/
		if(deviceState.size() == 0){
			if(!SocketInit.getInstance().isCanSend()){
				ShowToast("无连接...");
				return;
			}
			showLoadingDialog("正在获取选择的历史记录...");
			Config.HISTORY_PAGE = 1;
			getTime();
			return;
		}
		latlngList.clear();
		BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.drawable.main_child_logo);
		for (int i = 0; i < deviceState.size(); i++) {
			CamelDeviceHistory camelDeviceState1 = deviceState.get(i);
			mapStep.setText(camelDeviceState1.getDeviceHistoryStep());
			LatLng nodeLocation = new LatLng(Double.valueOf(camelDeviceState1.getDeviceHistoryLatitude()).doubleValue(), Double.valueOf(camelDeviceState1.getDeviceHistoryLongtitude()).doubleValue());
			latlngList.add(nodeLocation);
			OverlayOptions ooA = new MarkerOptions().position(nodeLocation).icon(bdA);
			mBaiduMap.addOverlay(ooA);
			if(i == 0){
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(nodeLocation);
				mBaiduMap.animateMapStatus(u); // 以动画方式更新地图状态，动画耗时 300 ms
			}
		}
		if(!TextUtils.isEmpty(camel.getDeviceLigature()) && camel.getDeviceLigature().equals("02")){
			OverlayOptions ooPolyline = new PolylineOptions().width(3).color(0xAAFF0000).points(latlngList);
			mBaiduMap.addOverlay(ooPolyline);
		}
	}

	public void nodeClick(View v) {
		if (nodeIndex == -1 && v.getId() == R.id.pre) {
			return;
		}
		// 设置节点索引
		if (v.getId() == R.id.next) {
			if (nodeIndex < deviceState.size() - 1) {
				nodeIndex++;
			} else {
				return;
			}
		} else if (v.getId() == R.id.pre) {
			if (nodeIndex > 0) {
				nodeIndex--;
			} else {
				return;
			}
		}
		// 获取节结果信息
		LatLng nodeLocation = null;
		String nodeTitle = null;
		CamelDeviceHistory camelDeviceState = deviceState.get(nodeIndex);
		nodeLocation = new LatLng(Double.valueOf(
				camelDeviceState.getDeviceHistoryLatitude()).doubleValue(),
				Double.valueOf(camelDeviceState.getDeviceHistoryLongtitude())
						.doubleValue());
		nodeTitle = camelDeviceState.getDeviceHistoryTermination();
		if (nodeLocation == null || nodeTitle == null) {
			return;
		}
		// 移动节点至中心
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
		// show popup
		popupText = new TextView(this);
		popupText.setBackgroundResource(R.drawable.popup);
		popupText.setTextColor(0xFF000000);
		popupText.setText(nodeTitle);
		mBaiduMap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));
	}

	/**
	 * 初始化时间控件
	 */
	@SuppressLint("SimpleDateFormat")
	public void initDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		selectTime = formatter.format(curDate);
		datePickerDialog = DatePickerDialog.newInstance(new OnDateSetListener() {
			@SuppressLint("InlinedApi")
			public void onDateSet(DatePickerDialog datePickerDialog,int year, int month, int day) {
				selectTime = new StringBuilder().append(pad(year)).append("-").append(pad(month + 1)).append("-").append(pad(day)).toString();		
				// 查询是否存在数据
				deviceState = DBHelper.getInstance(mContext).getDeviceHistoryMessage(id ,selectTime);
				if(deviceState.size() == 0){
					showLoadingDialog("正在获取选择的历史记录...");
					Config.HISTORY_PAGE = 1;
					getTime();
				}else{
					showLoadingDialog("正在获取本地数据...");
					queryLength(selectTime ,deviceState.size());
				}
			}
		}, 
		mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),mCalendar.get(Calendar.DAY_OF_MONTH));
	
		if(CustomApplcation.getInstance().getCurrentCamelDevice() == null){
			return;
		}
		String id = CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify();
		if(DBHelper.getInstance(mContext).isDevicesInfoSaved(id)){
			camel = DBHelper.getInstance(mContext).getAssignDevices(id).get(0);
		}else{
			camel = null;
		}
	}

	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}
	
	public void getTime(){
		SocketInit.getInstance().queryCurrentDeviceState(Config.HISTORY_PAGE,selectTime, new FindStateListener() {
			@Override
			public void onSuccess() 
			{
				try 
				{
					runOnUiThread(new Runnable() 
					{
					    public void run() 
					    {
							new Handler().postDelayed(new Runnable() 
							{
								public void run() 
								{
									Config.HISTORY_PAGE++;
									getTime();
								}
							}, 2000);
						}
					});
				} catch (Exception e) 
				{
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int paramInt, String paramString) 
			{
				dismissLoadingDialog();
				refresh();
			}
		});
	}
	
	public void queryLength(String time ,final int length){
		SocketInit.getInstance().queryHistoryLength(time,new SaveListener()
		{
			@Override
			public void onSuccess() 
			{
				try 
				{
					runOnUiThread(new Runnable() 
					{
					    public void run() 
					    {
					    	Log.e("长度", String.valueOf(length) +"   "+ String.valueOf(Config.HISTORY_PAGE_LENGTH));
							if(length == Config.HISTORY_PAGE_LENGTH)
							{
								refresh();
								dismissLoadingDialog();
							}
							else
							{
								showLoadingDialog("正在获取选择的历史记录...");
								Config.HISTORY_PAGE = 1;
								getTime();
							}
						}
					});
				} catch (Exception e) 
				{
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int paramInt, String paramString) 
			{
				ShowToast("查询失败");
				dismissLoadingDialog();
			}
		});
	}
}

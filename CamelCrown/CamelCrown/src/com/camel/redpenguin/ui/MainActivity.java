package com.camel.redpenguin.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.camel.redpenguin.CustomApplcation;
import com.camel.redpenguin.R;
import com.camel.redpenguin.adapter.UserDeviceAdapter;
import com.camel.redpenguin.bean.DBHelper;
import com.camel.redpenguin.config.SocketInit;
import com.camel.redpenguin.greendao.CamelDevice;
import com.camel.redpenguin.greendao.CamelDevicePush;
import com.camel.redpenguin.listener.SaveListener;
import com.camel.redpenguin.util.PixelUtil;
import com.camel.redpenguin.util.PowerUtils;
import com.camel.redpenguin.view.BaseDialog;
import com.camel.redpenguin.view.CircleImageView;
import com.camel.redpenguin.view.HeaderLayout.onRightImageButtonClickListener;
import com.camel.redpenguin.view.HeaderLayout.onLeftImageButtonClickListener;
import com.camel.redpenguin.view.slidingdrawer.MenuDrawer;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;
import android.widget.PopupWindow.OnDismissListener;

/**
 * @ClassName: MainActivity
 * @Description: 组界面
 * @author: KCJ
 * @date: 
 */ 
public class MainActivity extends BaseActivity implements OnClickListener , OnItemClickListener,OnItemLongClickListener ,OnGetGeoCoderResultListener{
	private View vMenu;
	private View vContent;
	private String mHeaderTitle;
	private BaseDialog mBackDialog;
	/** popup */
	private View mPopView;
	private PopupWindow mPopupWindow;
	private RelativeLayout rlHistory;
	private RelativeLayout rlMessage;
	private RelativeLayout rlSafetyArea;
	private RelativeLayout rlFamily;
	/** 侧滑菜单 */
	private MenuDrawer mDrawer;
	private CircleImageView circleImgView;
	private LinearLayout llMenuHeaderInfo;
	private TextView tvInfo;
	private Button btnSetting;
	private Button btnFeedback;
	LinearLayout layout_code;
	LinearLayout layout_add;
	ListView list_devices;
	private UserDeviceAdapter userAdapter;
	List<CamelDevice> devices = new ArrayList<CamelDevice>();
	/** 主界面map显示的状态 */
	private TextView tvDeviceAddress;
	private TextView tvDeviceTime;  
	private TextView tvDeviceStep;  
	private ImageView imgDeviceLocation;
	private ImageView imgDeviceElectric;
	private ImageView imgDeviceLocaMode;
	private TextView tvDeviceElectric;  
	private RelativeLayout rlDeviceElectric;  
	/** 百度 */
	private double latitude = 0;
	private double longtitude = 0;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private LocationClient mLocClient;
	private MyLocationListenner myListener = new MyLocationListenner();
	private boolean isFirstLoc = true;// 是否首次定位
	private GeoCoder mGeoCoder = null; 
	// 当前界面是否在最前面
	public static boolean activityRun = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/** 初始化抽屉和主界面内容 */
		agent.sync();
		mHeaderTitle = "无设备";
		mDrawer = MenuDrawer.attach(this, MenuDrawer.Type.OVERLAY);
		vMenu = new MenuActivity(MainActivity.this);
		vContent = new ContentActivity(MainActivity.this);
		mDrawer.setMenuView(vMenu);// 设置左侧菜单UI
		mDrawer.setContentView(vContent);// 设置主体UI
		openHeaderTitle();
		initBaiDuMap();
		initView();
		initEvents();
	}
	
	/**
	 * 界面UI
	 */
	@SuppressLint("InflateParams")
	public void initView(){ 
		/** 主界面Popup菜单 */
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		mPopView = layoutInflater.inflate(R.layout.include_popup_menu, null);
		rlHistory = (RelativeLayout) mPopView.findViewById(R.id.rl_main_popup_history);
		rlMessage = (RelativeLayout) mPopView.findViewById(R.id.rl_main_popup_message);
		rlSafetyArea = (RelativeLayout) mPopView.findViewById(R.id.rl_main_popup_safetyarea);
		rlFamily = (RelativeLayout) mPopView.findViewById(R.id.rl_main_popup_location);
		mPopupWindow = new PopupWindow(mPopView, LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				openHeaderTitle();
			}
		});	
		/** 主界面的控件 */
		tvDeviceAddress = (TextView)findViewById(R.id.tv_display_showinfo_addr);
		tvDeviceTime = (TextView)findViewById(R.id.tv_display_showinfo_time);  
		tvDeviceStep = (TextView)findViewById(R.id.tv_display_showinfo_step);  
		imgDeviceLocation = (ImageView)findViewById(R.id.rl_display_showinfo_icon_lication);
		imgDeviceElectric = (ImageView)findViewById(R.id.img_display_electric);
		imgDeviceLocaMode = (ImageView)findViewById(R.id.rl_display_showinfo_icon_mode);
		tvDeviceElectric = (TextView)findViewById(R.id.tv_display_electric_percent);
		rlDeviceElectric = (RelativeLayout)findViewById(R.id.rl_display_electric_percent);  
		imgDeviceElectric.setBackgroundResource(R.drawable.display_electric_0);
		Bitmap mBitmap1 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(),R.drawable.display_showinfo_icon_null), PixelUtil.dp2px(32), PixelUtil.dp2px(32), true);
		imgDeviceLocaMode.setImageBitmap(mBitmap1);
		/** 侧滑菜单的控件 */
		circleImgView = (CircleImageView) findViewById(R.id.img_menu_icon);
		Bitmap btm1 = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.default_head);
		Bitmap mBitmap = Bitmap.createScaledBitmap(btm1, PixelUtil.dp2px(75), PixelUtil.dp2px(75), true);
		circleImgView.setImageBitmap(mBitmap);
		llMenuHeaderInfo = (LinearLayout)findViewById(R.id.ll_menu_header_info);
		tvInfo = (TextView)findViewById(R.id.tv_menu_header_info);
		btnSetting = (Button)findViewById(R.id.btn_menu_set);
		btnFeedback = (Button)findViewById(R.id.btn_menu_feedback);
		initDeviceList(layoutInflater);
	}
	
	/**
	 *  初始化菜单设备list
	 */
	@SuppressLint("InflateParams")
	public void initDeviceList(LayoutInflater mInflater){
		list_devices = (ListView)findViewById(R.id.lv_menu_devices);
		RelativeLayout headView = (RelativeLayout) mInflater.inflate(R.layout.include_add_device, null);
		layout_code = (LinearLayout)headView.findViewById(R.id.layout_code);
		layout_add  = (LinearLayout)headView.findViewById(R.id.layout_add);
		layout_code.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startAnimActivity(CodeCreateActivity.class);
			}
		});
		layout_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startAnimActivity(CodeScanActivity.class);
			}
		});
		list_devices.addHeaderView(headView);
		userAdapter = new UserDeviceAdapter(this , devices);
		list_devices.setAdapter(userAdapter);
		list_devices.setOnItemClickListener(this);
		list_devices.setOnItemLongClickListener(this);
	}
	
	public void initEvents(){
		rlHistory.setOnClickListener(this);
		rlMessage.setOnClickListener(this);
		rlSafetyArea.setOnClickListener(this);
		rlFamily.setOnClickListener(this);
		
		imgDeviceElectric.setOnClickListener(this);
		imgDeviceLocation.setOnClickListener(this);
		
		circleImgView.setOnClickListener(this);
		llMenuHeaderInfo.setOnClickListener(this);
		btnSetting.setOnClickListener(this);
		btnFeedback.setOnClickListener(this);
	}
	
	public void initBaiDuMap(){
		mMapView = (MapView) findViewById(R.id.mv_display);
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
//		mBaiduMap.setMyLocationEnabled(true);
		/** 初始化定位 */
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		/** 设置地图 */
		// 地图图层
		float zoomLevel = Float.parseFloat("18");
		MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(zoomLevel);
		mBaiduMap.animateMapStatus(u);
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
		// 删除百度地图logo
//		mMapView.removeViewAt(1);
		// 编码
		mGeoCoder = GeoCoder.newInstance();
		mGeoCoder.setOnGetGeoCodeResultListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_main_popup_history: // pop - 历史位置
			mPopupWindow.dismiss();
			startAnimActivity(HistoryMapActivity.class);
			break;
		case R.id.rl_main_popup_location: // pop - 定位模式
			mPopupWindow.dismiss();
			startAnimActivity(TrackModelActivity.class);
			break;
		case R.id.rl_main_popup_safetyarea: // pop - 远程模式
			mPopupWindow.dismiss();
			startAnimActivity(WatchActivity.class);
			break;
		case R.id.rl_main_popup_message: // pop - 消息中心
			mPopupWindow.dismiss();
			startAnimActivity(CallPoliceActivity.class);
			break;
		case R.id.img_display_electric: // 界面 - 设备定位
			if(rlDeviceElectric.getVisibility() == View.VISIBLE){
				Animation right = AnimationUtils.loadAnimation(this,R.anim.right_out);
				rlDeviceElectric.setVisibility(View.GONE);
				rlDeviceElectric.startAnimation(right);
			}else{
				Animation left = AnimationUtils.loadAnimation(this,R.anim.left_in);
				rlDeviceElectric.setVisibility(View.VISIBLE);
				rlDeviceElectric.startAnimation(left);
			}
			break;
		case R.id.rl_display_showinfo_icon_lication: // 界面 - 设备定位
			realTimeLocation(); // 实时定位
    		break;
		case R.id.img_menu_icon:  // 菜单——设备信息
		case R.id.ll_menu_header_info:
			startAnimActivity(DeviceInfoActivity.class);
		    break;
		case R.id.btn_menu_set: // 菜单——设置
			Intent intent = new Intent(MainActivity.this,  SettingActivity.class); 
            // 启动指定Activity并等待返回的结果，其中0是请求码，用于标识该请求 
            startActivityForResult(intent, 0); 
//			startAnimActivity(SettingActivity.class);
			break;
		case R.id.btn_menu_feedback: // 菜单——反馈
			agent.startFeedbackActivity();
			break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		activityRun = false;
		// activity 暂停时同时暂停地图控件
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		activityRun = true;
		// activity 恢复时同时恢复地图控件
		mMapView.onResume();
		initDialog();
		registerBroadcastReceiver();
		refresh();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		activityRun = false;
		// activity 销毁时同时销毁地图控件
		mMapView.onDestroy();
	}
	
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
	    if (requestCode == 0 && resultCode == 0) { 
	    	finish();  
	        System.exit(0); 
	    } 
	} 
	
	/**
	 *  popupwindow显示
	 */
	public void popupWindow(View v){
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#E9E9E9")));
		mPopupWindow.showAsDropDown(v, 0,5);
		mPopupWindow.setAnimationStyle(R.style.popwin_anim_style);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.update();
		closeHeaderTitle();
	}
	
	public void refresh(){
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
	
	private void queryMyDevices(){
		// 更新数据
	    CustomApplcation.getInstance().setContactList(DBHelper.getInstance(mContext).getDevicesInfoList());
	    List<CamelDevice> device = CustomApplcation.getInstance().getContactList();
		//组装新的device
		filledData(device);
		if(userAdapter==null){
			userAdapter = new UserDeviceAdapter(this,devices);
			list_devices.setAdapter(userAdapter);
		}else{
			userAdapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * 为ListView填充数据
	 * @param date
	 * @return
	 */
	private void filledData(List<CamelDevice> datas) {
		devices.clear();
		int total = datas.size();
		for (int i = 0; i < total; i++) {
			CamelDevice mCamelDevice = datas.get(i);
			CamelDevice camelDevice = new CamelDevice ();
			camelDevice.setDeviceAvatar(mCamelDevice.getDeviceAvatar()); // 本地数据
			camelDevice.setDeviceNike(mCamelDevice.getDeviceNike());
			camelDevice.setDeviceSelect(mCamelDevice.getDeviceSelect()); // 本地数据
			camelDevice.setDeviceIdentify(mCamelDevice.getDeviceIdentify());
			if(mCamelDevice.getDeviceSelect().equals("true")){
				CustomApplcation.getInstance().setCurrentCamelDevice(mCamelDevice);
				mHeaderTitle = mCamelDevice.getDeviceNike();
				setTopBarForOnlyTitle(mCamelDevice.getDeviceNike());
				tvInfo.setText(!TextUtils.isEmpty(mCamelDevice.getDeviceNike()) ?mCamelDevice.getDeviceNike():"宝贝");
				if (!TextUtils.isEmpty(mCamelDevice.getDeviceAvatar())) {
					File mFile=new File(mCamelDevice.getDeviceAvatar());
			        //若该文件存在
			        if (mFile.exists()) {
			            Bitmap bitmap=BitmapFactory.decodeFile(mCamelDevice.getDeviceAvatar());
			            Bitmap mBitmap = Bitmap.createScaledBitmap(bitmap, PixelUtil.dp2px(75), PixelUtil.dp2px(75), true);
			    		circleImgView.setImageBitmap(mBitmap);
			        }
				}else{
		        	Bitmap btm1 = BitmapFactory.decodeResource(this.getResources(),
		    				R.drawable.default_head);
		    		Bitmap mBitmap = Bitmap.createScaledBitmap(btm1, PixelUtil.dp2px(75), PixelUtil.dp2px(75), true);
		    		circleImgView.setImageBitmap(mBitmap);
		        }
			}
			devices.add(camelDevice);
		}
	}
	
	// 设备发生变化是
	public void deviceConversion(){
		
		updateFamilyMember(); // 更新家庭成员 3D
		
		new Handler().postDelayed(new Runnable(){   
		    public void run() {   
//		    	updateMessageData(); // 更新历史数据  3E
		    }   
		 }, 2000);
		
		
		new Handler().postDelayed(new Runnable(){   
		    public void run() {   
		    	updateDeviceNewestMessage(); // 更新当前设备最新数据   4A
		    }   
		 }, 4000);
	}
	
	/**
	 *  初始标题
	 */
	public void openHeaderTitle(){
		initTopBarForBoth(mHeaderTitle, R.drawable.base_action_bar_up_bg_selector, new onRightImageButtonClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow(v);
			}
		},R.drawable.base_action_bar_menu_bg_selector,new onLeftImageButtonClickListener() {
			@Override
			public void onClick(View v) {
				mDrawer.toggleMenu();
			}
		});
	}
	
	/**
	 *  选择标题
	 */
    public void closeHeaderTitle(){
    	initTopBarForBoth(mHeaderTitle, R.drawable.base_action_bar_down_bg_selector, new onRightImageButtonClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow(v);
			}
		},R.drawable.base_action_bar_menu_bg_selector,new onLeftImageButtonClickListener() {
			@Override
			public void onClick(View v) {
				mDrawer.toggleMenu();
			}
		});
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {}
	
	/**
	 *  反编译坐标
	 */
	public void reverseGeoCoder(LatLng ptCenter){
		// 反Geo搜索
		mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
	}

	/**
	 *  地理坐标编码 - 地址转换经纬度
	 */
	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {	}

	/**
	 *  地理坐标反编码 - 经纬度转换地址
	 */
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this, "抱歉,未能确认您当前位置", Toast.LENGTH_LONG).show();
			return;
		}
		String addr = null;
//		String province = result.getAddressDetail().province;
		String city = result.getAddressDetail().city;
		String district = result.getAddressDetail().district;
		String street = result.getAddressDetail().street;
		String streetNumber = result.getAddressDetail().streetNumber;
//		ShowToast(city +district +street);
		addr = city +district +street+streetNumber;
		tvDeviceAddress.setText(city +district +street+streetNumber);
		CustomApplcation.getInstance().setUserCity(city);
		
		if(CustomApplcation.getInstance().getCurrentCamelDevice() == null){
			return ;
		}
		String id = CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify();
		if(DBHelper.getInstance(mContext).isCamelDevicePushSaved(id)){
			List<CamelDevicePush> camelDeviceStateList = DBHelper.getInstance(mContext).getDevicePushMessage(id);
			CamelDevicePush mCamelDeviceState = camelDeviceStateList.get(camelDeviceStateList.size()-1);
		    if(mCamelDeviceState.getDevicePushWorkState().equals("03")){
		    	addr += "(待机)";
		    	tvDeviceAddress.setText(addr);
		    }
		}
	}
	
	/**
	 * 定位SDK监听函数 定位经纬度
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
			        .accuracy(location.getRadius())
					// 定位精度
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData); // 设置定位数据
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u); // 以动画方式更新地图状态，动画耗时 300 ms
				
				// 编码
//				reverseGeoCoder(new LatLng(location.getLatitude(),location.getLongitude()));
			}
			latitude = location.getLatitude();
			longtitude = location.getLongitude();
			CustomApplcation.getInstance().setLatitude(String.valueOf(latitude));
			CustomApplcation.getInstance().setLongtitude(String.valueOf(longtitude));
		}
		public void onReceivePoi(BDLocation poiLocation) {}
	}
	
	
	/**
	 *  初始化对话框
	 */
	public void initDialog(){
		mBackDialog = BaseDialog.getDialog(this, "提示","确认要切换设备吗么?", "确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				selectorDevice(position);
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
	 *  初始化广播
	 */
	public void registerBroadcastReceiver(){
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction("action.main.refresh.1b");
		iFilter.addAction("action.main.refresh.3a");
		iFilter.addAction("action.main.push");
		iFilter.addAction("action.main.refresh.2a");
		registerReceiver(new refreshReceiver(), iFilter);
	}
	
	/**
	 *  广播监听
	 */
	int position;
	public class refreshReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			if (s.equals("action.main.refresh.1b")) { // 设备选择
				position = Integer.valueOf(intent.getStringExtra("position")).intValue();//
				mBackDialog.show();
			}else if(s.equals("action.main.refresh.3a")){ // 获取完成设备
				refresh();
				deviceConversion();
			}
			// 消息推送
			else if (s.equals("action.main.push")){ // 消息推送的 1A 4A
				pushMessage();
				dismissLoadingDialog();
			}else if (s.equals("action.main.refresh.2a")){ // 添加设备
				refresh();
				// 假如当前只有一个设备的话，当前添加设备为选中设备
				if(devices.size() == 1){ 
					deviceConversion();
				}else if(devices.size() == 0){ // 无设备
					setTopBarForOnlyTitle("无设备");
					tvInfo.setText("无设备");
				}
			}
		}
	}
	
	/**
	 *  更新地图 图标位置
	 */
	public void pushMessage(){
		if(CustomApplcation.getInstance().getCurrentCamelDevice() == null){
			return ;
		}
		String id = CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify();
		if(DBHelper.getInstance(mContext).isCamelDevicePushSaved(id)){
			List<CamelDevicePush> camelDeviceStateList = DBHelper.getInstance(mContext).getDevicePushMessage(id);
			CamelDevicePush mCamelDeviceState = camelDeviceStateList.get(camelDeviceStateList.size()-1);
			LatLng latlng = new LatLng(Double.valueOf(mCamelDeviceState.getDevicePushLatitude()).doubleValue(),Double.valueOf(mCamelDeviceState.getDevicePushLongtitude()).doubleValue());
	    	// 移动当前位置
			baiMapDeviceShow(latlng);
	    	pushLocationMessage(latlng,mCamelDeviceState);
		}	
	}
	
	/**
	 *  更新地图 位置信息
	 */
	public void pushLocationMessage(LatLng latlng,CamelDevicePush mCamelDeviceState){
		// 对当前位置进行编码
		reverseGeoCoder(latlng);
		// 时间
		tvDeviceTime.setText("时间 ： " + mCamelDeviceState.getDevicePushCreated());
		// 步数
		tvDeviceStep.setText("步数 ： " + mCamelDeviceState.getDevicePushStep()+"步");
		// 电量
		int rank = PowerUtils.countPercent(mCamelDeviceState.getDevicePushElectric());
		String str = PowerUtils.setPower(mCamelDeviceState.getDevicePushElectric());
		tvDeviceElectric.setText(str); 
		switch(rank){
		case 0:
			imgDeviceElectric.setBackgroundResource(R.drawable.display_electric_0);
			break;
		case 1:
			imgDeviceElectric.setBackgroundResource(R.drawable.display_electric_1);
			break;
		case 2:
			imgDeviceElectric.setBackgroundResource(R.drawable.display_electric_2);
			break;
		case 3:
			imgDeviceElectric.setBackgroundResource(R.drawable.display_electric_3);
			break;
		case 4:
			imgDeviceElectric.setBackgroundResource(R.drawable.display_electric_4);
			break;
		case 5:
			imgDeviceElectric.setBackgroundResource(R.drawable.display_electric_5);
			break;
		}
		// 电池状态
		if(mCamelDeviceState.getDevicePushElectricMode().equals("01")){ // 充电
			imgDeviceElectric.setBackgroundResource(R.drawable.display_electric_6);
		}else if(mCamelDeviceState.getDevicePushElectricMode().equals("02")){ // 已充满
			CustomApplcation.getInstance().notificationNotify(3,"电量已充满");
		}
		// 定位模式
		String signal = mCamelDeviceState.getDevicePushLocaMode();
		int radius = 50;
		if(!TextUtils.isEmpty(signal)){
			Bitmap btm1 = null ;
			if(signal.equals("00")){
				btm1 = BitmapFactory.decodeResource(this.getResources(),R.drawable.display_showinfo_icon_null);
			}else if(signal.equals("01")){
				radius = 50;
				btm1 = BitmapFactory.decodeResource(this.getResources(),R.drawable.display_showinfo_icon_wifi);
			}else if(signal.equals("02")){
				radius = 10;
				btm1 = BitmapFactory.decodeResource(this.getResources(),R.drawable.display_showinfo_icon_gps);
			}else if(signal.equals("03")){
				radius = 800;
				btm1 = BitmapFactory.decodeResource(this.getResources(),R.drawable.display_showinfo_icon_jz);
			}
			Bitmap mBitmap = Bitmap.createScaledBitmap(btm1, PixelUtil.dp2px(32), PixelUtil.dp2px(32), true);
			imgDeviceLocaMode.setImageBitmap(mBitmap);
			
			// 区域
			OverlayOptions ooCircle = new CircleOptions().fillColor(0x20468fdd).center(latlng).stroke(new Stroke(2, 0xffa1c5ea)).radius(radius);
	        mBaiduMap.addOverlay(ooCircle);
		}
	}
	
	/**
	 *  发送设备选择消息(设备选择) 
	 */
	public void selectorDevice(final int position){	
		showLoadingDialog("切换设备...");	
		SocketInit.getInstance().selectorDevice(devices.get(position).getDeviceIdentify(), new SaveListener(){
			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dismissLoadingDialog();
						mBaiduMap.clear();
						operationData(position); // 数据库操作
						refresh();
						deviceConversion();
					}
				});
			}

			@Override
			public void onFailure(int paramInt, String paramString) {
				ShowToast(paramString);
				dismissLoadingDialog();
			}
		});
	}
	
	/**
	 *  发送设备当前位置(实时定位)
	 */
	public void realTimeLocation(){
		if(!SocketInit.getInstance().isCanSend()){
			ShowToast("无连接...");
			return;
		}
		showLoadingDialog("获取实时位置...");
		updateDeviceNewestMessage();
	}

	/**
	 *  发送设备当前位置     成功后修改更新显示界面
	 */
	@SuppressLint("ResourceAsColor")
	public void baiMapDeviceShow(LatLng latlng){
		mBaiduMap.clear();
		// 显示设备的位置
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latlng));
		ImageView civ = new ImageView(this);
		Bitmap btm = BitmapFactory.decodeResource(this.getResources(),R.drawable.main_child_logo_1); // child_header
		Bitmap mBitmap1 = Bitmap.createScaledBitmap(btm, PixelUtil.dp2px(30), PixelUtil.dp2px(40), true);
		civ.setImageBitmap(mBitmap1);
		mBaiduMap.showInfoWindow(new InfoWindow(civ, latlng, 0));
		// 百度地图移动
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latlng);
		mBaiduMap.animateMapStatus(u); // 以动画方式更新地图状态，动画耗时 300 ms	
	}
	
	/** 
	 *  发送设备选择消息 成功后操作数据库
	 */
	public void operationData(int position){
		List<CamelDevice> list = new ArrayList<CamelDevice>();
		for(int i = 0 ; i < devices.size() ; i++){
			if(DBHelper.getInstance(mContext).isDevicesInfoSaved(devices.get(i).getDeviceIdentify())){
				CamelDevice camel = DBHelper.getInstance(mContext).getAssignDevices(devices.get(i).getDeviceIdentify()).get(0);
				camel.setDeviceAvatar(devices.get(i).getDeviceAvatar());
				camel.setDeviceNike(devices.get(i).getDeviceNike());
				camel.setDeviceIdentify(devices.get(i).getDeviceIdentify());
				if(position == i){
					camel.setDeviceSelect("true");
				}else{
					camel.setDeviceSelect("false");
				}
				DBHelper.getInstance(mContext).updateToDevicesInfoTable(camel) ;
				list.add(camel);
			}
		}
		ShowToast("切换成功");
	}  
	
	
	/** 
	 *  菜单、返回键响应 
	 */  
	@Override  
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
	    if(keyCode == KeyEvent.KEYCODE_BACK)  {    
	           exitBy2Click();      //调用双击退出函数  
	    }  
	    return false;  
	}  
	
	/** 
	 *  双击退出函数 
	 */  
	private static Boolean isExit = false;  
	  
	private void exitBy2Click() {  
	    Timer tExit = null;  
	    if (isExit == false) {  
	        isExit = true; // 准备退出  
	        Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();  
	        tExit = new Timer();  
	        tExit.schedule(new TimerTask() {  
	            @Override  
	            public void run() {  
	                isExit = false; // 取消退出  
	            }  
	        }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务  
	  
	    } else {  
	        finish();  
	        System.exit(0);  
	    }  
	}
}

package com.camel.redpenguin.ui;

import java.sql.Date;
import java.text.SimpleDateFormat;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import android.os.Bundle;

import android.text.TextUtils;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.camel.redpenguin.CustomApplcation;
import com.camel.redpenguin.R;
import com.camel.redpenguin.bean.DBHelper;
import com.camel.redpenguin.greendao.RelSafetyZone;
import com.camel.redpenguin.util.PixelUtil;
import com.camel.redpenguin.view.EditViewDialog;
import com.camel.redpenguin.view.HeaderLayout.onRightImageButtonClickListener;

/**
 * @ClassName: WatchMapActivity
 * @Description: 远程守护设置、查看地图
 * @author kcj
 * @date
 */
public class WatchMapActivity extends BaseActivity  
implements OnClickListener ,OnGetGeoCoderResultListener ,
OnGetPoiSearchResultListener{
	// 搜索与修改数据
	boolean mBoolSearch = false;
	RelativeLayout mSearch;
	EditText edContent;
	ImageView ivBack;
	ImageView ivSave;
	// 百度
	private double latitude = 0;
	private double longtitude = 0;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	LatLng pointLatLng = null;
	Button btnIcon;
	// 地图上面的空间
	private RelativeLayout rlWatchMapSearch;
	private RelativeLayout rlWatchMapClear;
	private RelativeLayout rlWatchMapLocation;
	// 地图下面的
	private LinearLayout llWatchMapMessage;
	boolean mBoolSign = false;
	private TextView tvSign;
	private SeekBar seekBarScope;
	private Button ivHome;
	private Button ivSchool;
	private Button ivGame;
	private Button ivCollect;
    /** 百度SDK */
	private ProgressDialog dialog;
	private GeoCoder mGeoCoder = null; 
	private PoiSearch mPoiSearch = null;
	
	EditViewDialog mEditViewDialog;
	// 信息
	private RelSafetyZone mRelSafetyZone = new RelSafetyZone();
	
	// intent传参
	private RelSafetyZone intentZone = null;
	
	@SuppressLint({ "ResourceAsColor", "SimpleDateFormat" })
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watchmap);
		latitude = Double.valueOf(CustomApplcation.getInstance().getLatitude());
		longtitude = Double.valueOf(CustomApplcation.getInstance()
				.getLongtitude());
		// 获取传参
		intentZone = WatchActivity.mIntentTransmit;
//		intentZone = (IntentTransmit)getIntent().getSerializableExtra("data");
		/** header */
		initTopBarForBoth("设置区域", R.drawable.base_action_bar_true_bg_selector,
				new onRightImageButtonClickListener() {
					@Override 
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(CustomApplcation.getInstance().getCurrentCamelDevice() == null){
							ShowToast("无选择设备");
							return;
						}
						if(intentZone == null){
							String id = CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify();
							mRelSafetyZone.setRelZoneIdentify(id);
							if(TextUtils.isEmpty(mRelSafetyZone.getRelZoneAddr())){
								ShowToast("请选择区域");
								return;
							}
							DBHelper.getInstance(mContext).addToRelZoneTable(mRelSafetyZone);
						    finish();
						}else{
							DBHelper.getInstance(mContext).deleteRelZoneList(intentZone.getRelZoneCreated());
							DBHelper.getInstance(mContext).updateToRelZoneTable(mRelSafetyZone);
							finish();
						}
					}
				});
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");       
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
		mRelSafetyZone.setRelZoneName("家");
		mRelSafetyZone.setRelZoneRadius("100");
		mRelSafetyZone.setRelZoneCreated(formatter.format(curDate));
		/** 地图显示 */
		mMapView = (MapView) findViewById(R.id.mv_map_watch);
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 移动至位置
		LatLng ll = new LatLng(latitude, longtitude);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		mBaiduMap.animateMapStatus(u); // 以动画方式更新地图状态，动画耗时 300 ms
		// 图层
		float zoomLevel = Float.parseFloat("18");
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
		// 删除百度地图logo
		mMapView.removeViewAt(1);
		// 长按确定位置
		mBaiduMap.setOnMapLongClickListener(new OnMapLongClickListener() {
			public void onMapLongClick(LatLng point) {

			}
		});
		// 点击百度地图
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			@SuppressLint("ResourceAsColor")
			public void onMapClick(LatLng point) {
				showLoadingDialog("查询位置");
				mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(point));
				mRelSafetyZone.setRelZoneLatitude(String.valueOf(point.latitude));
				mRelSafetyZone.setRelZoneLongtitude(String.valueOf(point.longitude));
				mMapView.getMap().clear();
				pointLatLng = point;
				if (llWatchMapMessage.getVisibility() == View.GONE) {
					llWatchMapMessage.setVisibility(View.VISIBLE);
				}
				mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));
				mBaiduMap.showInfoWindow(new InfoWindow(btnIcon, point, 0));
				// 添加圆
				OverlayOptions ooCircle = new CircleOptions()
						.fillColor(0x20468fdd).center(point)
						.stroke(new Stroke(2, 0xff0f81d9)).radius(100);
				mBaiduMap.addOverlay(ooCircle);
			}

			public boolean onMapPoiClick(MapPoi poi) {
				return false;
			}
		});
		mGeoCoder = GeoCoder.newInstance();
		mGeoCoder.setOnGetGeoCodeResultListener(this);
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);

		initView();
		// 初始化对话框
		initDialog();

		refresh();	
	}
	
	@SuppressLint("ResourceAsColor")
	public void initView(){
		// 显示的图标
		btnIcon = new Button(WatchMapActivity.this);
		btnIcon.setBackgroundResource(R.drawable.watch_location_icon_home);
		btnIcon.setWidth(PixelUtil.dp2px(45));
		btnIcon.setHeight(PixelUtil.dp2px(45));
		btnIcon.setText("100");
		btnIcon.setTextSize(10);
		btnIcon.setTextColor(R.color.base_color_text_white);
		/** 初始化UI */
		mSearch = (RelativeLayout) findViewById(R.id.ll_search);
		edContent = (EditText) findViewById(R.id.ev_search_comment);
		ivBack = (ImageView) findViewById(R.id.img_search_back);
		ivSave = (ImageView) findViewById(R.id.img_search_save);
		rlWatchMapSearch = (RelativeLayout) findViewById(R.id.rl_watch_map_search);
		rlWatchMapClear = (RelativeLayout) findViewById(R.id.rl_watch_map_clear);
		rlWatchMapLocation = (RelativeLayout) findViewById(R.id.rl_watch_map_location);
		llWatchMapMessage = (LinearLayout) findViewById(R.id.ll_watch_map_message);
		tvSign = (TextView) findViewById(R.id.tv_watch_map_message_name_content);
		seekBarScope = (SeekBar) findViewById(R.id.sb_bar_scope);
		ivHome = (Button) findViewById(R.id.img_watch_map_message_sign_home);
		ivSchool = (Button) findViewById(R.id.img_watch_map_message_sign_school);
		ivGame = (Button) findViewById(R.id.img_watch_map_message_sign_game);
		ivCollect = (Button) findViewById(R.id.img_watch_map_message_sign_collect);
		ivHome.setSelected(true);
		ivBack.setOnClickListener(this);  // 搜索-返回
		ivSave.setOnClickListener(this);  // 搜索-确定
		rlWatchMapSearch.setOnClickListener(this); // 地图-搜索
		rlWatchMapClear.setOnClickListener(this);  // 地图-清除
		rlWatchMapLocation.setOnClickListener(this); // 地图-定位
		tvSign.setOnClickListener(this); // 位置文字
		ivHome.setOnClickListener(this); // 标志 - 家
		ivSchool.setOnClickListener(this); // 标志 - 学校
		ivGame.setOnClickListener(this);// 标志 - 娱乐
		ivCollect.setOnClickListener(this);// 标志 - 收藏
		// 监听seekbar的拖动事件
		/*runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try  {   
					
					
				}catch(Exception e){
					Log.e("TAG", e.toString());
				}
				
			}
		});*/
		seekBarScope.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			/** 拖动条停止拖动的时候调用 */
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			/** 拖动条开始拖动的时候调用  */
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			/** 拖动条进度改变的时候调用 */
			@Override
			public void onProgressChanged(SeekBar seekBar,int progress, boolean fromUser) {
				mRelSafetyZone.setRelZoneRadius(String.valueOf(100 + progress));
				btnIcon.setText(String.valueOf(100 + progress));
				OverlayOptions ooCircle = new CircleOptions().fillColor(0x20468fdd).center(pointLatLng).stroke(new Stroke(2, 0xff0f81d9)).radius(100 + progress);
				mMapView.getMap().clear();
				mBaiduMap.showInfoWindow(new InfoWindow(btnIcon, pointLatLng, 0));
				mBaiduMap.addOverlay(ooCircle);
			}
		});
	}
	
	public void initDialog(){
		mEditViewDialog = new EditViewDialog(this);
		mEditViewDialog.setTitle("修改名称");
		mEditViewDialog.setButton1("确定",new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				String name = mEditViewDialog.getDeletableEditText();
				tvSign.setText(name);
				mRelSafetyZone.setRelZoneAddr(name);
				mEditViewDialog.cancel();
			}
			
		});
		mEditViewDialog.setButton2("取消",new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				mEditViewDialog.cancel();
			}
			
		});
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.img_search_back:
			hideSoftInputView();
			mSearch.setVisibility(View.GONE);
			mSearch.startAnimation(AnimationUtils.loadAnimation(this, R.anim.translate_out));
			break;
		case R.id.img_search_save:
			hideSoftInputView();
			mSearch.setVisibility(View.GONE);
			mSearch.startAnimation(AnimationUtils.loadAnimation(this, R.anim.translate_out));
			mPoiSearch(edContent.getText().toString());
			break;
		case R.id.rl_watch_map_search:
			if(mSearch.getVisibility() == View.VISIBLE){
				hideSoftInputView();
				mSearch.setVisibility(View.GONE);
				mSearch.startAnimation(AnimationUtils.loadAnimation(this, R.anim.translate_out));
			}else{
				mSearch.setVisibility(View.VISIBLE);
				mSearch.startAnimation(AnimationUtils.loadAnimation(this, R.anim.translate_in));
				showSoftInputView(edContent);
			}
			break;
		case R.id.rl_watch_map_clear:
			seekBarScope.setProgress(0);
			mMapView.getMap().clear();
			llWatchMapMessage.setVisibility(View.GONE);
			break;
		case R.id.rl_watch_map_location:
			LatLng ll = new LatLng(latitude, longtitude);
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u); // 以动画方式更新地图状态，动画耗时 300 ms
			break;
		case R.id.tv_watch_map_message_name_content:
			mEditViewDialog.show();
			mEditViewDialog.setDeletableEditText(tvSign.getText().toString());
			break;
		case R.id.img_watch_map_message_sign_home:
			btnIcon.setBackgroundResource(R.drawable.watch_location_icon_home);
			mBaiduMap.showInfoWindow(new InfoWindow(btnIcon, pointLatLng, 0));
			mRelSafetyZone.setRelZoneName("家");
			ivHome.setSelected(true);
			ivSchool.setSelected(false);
			ivGame.setSelected(false);
			ivCollect.setSelected(false);
			break;
		case R.id.img_watch_map_message_sign_school:
			btnIcon.setBackgroundResource(R.drawable.watch_location_icon_school);
			mBaiduMap.showInfoWindow(new InfoWindow(btnIcon, pointLatLng, 0));
			mRelSafetyZone.setRelZoneName("学校");
			ivHome.setSelected(false);
			ivSchool.setSelected(true);
			ivGame.setSelected(false);
			ivCollect.setSelected(false);
			break;
		case R.id.img_watch_map_message_sign_game:
			btnIcon.setBackgroundResource(R.drawable.watch_location_icon_game);
			mBaiduMap.showInfoWindow(new InfoWindow(btnIcon, pointLatLng, 0));
			mRelSafetyZone.setRelZoneName("娱乐");
			ivHome.setSelected(false);
			ivSchool.setSelected(false);
			ivGame.setSelected(true);
			ivCollect.setSelected(false);
			break;
		case R.id.img_watch_map_message_sign_collect:
			btnIcon.setBackgroundResource(R.drawable.watch_location_icon_collect);
			mBaiduMap.showInfoWindow(new InfoWindow(btnIcon, pointLatLng, 0));
			mRelSafetyZone.setRelZoneName("其他");
			ivHome.setSelected(false);
			ivSchool.setSelected(false);
			ivGame.setSelected(false);
			ivCollect.setSelected(true);
			break;
		}
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
	
	public void queryMyDevices(){
		// 判断
		if(intentZone != null){
			mRelSafetyZone.setRelZoneAddr(intentZone.getRelZoneAddr());
			mRelSafetyZone.setRelZoneName(intentZone.getRelZoneName());
			mRelSafetyZone.setRelZoneIdentify(intentZone.getRelZoneIdentify());
			mRelSafetyZone.setRelZoneLatitude(intentZone.getRelZoneLatitude());
			mRelSafetyZone.setRelZoneLongtitude(intentZone.getRelZoneLongtitude());
			mRelSafetyZone.setRelZoneRadius(intentZone.getRelZoneRadius());
//			mRelSafetyZone.setRelZoneCreated(intentZone.getRelZoneCreated());
			llWatchMapMessage.setVisibility(View.VISIBLE);
			// 显示信息
			tvSign.setText(mRelSafetyZone.getRelZoneAddr());
//			seekBarScope.setProgress(Integer.valueOf(mRelSafetyZone.getRelZoneRadius()).intValue() - 100);	
			// 地图显示
			LatLng latlng = new LatLng(Double.valueOf(mRelSafetyZone.getRelZoneLatitude()).doubleValue(),Double.valueOf(mRelSafetyZone.getRelZoneLongtitude()).doubleValue());
			mMapView.getMap().clear();
			pointLatLng = latlng;
			btnIcon.setText(mRelSafetyZone.getRelZoneRadius());
			if(mRelSafetyZone.getRelZoneName().equals("家")){
				ivHome.setSelected(true);
				ivSchool.setSelected(false);
				ivGame.setSelected(false);
				ivCollect.setSelected(false);
			    btnIcon.setBackgroundResource(R.drawable.watch_location_icon_home);
			}else if(mRelSafetyZone.getRelZoneName().equals("学校")){
				ivHome.setSelected(false);
				ivSchool.setSelected(true);
				ivGame.setSelected(false);
				ivCollect.setSelected(false);
				btnIcon.setBackgroundResource(R.drawable.watch_location_icon_school);
			}else if(mRelSafetyZone.getRelZoneName().equals("娱乐")){
				ivHome.setSelected(false);
				ivSchool.setSelected(false);
				ivGame.setSelected(true);
				ivCollect.setSelected(false);
				btnIcon.setBackgroundResource(R.drawable.watch_location_icon_game);
			}else if(mRelSafetyZone.getRelZoneName().equals("其他")){
				ivHome.setSelected(false);
				ivSchool.setSelected(false);
				ivGame.setSelected(false);
				ivCollect.setSelected(true);
				btnIcon.setBackgroundResource(R.drawable.watch_location_icon_collect);
			}
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latlng));
			mBaiduMap.showInfoWindow(new InfoWindow(btnIcon, latlng, 0));
			// 添加圆
			OverlayOptions ooCircle = new CircleOptions()
					.fillColor(0x000000FF).center(latlng)
					.stroke(new Stroke(2, 0xff0f81d9)).radius(Integer.valueOf(mRelSafetyZone.getRelZoneRadius()).intValue());
			mBaiduMap.addOverlay(ooCircle);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// activity 销毁时同时销毁地图控件
		mMapView.onDestroy();
	}

	/**
	 *  编译坐标
	 */
	public void geoCoder(String address , String city){
		mGeoCoder.geocode(new GeoCodeOption().address(address).city(city));
	}
	
	/**
	 *  反编译坐标
	 */
	public void reverseGeoCoder(LatLng ptCenter){
		mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
	}
	
	/**
	 *  地理坐标编码 - 地址转换经纬度
	 */
	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		// TODO Auto-generated method stub
		
	}

	/**
	 *  地理坐标反编码 - 经纬度转换地址
	 */
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this, "抱歉,未能确认您当前位置", Toast.LENGTH_LONG)
					.show();
			return;
		}
		String addr = result.getAddressDetail().district+result.getAddressDetail().street+result.getAddressDetail().streetNumber;
		tvSign.setText(addr);
		mRelSafetyZone.setRelZoneAddr(addr);
		dismissLoadingDialog();
	}

	public void mPoiSearch(String search){
		dialog=new ProgressDialog(this);
		dialog.setMessage("加载中");
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		mPoiSearch.searchInCity(new PoiCitySearchOption().city(CustomApplcation.getInstance().getUserCity())
				.keyword(search));
	}
	
	/**
	 *  poi - 详细信息
	 */
	@Override
	public void onGetPoiDetailResult(PoiDetailResult arg0) {
		// TODO Auto-generated method stub
		
	}

	/**
	 *  poi - 基本信息
	 */
	@Override
	public void onGetPoiResult(PoiResult result) {
		// TODO Auto-generated method stub
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			dialog.dismiss();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			edContent.setText("");
			mMapView.getMap().clear();
			if (llWatchMapMessage.getVisibility() == View.GONE) {
				llWatchMapMessage.setVisibility(View.VISIBLE);
			}
			for(PoiInfo info : result.getAllPoi()){ 
				tvSign.setText(info.name);
				pointLatLng = info.location;
				mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(info.location));
				mBaiduMap.showInfoWindow(new InfoWindow(btnIcon, info.location, 0));
				// 添加圆
				OverlayOptions ooCircle = new CircleOptions()
						.fillColor(0x000000FF).center(info.location)
						.stroke(new Stroke(2, 0xff0f81d9)).radius(100);
				mBaiduMap.addOverlay(ooCircle);
				dialog.dismiss();
				break;
			}
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
			dialog.dismiss();
			String strInfo = "在";
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			strInfo += "找到结果";
			ShowToast(strInfo);
		}
	}


}

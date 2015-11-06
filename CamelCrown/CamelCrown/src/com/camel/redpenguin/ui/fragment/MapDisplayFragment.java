package com.camel.redpenguin.ui.fragment;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;

import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;

import com.baidu.mapapi.model.LatLng;
import com.camel.redpenguin.CustomApplcation;
import com.camel.redpenguin.R;
import com.camel.redpenguin.ui.FragmentBase;
import android.annotation.SuppressLint;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ZoomControls;
import com.camel.redpenguin.view.HeaderLayout.onRightImageButtonClickListener;

/**
 * @ClassName:MapDisplayFragment
 * @Description: 地图展示
 * @author kcj
 * @date
 */
public class MapDisplayFragment extends FragmentBase {
	double latitude = 0;
	double longtitude = 0;
	private View contentView;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private boolean isFirstLoc = true;// 是否首次定位
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub fragment_mapshow
		contentView = inflater.inflate(R.layout.fragment_map_display,container,false);
		return contentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initMapDisplayUI(contentView);	
	}
	
	@SuppressLint("NewApi")
	public void initMapDisplayUI(View v){
		/** header */
		initTopBarForBoth("宝贝", v, R.drawable.base_action_bar_more_bg_selector,
				new onRightImageButtonClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

					}
				});
		/** 初始化地图显示 */
		mMapView = (MapView)v.findViewById(R.id.mv_display);
	    BaiduMapOptions options = new  BaiduMapOptions();
	    options.scrollGesturesEnabled(false);
		mMapView.onPause();
		//MapFragment map = MapFragment.newInstance(options);
		//map.onStop() ;
		
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		/** 初始化定位 */
		mLocClient = new LocationClient(getActivity());
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		/** 设置地图 */
		// 地图图层
		float zoomLevel = Float.parseFloat("17");
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
        mMapView.removeViewAt(1); 
	}
	
	/**
	 * 定位SDK监听函数   定位经纬度
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())// 定位精度
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData); // 设置定位数据
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u); // 以动画方式更新地图状态，动画耗时 300 ms
			}
			latitude = location.getLatitude();
			longtitude = location.getLongitude();
			CustomApplcation.getInstance().setLatitude(String.valueOf(latitude));
			CustomApplcation.getInstance().setLongtitude(String.valueOf(longtitude));
		}
		
		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
	
	public void onResume() {
		super.onResume();
		//ShowToast("onResume");
	}

	public void onPause() {
		super.onPause();
		//ShowToast("onPause");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//ShowToast("onDestroy");
	}
}

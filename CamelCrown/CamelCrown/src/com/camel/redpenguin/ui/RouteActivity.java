package com.camel.redpenguin.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.camel.redpenguin.CustomApplcation;
import com.camel.redpenguin.R;
import com.camel.redpenguin.config.Config;

/**
 * @ClassName: RouteActivity
 * @Description: 路线规划
 * @author: kcj
 * @date: 
 */
public class RouteActivity extends BaseActivity implements OnClickListener {

	Button btnWalk , btnDrive , btnBus ;
	
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_routemode);
		/** header */
		initHeader();
		/** UI */
		btnWalk = (Button) findViewById(R.id.btn_route_mode_walk_select);
		btnDrive = (Button) findViewById(R.id.btn_route_mode_drive_select);
		btnBus = (Button) findViewById(R.id.btn_route_mode_bus_select);
		btnWalk.setSelected(true);
		btnWalk.setOnClickListener(this);
		btnDrive.setOnClickListener(this);
		btnBus.setOnClickListener(this);
		/** 数据 */
		String string = CustomApplcation.getInstance().getBaiduLocationMode();
		if(string.equals("1")){
			btnWalk.setSelected(true);
			btnDrive.setSelected(false);
			btnBus.setSelected(false);
		}else if(string.equals("2")){
			btnWalk.setSelected(false);
			btnDrive.setSelected(true);
			btnBus.setSelected(false);
		}else if(string.equals("3")){
			btnWalk.setSelected(false);
			btnDrive.setSelected(false);
			btnBus.setSelected(true);
		}	
	}
	
	public void initHeader(){
		initTopBarForLeft("路线规划模式");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_route_mode_walk_select:
			btnWalk.setSelected(true);
			btnDrive.setSelected(false);
			btnBus.setSelected(false);
			CustomApplcation.getInstance().setBaiduLocationMode("1");
			break;
		case R.id.btn_route_mode_drive_select:
			btnWalk.setSelected(false);
			btnDrive.setSelected(true);
			btnBus.setSelected(false);
			CustomApplcation.getInstance().setBaiduLocationMode("2");
			break;
		case R.id.btn_route_mode_bus_select:
			btnWalk.setSelected(false);
			btnDrive.setSelected(false);
			btnBus.setSelected(true);
			CustomApplcation.getInstance().setBaiduLocationMode("3");
			break;
		}
	}
}


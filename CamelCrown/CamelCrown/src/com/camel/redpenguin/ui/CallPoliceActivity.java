package com.camel.redpenguin.ui;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.camel.redpenguin.CustomApplcation;
import com.camel.redpenguin.R;
import com.camel.redpenguin.bean.DBHelper;
import com.camel.redpenguin.greendao.CamelDevicePush;

/**
 * @ClassName: CallPoliceActivity
 * @Description: 报警
 * @author: KCJ
 * @date: 
 */
public class CallPoliceActivity extends BaseActivity implements OnClickListener{

	private TextView tvElectricityHeaderTime;
	private TextView tvSosHeaderTime;
	private RelativeLayout rlElectricity ,rlSOS ;
	
	private TextView tvInAddr , tvOutAddr;
	private RelativeLayout rlIn ,rlOut ;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_callpolice);
		initTopBarForLeft("消息中心");
		initView();
		initEvents();
	}
	
	public void initView(){
		rlElectricity = (RelativeLayout)findViewById(R.id.rl_call_electricity);
		tvElectricityHeaderTime = (TextView)findViewById(R.id.tv_call_electricity_header_time);
		rlSOS = (RelativeLayout)findViewById(R.id.rl_call_sos);
		tvSosHeaderTime = (TextView)findViewById(R.id.tv_call_sos_header_time);
		
		tvInAddr = (TextView)findViewById(R.id.tv_call_in_body_addr);
		tvInAddr = (TextView)findViewById(R.id.tv_call_out_body_addr);
		rlIn = (RelativeLayout)findViewById(R.id.rl_call_in);
		rlOut = (RelativeLayout)findViewById(R.id.rl_call_out);
	}

	public void initEvents(){}
	
	@Override
	protected void onResume() {
		super.onResume();
		refresh();
	}
	
	public void refresh(){
		try {
			this.runOnUiThread(new Runnable() {
				public void run() {
					queryInfo();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	public void queryInfo(){
		if(!DBHelper.getInstance(mContext).isCamelDevicePushSaved(CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify())){
			Log.e("total", "无数据");
			return;
		}
		String id = CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String selectTime = formatter.format(curDate);
		List<CamelDevicePush> deviceStates = DBHelper.getInstance(mContext).getDevicePushMessage(id ,selectTime);
		for(int i = 0 ; i < deviceStates.size() ; i++){
			CamelDevicePush camelDeviceState = deviceStates.get(i);
//	    	Log.e("设备工作状态", camelDeviceState.getDevicePushWorkState());
//	    	Log.e("电池状态", camelDeviceState.getDevicePushElectricMode());
//	    	Log.e("电量", camelDeviceState.getDevicePushElectric());
//	    	Log.e("报警", camelDeviceState.getDevicePushUrgency());
//	    	Log.e("时间", camelDeviceState.getDevicePushCreated());
	    	if(camelDeviceState.getDevicePushUrgency().equals("01")){ // 低电报警
	    		if(rlElectricity.getVisibility() == View.GONE){
	    			rlElectricity.setVisibility(View.VISIBLE);
	    		}
	    		tvElectricityHeaderTime.setText(camelDeviceState.getDevicePushCreated());
//	    		rlElectricityBodyText.setText("123456789");
	    	}else if(camelDeviceState.getDevicePushUrgency().equals("02")){ // SOS报警
	    		if(rlSOS.getVisibility() == View.GONE){
	    			rlSOS.setVisibility(View.VISIBLE);
	    		}
	    		tvSosHeaderTime.setText(camelDeviceState.getDevicePushCreated());
	    	}else if(camelDeviceState.getDevicePushUrgency().equals("03")){ // 两者
	    		if(rlElectricity.getVisibility() == View.GONE){
	    			rlElectricity.setVisibility(View.VISIBLE);
	    		}
	    		if(rlSOS.getVisibility() == View.GONE){
	    			rlSOS.setVisibility(View.VISIBLE);
	    		}
	    		tvElectricityHeaderTime.setText(camelDeviceState.getDevicePushCreated());
	    		tvSosHeaderTime.setText(camelDeviceState.getDevicePushCreated());
	    	}
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		}
	}
}

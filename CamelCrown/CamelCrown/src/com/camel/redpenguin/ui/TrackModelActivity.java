package com.camel.redpenguin.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.camel.redpenguin.CustomApplcation;
import com.camel.redpenguin.R;
import com.camel.redpenguin.bean.DBHelper;
import com.camel.redpenguin.bean.Packet;
import com.camel.redpenguin.config.SocketInit;
import com.camel.redpenguin.greendao.CamelDevice;
import com.camel.redpenguin.listener.SaveListener;
import com.camel.redpenguin.util.EncodeUtils;

/**
 * @ClassName: TrackModelActivity
 * @Description: 追踪(定位)模式
 * @author: kcj
 * @date:
 */
public class TrackModelActivity extends BaseActivity implements OnClickListener {

	Button btnSafety, btnEconomize, btnUrgency;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track);
		/** header */
		initTopBarForLeft("定位模式");
		/** UI */
		btnSafety = (Button) findViewById(R.id.btn_track_safety_select);
		btnEconomize = (Button) findViewById(R.id.btn_track_economize_select);
		btnUrgency = (Button) findViewById(R.id.btn_track_urgency_select);
		btnSafety.setOnClickListener(this);
		btnEconomize.setOnClickListener(this);
		btnUrgency.setOnClickListener(this);
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
					queryMyDevices();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void queryMyDevices(){
		if(CustomApplcation.getInstance().getCurrentCamelDevice() ==null){
			return;
		}
		String id = CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify();
		if(DBHelper.getInstance(mContext).isDevicesInfoSaved(id)){
	    	CamelDevice camel = DBHelper.getInstance(mContext).getAssignDevices(id).get(0);
//	    	Log.e("MODE",camel.getDeviceFrequency());
	    	/*if(TextUtils.isEmpty(camel.getDeviceFrequency())){
            	camel.setDeviceFrequency("05");
            	DBHelper.getInstance(mContext).updateToDevicesInfoTable(camel); 	
            }*/
	    	if(camel.getDeviceFrequency().equals("05")){
            	btnSafety.setSelected(true);
    			btnEconomize.setSelected(false);
    			btnUrgency.setSelected(false);
            }else if(camel.getDeviceFrequency().equals("0A")){
            	btnSafety.setSelected(false);
    			btnEconomize.setSelected(true);
    			btnUrgency.setSelected(false);
            }else if(camel.getDeviceFrequency().equals("02")){
            	btnSafety.setSelected(false);
    			btnEconomize.setSelected(false);
    			btnUrgency.setSelected(true);
            }
		}
	}

	String mode = null;
	@Override
	public void onClick(View v) {
		String string;
		Packet packet;
		String id;
		if(!SocketInit.getInstance().isSocketConnected()){
			ShowToast("连接断开....");
			return;
		}
		switch (v.getId()) {
		case R.id.btn_track_safety_select:
			if(btnSafety.isSelected()){
				return;
			}
			if(CustomApplcation.getInstance().getCurrentCamelDevice() == null){
				return ;
			}
			mode = "05";
			id = CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify();
			string = EncodeUtils.locationModeDataEncode(mode,id);
			packet = new Packet();
			packet.pack(string);
			locationMode( packet);

			break;
		case R.id.btn_track_economize_select:
			if(btnEconomize.isSelected()){
				return;
			}
			if(CustomApplcation.getInstance().getCurrentCamelDevice() == null){
				return ;
			}
			mode = "0A";
			id = CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify();
			string = EncodeUtils.locationModeDataEncode(mode,id);
			packet = new Packet();
			packet.pack(string);
			locationMode( packet);

			break;
		case R.id.btn_track_urgency_select:
			if(btnUrgency.isSelected()){
				return;
			}
			if(CustomApplcation.getInstance().getCurrentCamelDevice() == null){
				return ;
			}
			mode = "02";
			id = CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify();
			string = EncodeUtils.locationModeDataEncode(mode,id);
			packet = new Packet();
			packet.pack(string);
			locationMode( packet);

			break;
		}
	}
	
	public void locationMode(Packet packet){
		showLoadingDialog("切换设备定位模式...");
		SocketInit.getInstance().locationMode(packet, new SaveListener(){
			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dismissLoadingDialog();
						refresh();
					}
				});
			}

			@Override
			public void onFailure(int paramInt, String paramString) {
				dismissLoadingDialog();
				ShowToast(paramString);
			}
		});
	}

}

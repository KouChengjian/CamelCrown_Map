package com.camel.redpenguin.ui;

import java.util.List;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.camel.redpenguin.CustomApplcation;
import com.camel.redpenguin.R;
import com.camel.redpenguin.bean.DBHelper;

/**
 * @ClassName: FeedbackActivity
 * @Description: 反馈
 * @author kcj
 * @date
 */
public class FeedbackActivity extends BaseActivity{

	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		/** header */
		initTopBarForLeft("反馈");
		
		//if(CustomApplcation.getInstance().getCurrentCamelDevice() == null){
		//	Log.e("TAG", "无");
		//	return ;
		//}
		

		
//		List<CamelDeviceState> deviceStates = DBHelper.getInstance(mContext).getDeviceStates(CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify());
//		for(int i = 0 ; i < deviceStates.size() ; i++){
//	    	CamelDeviceState camelDeviceState = deviceStates.get(0);
//	    	Log.e("TAG", camelDeviceState.getDeviceStateUrgency());
//		}
		
	}
	
}

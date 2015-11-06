package com.camel.redpenguin.ui;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.camel.redpenguin.CustomApplcation;
import com.camel.redpenguin.R;
import com.camel.redpenguin.adapter.MessageContentAdapter;
import com.camel.redpenguin.bean.DBHelper;
import com.camel.redpenguin.bean.SafetyMessage;
import com.camel.redpenguin.greendao.CamelDevicePush;
import com.camel.redpenguin.view.pulltorefresh.library.PullToRefreshBase;
import com.camel.redpenguin.view.pulltorefresh.library.PullToRefreshListView;
import com.camel.redpenguin.view.pulltorefresh.library.PullToRefreshBase.Mode;
import com.camel.redpenguin.view.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.camel.redpenguin.view.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.camel.redpenguin.view.pulltorefresh.library.PullToRefreshBase.State;

/**
 * @ClassName: MessageActivity
 * @Description: 消息中心
 * @author: KCJ
 * @date: 
 */
public class MessageActivity extends BaseActivity{

	private ListView actualListView;
	private ArrayList<SafetyMessage> mListItems;
	private MessageContentAdapter mAdapter;
	private PullToRefreshListView mPullRefreshListView;
	
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		/** header */
		initTopBarForLeft("消息中心");
		/** 初始化listView */
		mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.pull_refresh_watch_list);
		mPullRefreshListView.setMode(Mode.DISABLED); //BOTH
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub	
			}
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
			}
		});
        mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
			@Override
			public void onLastItemVisible() {
				// TODO Auto-generated method stub	
			}
		});
        actualListView = mPullRefreshListView.getRefreshableView();
        mListItems = new ArrayList<SafetyMessage>();
        mAdapter = new MessageContentAdapter(mContext, mListItems);
        actualListView.setAdapter(mAdapter);
        if(mListItems.size() == 0){
			fetchData();
		}
		mPullRefreshListView.setState(State.RELEASE_TO_REFRESH, true);
		actualListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			}
		});
	}
	
	/**
	 *  数据填充
	 */
	public void fetchData(){
		if(!DBHelper.getInstance(mContext).isCamelDevicePushSaved(CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify())){
			Log.e("total", "无数据");
			return;
		}
		String id = CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String selectTime = formatter.format(curDate);
		mListItems.clear();
		List<CamelDevicePush> deviceStates = DBHelper.getInstance(mContext).getDevicePushMessage(id );
		for(int i = 0 ; i < deviceStates.size() ; i++){
			CamelDevicePush camelDeviceState = deviceStates.get(i);
	    	Log.e("设备工作状态", camelDeviceState.getDevicePushWorkState());
	    	Log.e("电池状态", camelDeviceState.getDevicePushElectricMode());
	    	Log.e("电量", camelDeviceState.getDevicePushElectric());
	    	Log.e("报警", camelDeviceState.getDevicePushUrgency());
	    	Log.e("时间", camelDeviceState.getDevicePushCreated());
	    	SafetyMessage safetyMessage = new SafetyMessage();
	    	if(camelDeviceState.getDevicePushUrgency().equals("01")){ // 低电报警
	    		safetyMessage.setMessageType(1);
	    		safetyMessage.setMessageTime(camelDeviceState.getDevicePushCreated().split(" ")[0]);
	    		int mv = Integer.valueOf(camelDeviceState.getDevicePushElectric()).intValue();
	    		if(mv <= 3740){
	    			safetyMessage.setMessageContemt("电量还剩20%");
	    		} 
	    		if(mv <= 3680){
	    			safetyMessage.setMessageContemt("电量还剩10%");
	    		} 
	    		if(mv <= 3450){
	    			safetyMessage.setMessageContemt("电量还剩5%");
	    		} 
	    		if(mv <= 3000){
	    			safetyMessage.setMessageContemt("电量还剩0%");	
	    		}
	    		mListItems.add(safetyMessage);
	    	}else if(camelDeviceState.getDevicePushUrgency().equals("02")){ // SOS报警
	    		safetyMessage.setMessageType(2);
	    		safetyMessage.setMessageTime(camelDeviceState.getDevicePushCreated());
	    		safetyMessage.setMessageContemt("请确认宝贝是否安全");
	    		mListItems.add(safetyMessage);
	    	}else if(camelDeviceState.getDevicePushUrgency().equals("03")){ // 两者
	    		safetyMessage.setMessageType(3);
	    		safetyMessage.setMessageTime(camelDeviceState.getDevicePushCreated());
	    		safetyMessage.setMessageContemt("低电量,请确认宝贝是否安全");
	    		mListItems.add(safetyMessage);
	    	}
	    	
	    }
	}	
}

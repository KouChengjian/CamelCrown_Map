package com.camel.redpenguin.ui;

import com.camel.redpenguin.CustomApplcation;
import com.camel.redpenguin.R;
import com.camel.redpenguin.bean.DBHelper;
import com.camel.redpenguin.greendao.CamelDevice;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @ClassName: SettingActivity
 * @Description: 设置界面
 * @author: KCJ
 * @date: 
 */
public class SettingActivity extends BaseActivity implements OnClickListener {

	private TextView tvSettingAccount;  // 账号
//	private RelativeLayout rlSettingAccount;
	private Button btnSettingLink;     // 轨迹
//	private RelativeLayout rlSettingLink;
	private RelativeLayout rlSettingFamily;// 家庭成员
	private RelativeLayout rlSettingMessage;// 消息
	private RelativeLayout rlSettingOffline;// 离线地图
	private Button btnSettingKey; // 安全锁
	private RelativeLayout rlSettingKey; 
	private RelativeLayout rlSettingUpdate;// 更新
	private RelativeLayout rlSettingFeedback;    //反馈
	private RelativeLayout rlSettingAbout;  // 关于
	
	private TextView tvSettingLogout;  // 退出
	
	private CamelDevice camel = null;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		initTopBarForLeft("设置");
		initView();
		initEvents();
		initDatas();
	}
	
	public void initView(){
		/** 初始化设置UI */
		tvSettingAccount = (TextView)findViewById(R.id.tv_setting_account_text);
//		rlSettingAccount = (RelativeLayout)findViewById(R.id.rl_setting_account);
		btnSettingLink = (Button)findViewById(R.id.btn_setting_link);
//		rlSettingLink = (RelativeLayout)findViewById(R.id.rl_setting_link);
		rlSettingFamily = (RelativeLayout)findViewById(R.id.rl_setting_family);
		rlSettingMessage = (RelativeLayout)findViewById(R.id.rl_setting_message);
		rlSettingOffline = (RelativeLayout)findViewById(R.id.rl_setting_offline);
		btnSettingKey = (Button)findViewById(R.id.btn_setting_key);
		rlSettingKey = (RelativeLayout)findViewById(R.id.rl_setting_key);
		rlSettingUpdate = (RelativeLayout)findViewById(R.id.rl_setting_update);
		rlSettingFeedback = (RelativeLayout)findViewById(R.id.rl_setting_feedback);
		rlSettingAbout = (RelativeLayout)findViewById(R.id.rl_setting_about);
		tvSettingLogout = (TextView)findViewById(R.id.tv_setting_logout); 
	}
	
	public void initEvents(){
		btnSettingLink.setOnClickListener(this);
		btnSettingKey.setOnClickListener(this);
//		rlSettingAccount.setOnClickListener(this);
		rlSettingFamily.setOnClickListener(this);
		rlSettingMessage.setOnClickListener(this);
		rlSettingOffline.setOnClickListener(this);
		rlSettingUpdate.setOnClickListener(this);
		rlSettingFeedback.setOnClickListener(this);
		rlSettingAbout.setOnClickListener(this);
		tvSettingLogout.setOnClickListener(this); 
	}
	
	public void initDatas(){
		tvSettingAccount.setText(CustomApplcation.getInstance().getUsername());
		if(CustomApplcation.getInstance().getCurrentCamelDevice() == null){
			return;
		}
		String id = CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify();
		if(DBHelper.getInstance(mContext).isDevicesInfoSaved(id)){
			camel = DBHelper.getInstance(mContext).getAssignDevices(id).get(0);
		}else{
			camel = null;
		}
		
		if(TextUtils.isEmpty(camel.getDeviceLigature())){
			camel.setDeviceLigature("01");
			DBHelper.getInstance(mContext).updateToDevicesInfoTable(camel);	
		}
		if(camel.getDeviceLigature().equals("02")){
			btnSettingLink.setSelected(true);
		}else if(camel.getDeviceLigature().equals("01")){
			btnSettingLink.setSelected(false);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_setting_link:
			if(camel == null){
				return;
			}
			if(camel.getDeviceLigature().equals("02")){
				camel.setDeviceLigature("01");
				long i = DBHelper.getInstance(mContext).updateToDevicesInfoTable(camel);	
				Log.i("long i" , String.valueOf(i));
				btnSettingLink.setSelected(false);
			}
			else if(camel.getDeviceLigature().equals("01")){
				camel.setDeviceLigature("02");
				long i = DBHelper.getInstance(mContext).updateToDevicesInfoTable(camel);
				Log.i("long i" , String.valueOf(i));
				btnSettingLink.setSelected(true);
			}
			
			break;
		case R.id.rl_setting_family:
			startAnimActivity(FamilyActivity.class);
			break;
		case R.id.rl_setting_message:
			startAnimActivity(CallPoliceActivity.class);
			break;
		case R.id.rl_setting_offline:
			startAnimActivity(OfflineMapActivity.class);
			break;
		case R.id.btn_setting_key:;
		    if(!btnSettingKey.isSelected() && !CustomApplcation.getInstance().getLockPatternUtils().savedPatternExists()) {
		    	btnSettingKey.setSelected(true);
		    	CustomApplcation.getInstance().setSafetyLockState("star");
		    }else{
		    	btnSettingKey.setSelected(false);
		    	CustomApplcation.getInstance().setSafetyLockState("close");
		    }
		    startAnimActivity(SafetyLockActivity.class);
			break;
		case R.id.rl_setting_update:
//			ShowToast("1");
//			UmengUpdateAgent.update(this);	
			UmengUpdateAgent.setUpdateAutoPopup(false);
			UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

				@Override
				public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
					switch (updateStatus) {
			        case UpdateStatus.Yes: // has update
			            UmengUpdateAgent.showUpdateDialog(mContext, updateInfo);
			            break;
			        case UpdateStatus.No: // has no update
			        	ShowToast("没有更新");
			            break;
			        case UpdateStatus.NoneWifi: // none wifi
			        	ShowToast("没有wifi连接， 只在wifi下更新");
			            break;
			        case UpdateStatus.Timeout: // time out
			        	ShowToast("请检查网络");
			            break;
			        }
				}
			});
			UmengUpdateAgent.forceUpdate(mContext);
			break;
		case R.id.rl_setting_feedback:
//			startAnimActivity(FeedbackActivity.class);
			agent.startFeedbackActivity();
			break;
		case R.id.rl_setting_about:
			startAnimActivity(AboutActivity.class);
			break;
		case R.id.tv_setting_logout:
			setResult(0);
			CustomApplcation.getInstance().setCurrentUser(""); // 自动登入
			DBHelper.getInstance(mContext).logout(); // 清除数据库数据
			Intent intent = new Intent(this,LoginActivity.class);
			startActivity(intent);
			finish();
			break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// 判断是否设置了锁屏密码,如果没设置,跳转到设置界面
		if (!CustomApplcation.getInstance().getLockPatternUtils().savedPatternExists()) {
			btnSettingKey.setSelected(false);
		}else {
			btnSettingKey.setSelected(true);
		}
	}
}

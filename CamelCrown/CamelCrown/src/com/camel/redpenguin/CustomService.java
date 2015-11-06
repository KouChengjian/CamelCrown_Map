package com.camel.redpenguin;

import com.camel.redpenguin.bean.Packet;
import com.camel.redpenguin.config.SocketInit;
import com.camel.redpenguin.listener.HeartbeatHzListener;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;


/**
 * @ClassName: CustomService
 * @Description: 服务
 * @author:  
 * @date:  
 */
public class CustomService extends Service{
//	private static Context mContext = CustomApplcation.getInstance();
	// 验证发生时间
	private int mReSendTime = 60;
	private int mTime = 0;
	
	public void onCreate() {
		super.onCreate();
//		SocketInit.init(this ,Config.SERVE_IP, Config.SERVE_PORT);  //"192.168.10.114", 65431
		handler.sendEmptyMessage(0);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		flags = START_STICKY; 
		return super.onStartCommand(intent, flags, startId);
//		return START_STICKY;
	}
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (mReSendTime > 1) {
				mReSendTime--;
				mTime++;
				handler.sendEmptyMessageDelayed(0, 1000);
				if(mTime == 10){
					mTime = 0;
					if(SocketInit.getInstance() == null){
						return;
					}
					SocketInit.getInstance().heartbeatHz();
				}
			} else {
				if(SocketInit.getInstance() == null){
					return;
				}
				SocketInit.getInstance().heartbeatHz(new Packet(), new HeartbeatHzListener(){

					@Override
					public void onSuccess() {
						
					}

					@Override
					public void onFailure(int paramInt, String paramString) {
						SocketInit.getInstance().close();
					}
	
				});
				mReSendTime = 60;
				handler.sendEmptyMessage(0);
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
    
}

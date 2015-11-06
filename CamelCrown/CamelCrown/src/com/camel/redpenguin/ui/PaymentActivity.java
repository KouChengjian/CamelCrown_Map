package com.camel.redpenguin.ui;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.camel.redpenguin.R;
import com.camel.redpenguin.bean.DBHelper;
import com.camel.redpenguin.greendao.CamelUser;
import com.camel.redpenguin.view.HeaderLayout.onRightImageButtonClickListener;

/**
 * @ClassName: PaymentActivity
 * @Description: 设备支付
 * @author: kcj
 * @date: 
 */
public class PaymentActivity extends BaseActivity{

	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment);
		/** header */
		initTopBarForBoth("设备支付", R.drawable.base_action_bar_more_bg_selector,
				new onRightImageButtonClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
		
		// 来自服务器的数据
		final CamelUser camelUser1 = new CamelUser();
		camelUser1.setUserAccount("18710627436");
		camelUser1.setUserPassword("kcj844067");
		camelUser1.setUserPhonetype("android");
		camelUser1.setUserLatitude("20.545445");
		camelUser1.setUserLongitude("136.24552");
		camelUser1.setUserNike("hahahaha");
		camelUser1.setUserPhoneIdentify("123456789");
		camelUser1.setUserAdministrator("0");
		camelUser1.setUserUpdateDate("2015-2-20");
		camelUser1.setUserCreateDate("");
		
		
		if(DBHelper.getInstance(mContext).isUsersInfoSaved(camelUser1.getUserAccount())){
			Log.e("TAG", "mCamelUser");
		}else{
			Log.e("TAG", "null");
		}
				
//		CamelUser mCamelUser = DBHelper.getInstance(mContext).getCurrentUsers(camelUser1.getUserAccount()).get(0);
//		if(mCamelUser == null){
//			Log.e("TAG", "null");
//		}else{
//			Log.e("TAG", "mCamelUser");
//		}
		
		/** 测试 */
		Button Btn1 = (Button)findViewById(R.id.button1);
		Btn1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				DBHelper.getInstance(mContext).addToUsersInfoTable(camelUser1);
				
				
				/**  
				CamelUser mCamelUser = DBHelper.getInstance(mContext).getCurrentUsers(camelUser1.getUserAccount()).get(0);
				Log.e("TAG6", "mCamelUser");
				if(mCamelUser == null){
					Log.e("TAG7", "null");
					mCamelUser = new CamelUser();
				}
				
				
				List<CamelUser> list= DBHelper.getInstance(getApplicationContext()).getCurrentUsers(camelUser1.getUserAccount());
				Log.e("CamelUserlist",String.valueOf(list.size()));
				for(int i = 0 ; i < list.size() ; i++){
					Log.e("CamelUserlist",list.get(i).getUserAccount());
					Log.e("CamelUserlist",list.get(i).getUserPassword());
					Log.e("CamelUserlist",list.get(i).getUserUpdateDate());
				}
				*/
			
			
			
			}
			
		});
		
		Button Btn2 = (Button)findViewById(R.id.button2);
		Btn2.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CamelUser mCamelUser = DBHelper.getInstance(mContext).getCurrentUsers(camelUser1.getUserAccount()).get(0);
				if(mCamelUser == null){
					Log.e("TAG", "null");
				}else{
					Log.e("TAG", "mCamelUser");
				}
			}
			
		});
		
		Button Btn3 = (Button)findViewById(R.id.button3);
		Btn3.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
	}
}

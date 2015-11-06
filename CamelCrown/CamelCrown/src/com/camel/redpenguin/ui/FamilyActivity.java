package com.camel.redpenguin.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.camel.redpenguin.CustomApplcation;
import com.camel.redpenguin.R;
import com.camel.redpenguin.adapter.FamilyMemberAdapter;
import com.camel.redpenguin.adapter.UserDeviceAdapter;
import com.camel.redpenguin.bean.DBHelper;
import com.camel.redpenguin.greendao.CamelDevice;
import com.camel.redpenguin.greendao.DataRelFamily;
import com.camel.redpenguin.util.HexadecimalConver;

/**
 * @ClassName: FamilyActivity
 * @Description: 家庭成员
 * @author kcj
 * @date
 */
public class FamilyActivity extends BaseActivity implements OnItemClickListener,OnItemLongClickListener{

	private ListView list_family;
	private FamilyMemberAdapter familyMemberAdapter;
	private List<DataRelFamily> familyMember = new ArrayList<DataRelFamily>();
	
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_family);
		/** header */
		initTopBarForLeft("家庭成员");
		/** UI */
		list_family = (ListView)findViewById(R.id.lv_family_member);
		familyMemberAdapter = new FamilyMemberAdapter(this,familyMember);
		list_family.setAdapter(familyMemberAdapter);
		list_family.setOnItemClickListener(this);
		list_family.setOnItemLongClickListener(this);
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
					queryFamilyMember();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void queryFamilyMember(){
		if(!DBHelper.getInstance(mContext).isDataRelFamilySaved(CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify())){
			Log.e("total2", "无数据");
			return;
		}
		List<DataRelFamily> dataRelFamily = DBHelper.getInstance(mContext).getDataRelFamilyToIdentify(CustomApplcation.getInstance().getCurrentCamelDevice().getDeviceIdentify());
		Log.e("total2", String.valueOf(dataRelFamily.size()));
		for(int i = 0 ; i < dataRelFamily.size() ; i++){
			Log.e("list", String.valueOf(dataRelFamily.get(i).getDataRelAccount()));
			Log.e("list", String.valueOf(dataRelFamily.get(i).getDataRelNick()));

		}
		filledData(dataRelFamily);
		if(familyMemberAdapter==null){
			familyMemberAdapter = new FamilyMemberAdapter(this,familyMember);
			list_family.setAdapter(familyMemberAdapter);
		}else{
			familyMemberAdapter.notifyDataSetChanged();
		}
	}
	
	private void filledData(List<DataRelFamily> datas) {
		familyMember.clear();
		int total = datas.size();
		Log.e("total", String.valueOf(total));
		for (int i = 0; i < total; i++) {
			DataRelFamily mDataRelFamily = datas.get(i);
			DataRelFamily filledDataRelFamily = new DataRelFamily();
			filledDataRelFamily.setDataRelAccount(mDataRelFamily.getDataRelAccount());
			filledDataRelFamily.setDataRelNick("用户       ");
			filledDataRelFamily.setDataRelAdministrator(mDataRelFamily.getDataRelAdministrator());
			familyMember.add(filledDataRelFamily);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
}

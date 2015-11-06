package com.camel.redpenguin.ui;

import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.camel.redpenguin.R;
import com.camel.redpenguin.view.HeaderLayout;
import com.camel.redpenguin.view.HeaderLayout.HeaderStyle;
import com.camel.redpenguin.view.HeaderLayout.onLeftImageButtonClickListener;
import com.camel.redpenguin.view.HeaderLayout.onRightImageButtonClickListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

/** 
 * @ClassName: FragmentBase
 * @Description: 
 * @author 
 * @date 
 */
public class FragmentBase extends Fragment{
	Toast mToast;
	Context mContext;
	protected HeaderLayout mHeaderLayout;
	
	// 百度离线
	public MKOfflineMap mOffline = null;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//setRetainInstance(true);
		mContext = getActivity();
		mOffline = new MKOfflineMap();
	}
	
	/**
	 * 动画启动页面 startAnimActivity
	 * @throws
	 */
	public void startAnimActivity(Intent intent) {
		this.startActivity(intent);
	}
	public void startAnimActivity(Class<?> cla) {
		getActivity().startActivity(new Intent(getActivity(), cla));
	}
	
	public void ShowToast(String text) {
		if (mToast == null) {
			mToast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
		}
		mToast.show();
	}
	public void ShowToast(int text) {
		if (mToast == null) {
			mToast = Toast.makeText(getActivity(), text, Toast.LENGTH_LONG);
		} else {
			mToast.setText(text);
		}
		mToast.show();
	}
	
	/**
	 * @Description:只有title
	 * @Title: initTopBarLayoutByTitle
	 * @throws
	 */
	public void initTopBarForOnlyTitle(String titleName,View v) {
		mHeaderLayout = (HeaderLayout)v.findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
		mHeaderLayout.setDefaultTitle(titleName);
	}

	/**
	 * @Description :只有左边按钮和Title
	 * @Title:initTopBarLayout
	 * @throws
	 */
	public void initTopBarForLeft(String titleName,View v) {
		mHeaderLayout = (HeaderLayout) v.findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,
				R.drawable.base_action_bar_back_bg_selector,
				new OnLeftButtonClickListener());
	}

	/**
	 * 默认左右按钮
	 * @Description:初始化标题栏-带左右按钮
	 * @return void
	 * @throws
	 */
	public void initTopBarForBoth(String titleName,View v ,int rightDrawableId,
			 onRightImageButtonClickListener listener) {
		mHeaderLayout = (HeaderLayout) v.findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,
		R.drawable.base_action_bar_menu_bg_selector,
		new OnLeftButtonClickListener());
		mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId, 
				listener);
	}

	/**
	 * 自定义左右按钮
	 * @Description:初始化标题栏-带左右按钮
	 * @return void
	 * @throws
	 */
	public void initTopBarForBoth(String titleName ,View v, int rightDrawableId,
			 onRightImageButtonClickListener listener,int leftDrawableId, 
			 onLeftImageButtonClickListener listenerLeft) {
		mHeaderLayout = (HeaderLayout) v.findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId, 
				listener);
		mHeaderLayout.setTitleAndLeftImageButton(titleName, leftDrawableId,
				listenerLeft);
	}

	/**
	 *  (默认关闭)左边按钮的点击事件
	 */
	public class OnLeftButtonClickListener implements
			onLeftImageButtonClickListener {
		@Override
		public void onClick(View v) {
			
		}
	}
}

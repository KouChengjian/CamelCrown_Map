package com.camel.redpenguin.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.camel.redpenguin.CustomApplcation;
import com.camel.redpenguin.R;
import com.camel.redpenguin.bean.DBHelper;
import com.camel.redpenguin.config.Config;
import com.camel.redpenguin.config.SocketInit;
import com.camel.redpenguin.greendao.CamelDevice;
import com.camel.redpenguin.greendao.CamelUser;
import com.camel.redpenguin.listener.FindFamilyListener;
import com.camel.redpenguin.listener.FindListener;
import com.camel.redpenguin.listener.FindNewMessageListener;
import com.camel.redpenguin.listener.FindStateListener;
import com.camel.redpenguin.util.NetworkUtil;
import com.camel.redpenguin.view.FlippingLoadingDialog;
import com.camel.redpenguin.view.HandyTextView;
import com.camel.redpenguin.view.HeaderLayout;
import com.camel.redpenguin.view.HeaderLayout.HeaderStyle;
import com.camel.redpenguin.view.HeaderLayout.onLeftImageButtonClickListener;
import com.camel.redpenguin.view.HeaderLayout.onLeftMiddleClickListener;
import com.camel.redpenguin.view.HeaderLayout.onMiddleClickListener;
import com.camel.redpenguin.view.HeaderLayout.onRightImageButtonClickListener;
import com.camel.redpenguin.view.HeaderLayout.onRightMiddleClickListener;
import com.umeng.fb.FeedbackAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @ClassName:BaseActivity
 * @Description:
 * @author: kcj
 * @date:
 */
@SuppressLint("SimpleDateFormat")
public class BaseActivity extends FragmentActivity {

	protected Toast mToast;
	public Context mContext;
	protected HeaderLayout mHeaderLayout;
	CustomApplcation mApplication;
	// 对话框
	protected FlippingLoadingDialog mLoadingDialog;
	// socket
	SocketInit mSocketInit = null;
	// 友盟
	FeedbackAgent agent; // 反馈

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate(savedInstanceState);
		mContext = this;
		mSocketInit = SocketInit.getInstance();
		mApplication = CustomApplcation.getInstance();
		mLoadingDialog = new FlippingLoadingDialog(this, "请求提交中");
		agent = new FeedbackAgent(mContext);
	}

	/**
	 * 对话框 - 显示
	 */
	protected void showLoadingDialog(String text) {
		if (text != null) {
			mLoadingDialog.setText(text);
		}
		mLoadingDialog.show();
		mLoadingDialog.setCanceledOnTouchOutside(false);
	}

	/**
	 * 对话框-消失
	 */
	protected void dismissLoadingDialog() {
		if (mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}
	}

	/** 显示自定义Toast提示(来自String) **/
	protected void showCustomToast(String text) {
		View toastRoot = LayoutInflater.from(BaseActivity.this).inflate(
				R.layout.common_toast, null);
		((HandyTextView) toastRoot.findViewById(R.id.toast_text)).setText(text);
		Toast toast = new Toast(BaseActivity.this);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}

	/**
	 * 启动其他界面
	 */
	public void startAnimActivity(Class<?> cla) {
		this.startActivity(new Intent(this, cla));
	}

	public void startAnimActivity(Intent intent) {
		this.startActivity(intent);
	}

	/**
	 * 显示Toast
	 */
	public void ShowToast(final String text) {
		if (!TextUtils.isEmpty(text)) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (mToast == null) {
						mToast = Toast.makeText(getApplicationContext(), text,
								Toast.LENGTH_LONG);
					} else {
						mToast.setText(text);
					}
					mToast.show();
				}
			});

		}
	}

	public void ShowToast(final int resId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mToast == null) {
					mToast = Toast.makeText(
							BaseActivity.this.getApplicationContext(), resId,
							Toast.LENGTH_LONG);
				} else {
					mToast.setText(resId);
				}
				mToast.show();
			}
		});
	}

	public void setTopBarForOnlyTitle(String titleName) {
		if (mHeaderLayout != null) {
			mHeaderLayout.setDefaultTitle(titleName);
		}
	}

	/**
	 * @Description:只有title
	 * @Title: initTopBarLayoutByTitle
	 * @throws
	 */
	public void initTopBarForOnlyTitle(String titleName) {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
		mHeaderLayout.setDefaultTitle(titleName);
	}

	/**
	 * @Description:中间有按钮的标题 和 左右
	 * @Title: initTopBarForMiddleContainer
	 * @throws
	 */
	public void initTopBarForMiddleContainer(int leftDrawableId,
			onLeftImageButtonClickListener listenerLeft, int rightDrawableId,
			onRightImageButtonClickListener listenerRight, String lefttMiddle,
			onLeftMiddleClickListener leftMiddleClickListener,
			String rightMiddle,
			onRightMiddleClickListener rightMiddleClickListener) {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		mHeaderLayout
				.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON_MiddleContainer);
		mHeaderLayout.setDefaultTitle(lefttMiddle, rightMiddle);
		mHeaderLayout.setTitleAndLeftImageButton(null, leftDrawableId,
				listenerLeft);
		mHeaderLayout.setTitleAndRightImageButton(null, rightDrawableId,
				listenerRight);
		mHeaderLayout.setMiddleContainer(leftMiddleClickListener,
				rightMiddleClickListener);
	}

	/**
	 * @Description:中间有按钮的标题 和 左
	 * @Title: initTopBarForMiddleContainer
	 * @throws
	 */
	public void initTopBarForMiddleContainerLeft(int leftDrawableId,
			onLeftImageButtonClickListener listenerLeft, String lefttMiddle,
			onLeftMiddleClickListener leftMiddleClickListener,
			String rightMiddle,
			onRightMiddleClickListener rightMiddleClickListener) {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		mHeaderLayout
				.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON_MiddleContainer);
		mHeaderLayout.setDefaultTitle(lefttMiddle, rightMiddle);
		setSelectLeftMiddle();
		mHeaderLayout.setTitleAndLeftImageButton(null, leftDrawableId,
				listenerLeft);
		mHeaderLayout.setMiddleContainer(leftMiddleClickListener,
				rightMiddleClickListener);
	}

	public void setSelectLeftMiddle() {
		mHeaderLayout.setSelectLeftMiddle();
	}

	public void setSelectRightMiddle() {
		mHeaderLayout.setSelectRightMiddle();
	}

	/**
	 * @Description :只有左边按钮和Title
	 * @Title:initTopBarLayout
	 * @throws
	 */
	public void initTopBarForLeft(String titleName) {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,
				R.drawable.base_action_bar_back_bg_selector,
				new OnLeftButtonClickListener());
	}

	/**
	 * 默认左右按钮
	 * 
	 * @Description:初始化标题栏-带左右按钮
	 * @return void
	 * @throws
	 */
	public void initTopBarForBoth(String titleName, int rightDrawableId,
			onRightImageButtonClickListener listener) {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,
				R.drawable.base_action_bar_back_bg_selector,
				new OnLeftButtonClickListener());
		mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId,
				listener);
	}

	/**
	 * 中间有图标可点击
	 * 
	 * @Description:初始化标题栏-带左右按钮
	 * @return void
	 * @throws
	 */
	public void initTopBarForBoth(String titleName, int rightDrawableId,
			onRightImageButtonClickListener listener,
			onMiddleClickListener clickListener) {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON_ICON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,
				R.drawable.base_action_bar_back_bg_selector,
				new OnLeftButtonClickListener());
		mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId,
				listener);
		mHeaderLayout.setDefaultTitle(titleName, clickListener);
	}

	/**
	 * 自定义左右按钮
	 * 
	 * @Description:初始化标题栏-带左右按钮
	 * @return void
	 * @throws
	 */
	public void initTopBarForBoth(String titleName, int rightDrawableId,
			onRightImageButtonClickListener listener, int leftDrawableId,
			onLeftImageButtonClickListener listenerLeft) {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName, leftDrawableId,
				listenerLeft);
		mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId,
				listener);

	}

	/**
	 * 左边按钮的点击事件
	 */
	public class OnLeftButtonClickListener implements
			onLeftImageButtonClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
	}

	/**
	 * @Title: updateDeviceData
	 * @Description: 更新当前设备数据
	 * @param:
	 * @return: void 3A
	 * @throws:
	 */
	public void updateDeviceData() {
		// 更新经纬度
		updateUserLocation();

		SocketInit.getInstance().queryCurrentContactList(new FindListener() {
			@Override
			public void onSuccess(final List<CamelDevice> mCamelDevice) {
				// 延迟两秒，防止还没进入主界面初始化未完成就更新组界面
				try {
					runOnUiThread(new Runnable() {
						public void run() {
							new Handler().postDelayed(new Runnable() {
								public void run() {
									CustomApplcation.getInstance().setContactList(mCamelDevice);
									Intent intent = new Intent();
									intent.setAction("action.main.refresh.3a");
									mContext.sendBroadcast(intent);
								}
							}, 2000);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int paramInt, String paramString) {}
		});
	}

	/**
	 * @Title: updateMessageData
	 * @Description: 获取历史数据
	 * @param
	 * @return void 3E
	 * @throws
	 */
	@SuppressLint("SimpleDateFormat")
	public void updateMessageData() {
		if(CustomApplcation.getInstance().getCurrentCamelDevice() == null){
			return ;
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String time = formatter.format(curDate);
		SocketInit.getInstance().queryCurrentDeviceState(Config.HISTORY_PAGE,
				time, new FindStateListener() {

					@Override
					public void onSuccess() {
						try {
							runOnUiThread(new Runnable() {
								public void run() {
									new Handler().postDelayed(new Runnable() {
										public void run() {
											Config.HISTORY_PAGE++;
											updateMessageData();
										}
									}, 2000);
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

					@Override
					public void onFailure(int paramInt, String paramString) {

					}

				});
	}

	/**
	 * @Title: updateFamilyMember
	 * @Description: 更新家庭成员
	 * @param
	 * @return void 3D
	 * @throws
	 */
	public void updateFamilyMember() {
		if(CustomApplcation.getInstance().getCurrentCamelDevice() == null){
			return;
		}
		SocketInit.getInstance().queryCurrentFamilyMember(
				new FindFamilyListener() {
					@Override
					public void onSuccess() {
						Log.e("updateFamilyMember", "成功");
					}

					@Override
					public void onFailure(int paramInt, String paramString) {
						Log.e("updateFamilyMember", paramString);
					}

				});
	}

	/**
	 * @Title: updateDeviceNewestMessage
	 * @Description: 更新当前设备最新数据
	 * @param
	 * @return void 4A
	 * @throws
	 */
	public void updateDeviceNewestMessage() {
		if(CustomApplcation.getInstance().getCurrentCamelDevice() == null){
			return ;
		}
		SocketInit.getInstance().queryCurrentNewestMessage(new FindNewMessageListener() {
			@Override
			public void onSuccess() {}

			@Override
			public void onFailure(int paramInt, String paramString) {
				Log.e("updateFamilyMember", paramString);
			}

		});
	}

	/**
	 * @Title: updateUserLocation
	 * @Description: 更新经纬度
	 * @param
	 * @return void
	 * @throws
	 */
	public void updateUserLocation() {
		String newLat = mApplication.getLatitude();
		String newLong = mApplication.getLongtitude();
		if (TextUtils.isEmpty(newLat) && TextUtils.isEmpty(newLong))
			return;
		// 是否存在数据
		if (!DBHelper.getInstance(mContext).isUsersInfoSaved(
				mApplication.getUsername())) {
			return;
		}
		// 获取当前用户名
		final CamelUser mCamelUser = DBHelper
				.getInstance(getApplicationContext())
				.getCurrentUsers(mApplication.getUsername()).get(0);
		String saveLatitude = mCamelUser.getUserLatitude();
		String saveLongtitude = mCamelUser.getUserLongitude();
		if (!saveLatitude.equals(newLat) || !saveLongtitude.equals(newLong)) {// 只有位置有变化就更新当前位置，达到实时更新的目的
			mCamelUser.setUserLatitude(newLat); // 设置纬度
			mCamelUser.setUserLongitude(newLong);// 设置经度

			if (DBHelper.getInstance(mContext).isUsersInfoSaved(
					mCamelUser.getUserAccount())) {
				Log.e("latlon", "更新经纬度成功");
				DBHelper.getInstance(mContext).updateToUsersInfoTable(mCamelUser);
			}
		}
	}

	public String getCurrentDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		return str;
	}

	/**
	 * @Title: showSoftInputView
	 * @Description: 显示软键盘
	 * @param
	 * @return void
	 * @throws
	 */
	public void showSoftInputView(EditText edit) {
		if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.showSoftInput(edit, 0);
		}
	}

	/**
	 * @Title: hideSoftInputView
	 * @Description: 隐藏软键盘 
	 * @param
	 * @return void
	 * @throws
	 */
	public void hideSoftInputView() {
		InputMethodManager manager = ((InputMethodManager) this
				.getSystemService(Activity.INPUT_METHOD_SERVICE));
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}

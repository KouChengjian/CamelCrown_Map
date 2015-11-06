package com.camel.redpenguin.view;

import com.camel.redpenguin.R;
import com.camel.redpenguin.util.PixelUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/** 
  * @ClassName: HeaderLayout
  * @Description: 自定义头部布局
  * @author 
  * @date 
  */
public class HeaderLayout extends LinearLayout {
	private LayoutInflater mInflater;
	private View mHeader;
	private LinearLayout mLayoutLeftContainer;
	private LinearLayout mLayoutRightContainer;
	private TextView mHtvSubTitle;
	private LinearLayout mLayoutRightImageButtonLayout;
	private Button mRightImageButton;
	private onRightImageButtonClickListener mRightImageButtonClickListener;

	private LinearLayout mLayoutLeftImageButtonLayout;
	private ImageButton mLeftImageButton;
	private onLeftImageButtonClickListener mLeftImageButtonClickListener;
	
	/** 后期添加的 */
	private TextView mHtvSubTitleIcon;
	private Button btnLeftMiddleContainer;
	private Button btnRightMiddleContainer;
	private LinearLayout mLayoutMiddleContainer;

	public enum HeaderStyle {// 头部整体样式
		DEFAULT_TITLE, TITLE_LIFT_IMAGEBUTTON, TITLE_RIGHT_IMAGEBUTTON, TITLE_DOUBLE_IMAGEBUTTON,
		TITLE_DOUBLE_IMAGEBUTTON_MiddleContainer,TITLE_DOUBLE_IMAGEBUTTON_ICON
	    ,TITLE_DOUBLE_IMAGEBUTTON_MiddleContainer_LEFT;
	}

	public HeaderLayout(Context context) {
		super(context);
		init(context);
	}

	public HeaderLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void init(Context context) {
		mInflater = LayoutInflater.from(context);
		mHeader = mInflater.inflate(R.layout.common_header, null);
		addView(mHeader);
		initViews();
	}

	public void initViews() {
		mLayoutLeftContainer = (LinearLayout) findViewByHeaderId(R.id.header_layout_leftview_container);
		//mLayoutMiddleContainer = (LinearLayout)findViewByHeaderId(R.id.header_layout_middleview_container);//中间部分添加搜索或者其他按钮时可打开
		mLayoutRightContainer = (LinearLayout) findViewByHeaderId(R.id.header_layout_rightview_container);
		mHtvSubTitle = (TextView) findViewByHeaderId(R.id.header_htv_subtitle);
        
		/** 后期添加的 */
		mHtvSubTitleIcon = (TextView)findViewById(R.id.header_htv_subtitle_icon);
		btnLeftMiddleContainer = (Button)findViewById(R.id.btn_left_header_layout_middleview_containers);
		btnRightMiddleContainer = (Button)findViewById(R.id.btn_right_header_layout_middleview_containers);
		mLayoutMiddleContainer = (LinearLayout)findViewById(R.id.header_layout_middleview_containers);
	}

	public View findViewByHeaderId(int id) {
		return mHeader.findViewById(id);
	}

	public void init(HeaderStyle hStyle) {
		switch (hStyle) {
		case DEFAULT_TITLE:
			defaultTitle();
			break;

		case TITLE_LIFT_IMAGEBUTTON:
			defaultTitle();
			titleLeftImageButton();
			break;

		case TITLE_RIGHT_IMAGEBUTTON:
			defaultTitle();
			titleRightImageButton();
			break;

		case TITLE_DOUBLE_IMAGEBUTTON:
			defaultTitle();
			titleLeftImageButton();
			titleRightImageButton();
			break;
		case TITLE_DOUBLE_IMAGEBUTTON_ICON:
			defaultTitle();
			titleTextView();
			titleLeftImageButton();
			titleRightImageButton();
			break;
		case TITLE_DOUBLE_IMAGEBUTTON_MiddleContainer:
			defaultTitle();
			titleLeftImageButton();
			titleRightImageButton();
			titleMiddleContainer();
			break;
		case TITLE_DOUBLE_IMAGEBUTTON_MiddleContainer_LEFT:
			defaultTitle();
			titleLeftImageButton();
			titleMiddleContainer();
			break;
		}
	}

	// 默认文字标题
	private void defaultTitle() {
		mLayoutLeftContainer.removeAllViews();
		mLayoutRightContainer.removeAllViews();
	}
	
	// text按钮
	private void titleTextView(){
		mHtvSubTitleIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mMiddleClickListener != null) {
					mMiddleClickListener.onClick(arg0);
				}
			}
		});
	}

	// 左侧自定义按钮
	private void titleLeftImageButton() {
		View mleftImageButtonView = mInflater.inflate(
				R.layout.common_header_button, null);
		mLayoutLeftContainer.addView(mleftImageButtonView);
		mLayoutLeftImageButtonLayout = (LinearLayout) mleftImageButtonView
				.findViewById(R.id.header_layout_imagebuttonlayout);
		mLeftImageButton = (ImageButton) mleftImageButtonView
				.findViewById(R.id.header_ib_imagebutton);
		mLayoutLeftImageButtonLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mLeftImageButtonClickListener != null) {
					mLeftImageButtonClickListener.onClick(arg0);
				}
			}
		});
	}

	// 右侧自定义按钮
	private void titleRightImageButton() {
		View mRightImageButtonView = mInflater.inflate(
				R.layout.common_header_rightbutton, null);
		mLayoutRightContainer.addView(mRightImageButtonView);
		mLayoutRightImageButtonLayout = (LinearLayout) mRightImageButtonView
				.findViewById(R.id.header_layout_imagebuttonlayout);
		mRightImageButton = (Button) mRightImageButtonView
				.findViewById(R.id.header_ib_imagebutton);
		mLayoutRightImageButtonLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mRightImageButtonClickListener != null) {
					mRightImageButtonClickListener.onClick(arg0);
				}
			}
		});
	}
	
	// 中间
	private void titleMiddleContainer() {
		btnLeftMiddleContainer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(mLeftMiddleClickListener != null){
					mLeftMiddleClickListener.onClick(arg0);
				}
			}
		});
		btnRightMiddleContainer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(mRightMiddleClickListener != null){
					mRightMiddleClickListener.onClick(arg0);
				}
			}
		});
	}

	/** 获取右边按钮
	  * @Title: getRightImageButton
	  * @Description: TODO
	  * @param @return 
	  * @return Button
	  * @throws
	  */
	public Button getRightImageButton(){
		if(mRightImageButton!=null){
			return mRightImageButton;
		}
		return null;
	}
	public void setDefaultTitle(CharSequence title) {
		if (title != null) {
			mHtvSubTitle.setText(title);
			mHtvSubTitle.setVisibility(View.VISIBLE);
			mHtvSubTitleIcon.setVisibility(View.GONE);
		} else {
			mHtvSubTitle.setVisibility(View.GONE);
		}
	}
	
	public void setDefaultTitle(CharSequence title,onMiddleClickListener clickListener) {
		if (title != null) {
			mHtvSubTitleIcon.setText(title);
			mHtvSubTitleIcon.setVisibility(View.VISIBLE);
			mHtvSubTitle.setVisibility(View.GONE);
			setOnMiddleClickListener(clickListener);
		} else {
			mHtvSubTitleIcon.setVisibility(View.GONE);
		}
	}

	public void setTitleAndRightButton(CharSequence title, int backid,String text,
			onRightImageButtonClickListener onRightImageButtonClickListener) {
		setDefaultTitle(title);
		mLayoutRightContainer.setVisibility(View.VISIBLE);
		if (mRightImageButton != null && backid > 0) {
			mRightImageButton.setWidth(PixelUtil.dp2px(45));
			mRightImageButton.setHeight(PixelUtil.dp2px(40));
			mRightImageButton.setBackgroundResource(backid);
			mRightImageButton.setText(text);
			setOnRightImageButtonClickListener(onRightImageButtonClickListener);
		}
	}
	
	public void setTitleAndRightImageButton(CharSequence title, int backid,
			onRightImageButtonClickListener onRightImageButtonClickListener) {
		setDefaultTitle(title);
		mLayoutRightContainer.setVisibility(View.VISIBLE);
		if (mRightImageButton != null && backid > 0) {
			mRightImageButton.setWidth(PixelUtil.dp2px(30));
			mRightImageButton.setHeight(PixelUtil.dp2px(30));
			mRightImageButton.setTextColor(getResources().getColor(R.color.transparent));
			mRightImageButton.setBackgroundResource(backid);
			setOnRightImageButtonClickListener(onRightImageButtonClickListener);
		}
	}

	public void setTitleAndLeftImageButton(CharSequence title, int id,
			onLeftImageButtonClickListener listener) {
		setDefaultTitle(title);
		if (mLeftImageButton != null && id > 0) {
			mLeftImageButton.setImageResource(id);
			setOnLeftImageButtonClickListener(listener);
		}
		mLayoutRightContainer.setVisibility(View.INVISIBLE);
	}
	
	public void setMiddleContainer(onLeftMiddleClickListener leftMiddleClickListener,
			onRightMiddleClickListener rightMiddleClickListener) {
		setOnLeftMiddleClickListener(leftMiddleClickListener);
		setOnRightMiddleClickListener(rightMiddleClickListener);
	}
	
	/** 后期添加的 star*/
	public void setDefaultTitle(String left ,String right) {
		mHtvSubTitle.setVisibility(View.GONE);
		mLayoutMiddleContainer.setVisibility(View.VISIBLE);
		btnLeftMiddleContainer.setText(left);
		btnRightMiddleContainer.setText(right);
	}
	
    public void setSelectLeftMiddle(){
    	btnLeftMiddleContainer.setEnabled(false);
		btnRightMiddleContainer.setEnabled(true);
	}
	public void setSelectRightMiddle(){
		btnLeftMiddleContainer.setEnabled(true);
		btnRightMiddleContainer.setEnabled(false);
	}
	
	onLeftMiddleClickListener mLeftMiddleClickListener;
	public void setOnLeftMiddleClickListener(onLeftMiddleClickListener listener) {
		mLeftMiddleClickListener = listener;
	}
	public interface onLeftMiddleClickListener {
		void onClick(View v);
	}
	onRightMiddleClickListener mRightMiddleClickListener;
	public void setOnRightMiddleClickListener(onRightMiddleClickListener listenerRight) {
		mRightMiddleClickListener = listenerRight;
	}
	public interface onRightMiddleClickListener {
		void onClick(View v);
	}
	
	onMiddleClickListener mMiddleClickListener;
	public void setOnMiddleClickListener(onMiddleClickListener listener) {
		mMiddleClickListener = listener;
	}
	public interface onMiddleClickListener {
		void onClick(View v);
	}
	
	/** 后期添加的 end*/
	

	public void setOnRightImageButtonClickListener(
			onRightImageButtonClickListener listener) {
		mRightImageButtonClickListener = listener;
	}

	public interface onRightImageButtonClickListener {
		void onClick(View v);
	}

	public void setOnLeftImageButtonClickListener(
			onLeftImageButtonClickListener listener) {
		mLeftImageButtonClickListener = listener;
	}

	public interface onLeftImageButtonClickListener {
		void onClick(View v);
	}

}

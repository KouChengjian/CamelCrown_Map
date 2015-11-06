package com.camel.redpenguin.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.camel.redpenguin.R;
import com.camel.redpenguin.adapter.base.BaseContentAdapter;
import com.camel.redpenguin.bean.SafetyMessage;
import com.camel.redpenguin.greendao.CamelDevice;
import com.camel.redpenguin.util.PixelUtil;


/**
 * @ClassName: MessageContentAdapter
 * @Description: 设备发送的信息
 * @author kcj
 * @date
 */
public class MessageContentAdapter extends BaseContentAdapter<SafetyMessage>{

	public MessageContentAdapter(Context context, List<SafetyMessage> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getConvertView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_message, null);
			viewHolder.messageIcon = (ImageView)convertView.findViewById(R.id.img_message_icon);
			viewHolder.messageContent = (TextView)convertView.findViewById(R.id.tv_message_content);
			viewHolder.alpha = (TextView)convertView.findViewById(R.id.alpha);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		SafetyMessage safetyMessage = dataList.get(position);
		int type = safetyMessage.getMessageType();
		if(type == 1){ // 低电
			Bitmap btm1 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.display_electric_5);
    		Bitmap mBitmap = Bitmap.createScaledBitmap(btm1, PixelUtil.dp2px(50), PixelUtil.dp2px(50), true);
    		viewHolder.messageIcon.setImageBitmap(mBitmap);
		}else if(type == 2){ // sos
			Bitmap btm1 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.display_electric_5);
    		Bitmap mBitmap = Bitmap.createScaledBitmap(btm1, PixelUtil.dp2px(50), PixelUtil.dp2px(50), true);
    		viewHolder.messageIcon.setImageBitmap(mBitmap);
		}
		viewHolder.messageContent.setText(safetyMessage.getMessageContemt());
		viewHolder.alpha.setText(safetyMessage.getMessageTime());
		if(position >= 1){
			String time =  dataList.get(position - 1).getMessageTime();
			if(safetyMessage.getMessageTime().equals(time)){
				viewHolder.alpha.setVisibility(View.GONE);
			}else{
				viewHolder.alpha.setVisibility(View.VISIBLE);
			}
		}
		return convertView;
	}

	public static class ViewHolder{
		public ImageView messageIcon;
		public TextView  messageContent;
		public TextView  alpha;
	}
}

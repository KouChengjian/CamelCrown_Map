package com.camel.redpenguin.bean;

import java.util.ArrayList;
import java.util.List;

import com.camel.redpenguin.greendao.CamelDeviceHistory;


public class HistoryItem {
	
	public int icon; // 图像
	public String addr; // 地址
	public String timeQuantum; // 时间段
	public String timeCount; // 时间总数
	
	List<CamelDeviceHistory> mAllListItems = new ArrayList<CamelDeviceHistory>();
	
	public void setHistoryItemIcon(int icon){
		this.icon = icon ;
	}
	
	public int getHistoryItemIcon(){
		return icon;
	}
	
	public void setHistoryItemAddr(String addr){
		this.addr = addr ;
	}
	
	public String getHistoryItemAddr(){
		return addr;
	}
	
	public void setHistoryItemTimeQuantum(String timeQuantum){
		this.timeQuantum = timeQuantum ;
	}
	
	public String getHistoryItemTimeQuantum(){
		return timeQuantum;
	}
	
	public void setHistoryItemTimeCount(String timeCount){
		this.timeCount = timeCount ;
	}
	
	public String getHistoryItemTimeCount(){
		return timeCount;
	}
	
	public void setHistoryItemLatLngCount(List<CamelDeviceHistory> mAllListItems){
		this.mAllListItems = mAllListItems ;
	}
	
	public List<CamelDeviceHistory> getHistoryItemLatLngCount(){
		return mAllListItems;
	}
}

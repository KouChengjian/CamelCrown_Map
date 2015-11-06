package com.camel.redpenguin.bean;


/**
 * @ClassName: OfflineItem 
 * @Description:  离线地图
 * @author kcj
 * @date
 */
public class OfflineItem {
	
	private int cityID;
	private String cityName;
	private int cityType;
	private String size;
	
	public void setCityID(int cityID){
		this.cityID = cityID;
	}
	public int getCityID(){
		return cityID;
	}
	
	public void setCityName(String cityName){
		this.cityName = cityName;
	}
	public String getCityName(){
		return cityName;
	}
	
	public void setCityType(int cityType){
		this.cityType = cityType;
	}
	public int getCityType(){
		return cityType;
	}
	
	public void setSize(String size){
		this.size = size;
	}
	public String getSize(){
		return size;
	}
}

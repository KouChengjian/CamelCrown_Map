package com.camel.redpenguin.bean;


/**
 * @ClassName: SafetyMessage 
 * @Description: 安全信息
 * @author kcj
 * @date
 */
public class SafetyMessage {
    
	private int messageType ;
	private String messageContemt;
	private String messageTime;
	
	public void setMessageType(int messageType){
		this.messageType = messageType;
	}
	public int getMessageType(){
		return messageType ;
	}
	
	public void setMessageContemt(String messageContemt){
		this.messageContemt = messageContemt;
	}
	public String getMessageContemt(){
		return messageContemt ;
	}
	
	public void setMessageTime(String messageTime){
		this.messageTime = messageTime;
	}
	public String getMessageTime(){
		return messageTime ;
	}
}

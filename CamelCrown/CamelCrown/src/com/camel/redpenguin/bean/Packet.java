package com.camel.redpenguin.bean;

import java.io.UnsupportedEncodingException;

import com.camel.redpenguin.util.AtomicIntegerUtil;

/**
 * 
 * @author Administrator
 * 
 */
public class Packet {

	private int id = AtomicIntegerUtil.getIncrementID();
	private byte[] data;

	public int getId() {
		return id;
	}

	public void pack(String txt) {
		try {
			data = txt.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public byte[] getPacket() {
		return data;
	}
}

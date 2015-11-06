package com.camel.redpenguin.util;

import java.io.UnsupportedEncodingException;


/**
 * @ClassName: CRC16M
 * @Description: CRC 1024
 * @author kcj
 * @date 
 */
public class CRC16M {

	public static int CRC_XModem(byte[] bytes){  
		int crc = 0x0000;          // initial value  
	    int polynomial = 0x1021; 
		for (byte b : bytes) { 
            for (int i = 0; i < 8; i++) { 
                boolean bit = ((b   >> (7-i) & 1) == 1); 
                boolean c15 = ((crc >> 15    & 1) == 1); 
                crc <<= 1; 
                // If coefficient of bit and remainder polynomial = 1 xor crc with polynomial 
                if (c15 ^ bit) crc ^= polynomial; 
             } 
        } 
        crc &= 0xffff; 
        return crc;   
	}  
	
	
	/** 
     * CRC-CCITT(XModem) 
     * CRC-CCITT(0xFFFF)   
     * CRC-CCITT(0x1D0F)  
     * 校验模式 
     * @param flag< XModem(flag=1) 0xFFFF(flag=2) 0x1D0F(flag=3)> 
     * @param str 
     * @return 
     */  
    public static String  CRC_CCITT( int flag,String str) {   
        int crc = 0x00;          // initial value  
        int polynomial = 0x1021;     
        byte[] bytes = null;
		try {
			bytes = str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
          
        switch(flag){  
        case 1:  
            crc=0x00;  
            break;  
        case 2:  
            crc=0xFFFF;  
            break;  
        case 3:  
            crc=0x1D0F;  
            break;  
          
        }  
        for (int index = 0 ; index< bytes.length; index++) {  
            byte b = bytes[index];  
            for (int i = 0; i < 8; i++) {  
                boolean bit = ((b   >> (7-i) & 1) == 1);  
                boolean c15 = ((crc >> 15    & 1) == 1);  
                crc <<= 1;  
                if (c15 ^ bit) crc ^= polynomial;  
             }  
        }  
        crc &= 0xffff;  
        str = Integer.toHexString(crc);  
        return str;  
          
    }  
}

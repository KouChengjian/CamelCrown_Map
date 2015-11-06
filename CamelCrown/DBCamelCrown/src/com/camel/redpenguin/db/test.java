package com.camel.redpenguin.db;

import java.io.UnsupportedEncodingException;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// String latitude = Integer.valueOf("0157EB20",16).toString();
		// System.out.println(latitude);
		// String first= latitude.substring(0, latitude.length()-6);
		// String second=latitude.substring(latitude.length()-6,
		// latitude.length());
		// System.out.println(first+"."+second);
		//System.out.println(Long.parseLong("045B3D846C",16));
		try {
			String returnStr = new String("动漫".getBytes(), "ISO-8859-1");
			System.out.println(returnStr);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String crc16(String adata) {
		// int[] crc = null,data1;
		// data1=crc;
		// String adata="01050010FF00";
		String ad = adata.substring(2, 4);
		int[] w = new int[adata.length() / 2];
		for (int i = 0; i < adata.length(); i = i + 2) {
			ad = adata.substring(i, i + 2);
			w[i / 2] = Integer.parseInt(ad, 16);
		}

		int[] data = w;
		int[] stem = new int[data.length + 2];
		int a, b, c;
		a = 0xFFFF;
		b = 0xA001;
		for (int i = 0; i < data.length; i++) {
			a ^= data[i];
			for (int j = 0; j < 8; j++) {
				c = (int) (a & 0x01);
				a >>= 1;
				if (c == 1) {
					a ^= b;
				}
				System.arraycopy(data, 0, stem, 0, data.length);
				stem[stem.length - 2] = (int) (a & 0xFF);
				stem[stem.length - 1] = (int) (a >> 8);
			}
		}
		int[] z = stem;
		StringBuffer s = new StringBuffer();
		for (int j = 0; j < z.length; j++) {
			s.append(String.format("%02x", z[j]));
		} // System.out.print(s);
		return s.toString();
	}
}

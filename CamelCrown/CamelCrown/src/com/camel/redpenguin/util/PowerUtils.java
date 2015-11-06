package com.camel.redpenguin.util;

import android.text.TextUtils;

public class PowerUtils {

	
	public static int countPercent(String power){
		if(!TextUtils.isEmpty(power)){
			int mv = Integer.valueOf(power).intValue();
			if(mv >= 4080){ // 100
				return 1;
			}else if(mv >= 3900){ 
				return 2;
			}else if(mv >= 3730){ 
				return 3;
			}else if(mv >= 3710){ 
				return 4;
			}else {           
				return 5;
			} 
		}
		return 0;
	}
	
	public static String setPower(String power){
		String mpower = null;
		if(!TextUtils.isEmpty(power)){
			int mv = Integer.valueOf(power).intValue();
			if(mv >= 4220){
				mpower = "100%";
			}else if(mv >= 4150){
				mpower = "99%";
			}else if(mv >= 4140){
				mpower = "97%";
			}else if(mv >= 4120){
				mpower = "95%";
			}else if(mv >= 4100){
				mpower = "92%";
			}else if(mv >= 4080){
				mpower = "90%";
			}else if(mv >= 4050){
				mpower = "87%";
			}else if(mv >= 4030){
				mpower = "85%";
			}else if(mv >= 3970){
				mpower = "80%";
			}else if(mv >= 3930){
				mpower = "75%";
			}else if(mv >= 3900){
				mpower = "70%";
			}else if(mv >= 3870){
				mpower = "65%";
			}else if(mv >= 3840){
				mpower = "60%";
			}else if(mv >= 3810){
				mpower = "55%";
			}else if(mv >= 3790){
				mpower = "50%";
			}else if(mv >= 3770){
				mpower = "45%";
			}else if(mv >= 3760){
				mpower = "42%";
			}else if(mv >= 3750){
				mpower = "40%";
			}else if(mv >= 3740){
				mpower = "35%";
			}else if(mv >= 3730){
				mpower = "30%";
			}else if(mv >= 3720){
				mpower = "25%";
			}else if(mv >= 3710){
				mpower = "20%";
			}else if(mv >= 3690){
				mpower = "15%";
			}else if(mv >= 3660){
				mpower = "12%";
			}else if(mv >= 3650){
				mpower = "10%";
			}else if(mv >= 3640){
				mpower = "8%";
			}else if(mv >= 3630){
				mpower = "5%";
			}else if(mv >= 3610){
				mpower = "3%";
			}else if(mv >= 3590){
				mpower = "1%";
			}
		}
		return mpower;
		
	}
}

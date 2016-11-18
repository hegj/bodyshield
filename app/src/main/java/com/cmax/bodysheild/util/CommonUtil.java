package com.cmax.bodysheild.util;

import java.text.DecimalFormat;

/**
 * 工具类
 * Created by allen on 15-11-10.
 */
public class CommonUtil {
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.00");

	public static String bytesToHexString(byte[] src){
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv).append(" ");
		}
		return stringBuilder.toString();
	}

	/**
	 * 将摄氏度转换成华氏度
	 * @param centigrade
	 * @return
	 */
	public static float centigradeToFahrenheit(float centigrade){
		float fahrenheit = centigrade*9/5+32;
		return fahrenheit;
	}

	/**
	 * 将华氏度转换成摄氏度
	 * @param fahrenheit
	 * @return
	 */
	public static float fahrenheitToCentigrade(float fahrenheit){
		float centigrade = (fahrenheit-32)*5/9;
		return centigrade;
	}

	public static String getTemperature(float centigradeVal){
		String temperatureVal = "";
		if(0 == SharedPreferencesUtil.getIntValue(Constant.KEY_UNIT,0)){
			temperatureVal = DECIMAL_FORMAT.format(centigradeVal) + "℃";
			if( 1 == centigradeVal){
				temperatureVal = "<34℃";
			}else if(-1 == centigradeVal){
				temperatureVal = ">42℃";
			}else if(0 == centigradeVal){
				temperatureVal = "--.--℃";
			}
		}else {
			float fahrenheitVal = centigradeToFahrenheit(centigradeVal);
			temperatureVal = DECIMAL_FORMAT.format(fahrenheitVal) + "℉";
			if( 1 == centigradeVal){
				temperatureVal = "<93℉";
			}else if(-1 == centigradeVal){
				temperatureVal = ">107℉";
			}else if(0 == centigradeVal){
				temperatureVal = "--.--℉";
			}
		}
		return temperatureVal;
	}
}

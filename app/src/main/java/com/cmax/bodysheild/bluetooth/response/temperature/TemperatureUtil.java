package com.cmax.bodysheild.bluetooth.response.temperature;

/**
 * 温度结果解析
 * Created by allen on 15/11/9.
 */
public class TemperatureUtil {

	public static float getTemperature(byte temp_l,byte temp_h){
		float temperature;
		int h = (temp_h & 0xff);
		int l = (temp_l & 0xff);

		if (h  >= 0xC0) {
			//temperature is overflow
			temperature = -1;
		} else if (h >= 0x80) {
			//temperature is underflow
			temperature = 1;
		} else {

			int temp1 = (h >> 4) * 10 + (h & 0x0F);
			int temp2 = (l >> 4) * 10 + (l & 0x0F);

			temperature = temp1 + (float) temp2 / 100;
		}

		return temperature;
	}

}

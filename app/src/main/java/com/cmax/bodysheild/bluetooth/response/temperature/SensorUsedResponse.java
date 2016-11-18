package com.cmax.bodysheild.bluetooth.response.temperature;

import android.content.Intent;

import com.cmax.bodysheild.bean.ble.SensorUsed;
import com.cmax.bodysheild.bluetooth.response.BLEResponse;
import com.cmax.bodysheild.util.LogUtil;

/**
 * 传感器使用命令
 * Created by allen on 15-11-9.
 */
public class SensorUsedResponse extends BLEResponse{

	private final static String TAG = SensorUsedResponse.class.getSimpleName();

	public final static String ACTION_SENSOR_USED = "com.cmax.bodysheild.bluetooth.response" +
			".temperature.ACTION_SENSOR_USED";
	public final static String EXTRA_SENSOR_USED  = "com.cmax.bodysheild.bluetooth.response" +
			".temperature.EXTRA_SENSOR_USED";

	public final static int FLAG = 0xe2;

	private static SensorUsedResponse instance = new SensorUsedResponse();

	public static SensorUsedResponse getInstance() {
		return instance;
	}


	@Override
	public int getResponseFlag() {
		return FLAG;
	}

	@Override
	public Intent analyze(byte[] data) {

		if (data.length != 3) {
			LogUtil.e(TAG, "data size is error");
			return null;
		}


		final Intent intent = new Intent(ACTION_SENSOR_USED);
		intent.putExtra(EXTRA_SENSOR_USED, new SensorUsed(data[2]& 0xff));

		return intent;
	}

	public static SensorUsed getResponseResult(Intent intent) {

		if (intent.hasExtra(EXTRA_SENSOR_USED)){
			return intent.getParcelableExtra(EXTRA_SENSOR_USED);
		}

		return null;
	}
}

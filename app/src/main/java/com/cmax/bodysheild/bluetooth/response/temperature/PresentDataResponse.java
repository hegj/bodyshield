package com.cmax.bodysheild.bluetooth.response.temperature;

import android.content.Intent;
import android.util.Log;

import com.cmax.bodysheild.bean.ble.Temperature;
import com.cmax.bodysheild.bluetooth.response.BLEResponse;
import com.cmax.bodysheild.util.LogUtil;

/**
 * 最新数据
 * Created by allen on 15-11-3.
 */
public class PresentDataResponse extends BLEResponse {

	private final static String TAG = PresentDataResponse.class.getSimpleName();

	public final static String ACTION_REALTIME_TEMPRETURE = "com.cmax.bodysheild.bluetooth.ACTION_REALTIME_TEMPRETURE";
	public final static String EXTRA_PRESENT_DATA         = "com.cmax.bodysheild.bluetooth.EXTRA_PRESENTDATA";

	public final static int FLAG = 0xe3;
	private static PresentDataResponse instance = new PresentDataResponse();

	public static PresentDataResponse getInstance() {
		return instance;
	}

	@Override
	public Intent analyze(byte[] data) {

		if (data.length != 3) {
			LogUtil.e(TAG, "data size is error");
			return null;
		}

		float temperature = TemperatureUtil.getTemperature(data[1],data[2]);

		final Intent intent = new Intent(ACTION_REALTIME_TEMPRETURE);
		Temperature temperatureBean = new Temperature(System.currentTimeMillis(),temperature,data);
		intent.putExtra(EXTRA_PRESENT_DATA, temperatureBean);

		return intent;
	}

	@Override
	public int getResponseFlag() {
		return FLAG;
	}

	public static Temperature getResponseResult(Intent intent) {

		if (intent.hasExtra(EXTRA_PRESENT_DATA)){
			return intent.getParcelableExtra(EXTRA_PRESENT_DATA);
		}

		return null;
	}
}

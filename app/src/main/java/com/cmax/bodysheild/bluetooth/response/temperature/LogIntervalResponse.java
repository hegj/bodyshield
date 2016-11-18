package com.cmax.bodysheild.bluetooth.response.temperature;

import android.content.Intent;
import android.util.Log;

import com.cmax.bodysheild.bean.ble.LogInterval;
import com.cmax.bodysheild.bluetooth.response.BLEResponse;

/**
 * 记录间隔消息体
 * Created by allen on 15/11/9.
 */
public class LogIntervalResponse extends BLEResponse{

	public final static String ACTION_LOG_INTERVAL = "com.cmax.boysheild.bluetooth.response.temperature.ACTION_LOG_INTERVAL";
	public final static String EXTRA_LOG_INTERVAL  = "com.cmax.bodysheild.bluetooth.response.temperature.EXTRA_LOG_INTERVAL";


	private static LogIntervalResponse instance = new LogIntervalResponse();

	public final static int FLAG = 0xF2;

	public static LogIntervalResponse getInstance() {
		return instance;
	}

	private LogIntervalResponse() {
	}

	@Override
	public int getResponseFlag() {
		return FLAG;
	}

	@Override
	public Intent analyze(byte[] data) {

		final Intent intent = new Intent(ACTION_LOG_INTERVAL);
		LogInterval logInterval =  new LogInterval(data[1] & 0xff);
		intent.putExtra(EXTRA_LOG_INTERVAL, logInterval);
		return intent;
	}

	public static LogInterval getResponseResult(Intent intent) {

		if (intent.hasExtra(EXTRA_LOG_INTERVAL)){
			Log.i("LogIntervalResponse","有值");
			return intent.getParcelableExtra(EXTRA_LOG_INTERVAL);
		}
		Log.i("LogIntervalResponse","没值");
		return null;
	}
}

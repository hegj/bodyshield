package com.cmax.bodysheild.bluetooth.response.temperature;

import android.content.Intent;

import com.cmax.bodysheild.bean.ble.MemoryRecord;
import com.cmax.bodysheild.bluetooth.response.BLEResponse;
import com.cmax.bodysheild.util.LogUtil;

/**
 * 历史数据消息体
 * Created by allen on 15/11/9.
 */
public class MemoryRecordResponse extends BLEResponse{

	private final static String TAG = MemoryRecordResponse.class.getSimpleName();

	public final static String ACTION_MEMORY_RECORD = "com.cmax.bodysheild.bluetooth.response.temperature.ACTION_MEMORY_RECORD";
	public final static String EXTRA_MEMORY_RECORD  = "com.cmax.bodysheild.bluetooth.response.temperature.EXTRA_MEMORY_RECORD";

	public final static int FLAG = 0xe2;

	private static MemoryRecordResponse instance = new MemoryRecordResponse();

	public static MemoryRecordResponse getInstance() {
		return instance;
	}

	@Override
	public int getResponseFlag() {
		return FLAG;
	}

	@Override
	public Intent analyze(byte[] data) {

		if (data.length != 10) {
			LogUtil.e(TAG,"data size is error");
			return null;
		}

		MemoryRecord record = new MemoryRecord();

		record.setYear(2000+ (data[7]& 0xff));
		record.setMonth(data[6]& 0xff);
		record.setDate(data[5]& 0xff);
		record.setHour(data[4]& 0xff);
		record.setMinute(data[3]& 0xff);
		record.setSecond(data[2]& 0xff);

		record.setRecordIndex(data[1]& 0xff);

		final float temperature = TemperatureUtil.getTemperature(data[8],data[9]);
		record.setTempreature(temperature);

		final Intent intent = new Intent(ACTION_MEMORY_RECORD);
		intent.putExtra(EXTRA_MEMORY_RECORD,record);

		return intent;
	}

	public static MemoryRecord getResponseResult(Intent intent) {

		if (intent.hasExtra(EXTRA_MEMORY_RECORD)){
			return intent.getParcelableExtra(EXTRA_MEMORY_RECORD);
		}

		return null;
	}
}

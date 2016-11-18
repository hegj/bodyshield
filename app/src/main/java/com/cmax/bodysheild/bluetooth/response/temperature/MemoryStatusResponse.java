package com.cmax.bodysheild.bluetooth.response.temperature;

import android.content.Intent;

import com.cmax.bodysheild.bean.ble.MemoryStatus;
import com.cmax.bodysheild.bluetooth.response.BLEResponse;
import com.cmax.bodysheild.util.LogUtil;

/**
 * 存储状态消息体
 * Created by allen on 15-11-8.
 */
public class MemoryStatusResponse extends BLEResponse {

	private final static String TAG = MemoryStatusResponse.class.getSimpleName();
	public final static String ACTION_MEMORY_STATUS = "com.cmax.bodysheild.bluetooth.ACTION_MEMORY_STATUS";
	public final static String EXTRA_MEMORY_STATUS  = "com.cmax.bodysheild.bluetooth.EXTRA_MEMORY_STATUS";

	public final static int FLAG = 0xe1;

	private static MemoryStatusResponse instance = new MemoryStatusResponse();

	public static MemoryStatusResponse getInstance() {
		return instance;
	}


	@Override
	public Intent analyze(byte[] data) {

		if (data.length != 3) {
			LogUtil.e(TAG, "data size is error");
			return null;
		}

		MemoryStatus memoryStatus = new MemoryStatus(data[1]& 0xff,data[2]& 0xff);

		final Intent intent = new Intent(ACTION_MEMORY_STATUS);
		intent.putExtra(EXTRA_MEMORY_STATUS,memoryStatus);

		return intent;
	}

	@Override
	public int getResponseFlag() {
		return FLAG;
	}

	public static MemoryStatus getResponseResult(Intent intent) {

		if (intent.hasExtra(EXTRA_MEMORY_STATUS)){
			return intent.getParcelableExtra(EXTRA_MEMORY_STATUS);
		}

		return null;
	}
}

package com.cmax.bodysheild.bluetooth;

import android.content.Intent;
import android.util.SparseArray;

import com.cmax.bodysheild.bluetooth.response.BLEResponse;
import com.cmax.bodysheild.bluetooth.response.Response;
import com.cmax.bodysheild.bluetooth.response.temperature.ACKResponse;
import com.cmax.bodysheild.bluetooth.response.temperature.ErrorResponse;
import com.cmax.bodysheild.bluetooth.response.temperature.LogIntervalResponse;
import com.cmax.bodysheild.bluetooth.response.temperature.MemoryRecordResponse;
import com.cmax.bodysheild.bluetooth.response.temperature.MemoryStatusResponse;
import com.cmax.bodysheild.bluetooth.response.temperature.PresentDataResponse;

/**
 * 蓝牙结果分析器
 * Created by allen on 15-11-4.
 */
public class ResponseAnalyzer {

	private final static SparseArray<BLEResponse> TEMPERATURE_RESPONSES = new SparseArray<BLEResponse>();

	static {

		TEMPERATURE_RESPONSES.put(ErrorResponse.FLAG,
				ErrorResponse.getInstance());

		TEMPERATURE_RESPONSES.put(ACKResponse.FLAG,
				ACKResponse.getInstance());

		TEMPERATURE_RESPONSES.put(PresentDataResponse.FLAG,
				PresentDataResponse.getInstance());

		TEMPERATURE_RESPONSES.put(MemoryStatusResponse.FLAG,
				MemoryStatusResponse.getInstance());

		TEMPERATURE_RESPONSES.put(MemoryRecordResponse.FLAG,
				MemoryRecordResponse.getInstance());

		TEMPERATURE_RESPONSES.put(LogIntervalResponse.FLAG,
				LogIntervalResponse.getInstance());
	}

	public static Intent analyze(byte[] data, DeviceType deviceType) {

		int      header   = (data[0] & 0xFF);
		Response response = TEMPERATURE_RESPONSES.get(header);


		Intent intent = null;
		if (response != null) {
			intent = response.analyze(data);
		}

		return intent;
	}
}

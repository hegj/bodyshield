package com.cmax.bodysheild.bluetooth.response.temperature;

import android.content.Intent;

import com.cmax.bodysheild.bluetooth.response.BLEResponse;

/**
 * 错误消息体
 * Created by allen on 15-11-8.
 */
public class ErrorResponse extends BLEResponse {

	public final static String ACTION_ERROR = "com.cmax.bodysheild.bluetooth.response.temperature.ErrorResponse.ACTION_ERROR";
	public final static int    FLAG         = 0x90;

	private static ErrorResponse instance = new ErrorResponse();

	public static ErrorResponse getInstance() {
		return instance;
	}

	@Override
	public Intent analyze(byte[] data) {
		return new Intent(ACTION_ERROR);
	}

	@Override
	public int getResponseFlag() {
		return FLAG;
	}
}

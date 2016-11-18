package com.cmax.bodysheild.bluetooth.response.temperature;

import android.content.Intent;

import com.cmax.bodysheild.bluetooth.response.BLEResponse;

/**
 * 确认回复码
 * Created by allen on 15-11-8.
 */
public class ACKResponse extends BLEResponse {

	public final static String ACTION_ACK      = "com.cmax.bodysheild.bluetooth.response.ACKResponse.ACTION_ACK";
	public final static int FLAG = 0x91;


	private static ACKResponse instance = new ACKResponse();

	public static ACKResponse getInstance() {
		return instance;
	}


	@Override
	public Intent analyze(byte[] data) {
		return new Intent(ACTION_ACK);
	}

	@Override
	public int getResponseFlag() {
		return FLAG;
	}
}

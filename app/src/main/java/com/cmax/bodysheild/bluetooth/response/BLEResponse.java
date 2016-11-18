package com.cmax.bodysheild.bluetooth.response;

/**
 * 蓝牙返回消息体
 * Created by allen on 15-11-8.
 */
public abstract class BLEResponse implements Response{
	abstract public int getResponseFlag();
}

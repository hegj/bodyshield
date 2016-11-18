package com.cmax.bodysheild.bluetooth.response;

import android.content.Intent;

/**
 * 结果分析接口
 * Created by allen on 15-11-4.
 */
public interface Response {

	/**
	 * 解析蓝牙返回结果并摆入Intent对象返回
	 * @param data 蓝牙数据
	 * @return Intent对象
	 */
	 Intent analyze(byte[] data);
}

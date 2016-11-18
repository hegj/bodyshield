/**
 * 
 */
package com.cmax.bodysheild.util;

import org.apache.commons.lang3.StringUtils;

/**
 * BLE 工具类
 * @author Allen
 * @date 2015年11月25日 下午6:56:12 
 */
public class BLEUtil {


	private final static String ZERO = "0";
	private final static String SEQ = ":";

	/**
	 * 根据地址转化为序列号
	 * @param address 蓝牙地址
	 * @return 序列号
	 */
	public static String toIndex(String address) {

		String[] temp = address.split(SEQ);

		if (temp.length != 6) {
			return StringUtils.EMPTY;
		}

		String seq = temp[4] + temp[5];
		seq = String.valueOf(Integer.parseInt(seq, 16) + 1);
		
		while (seq.length() < 5) {
			seq = ZERO + seq;
		}
		
		return seq;
	}

}

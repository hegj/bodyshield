package com.cmax.bodysheild.bluetooth.command.temperature;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 更新设备时间值命令
 * Created by allen on 15-11-3.
 */
public class TimeDataCommand extends TemperatureCommand {

	public static final byte FLAG = 0x10;

	private static final DateFormat YEAR_FORMAT =  new SimpleDateFormat("yy");

	public TimeDataCommand(String address){
		super(address);
	}

	private byte[] getCurrentTime(){
		Calendar cal = Calendar.getInstance();
		String yearLast = YEAR_FORMAT.format(cal.getTime());
		// 当前年
		int year;
		try {
			year = Integer.parseInt(yearLast);
		}catch (Exception e){
			year = cal.get(Calendar.YEAR)-2000;
		}

		// 当前月
		int month = (cal.get(Calendar.MONTH)) + 1;

		// 当前月的第几天：即当前日
		int day_of_month = cal.get(Calendar.DAY_OF_MONTH);

		// 当前时：HOUR_OF_DAY-24小时制；HOUR-12小时制
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		// 当前分
		int minute = cal.get(Calendar.MINUTE);
		// 当前秒
		int second = cal.get(Calendar.SECOND);

		byte[] temp = new byte[7];

		temp[0] = FLAG;
		temp[1] = (byte) second;
		temp[2] = (byte) minute;
		temp[3] = (byte) hour;
		temp[4] = (byte) day_of_month;
		temp[5] = (byte) month;
		temp[6] = (byte) year;

		return  temp;
	}

	@Override
	public byte[] getCommand() {
		return getCurrentTime();
	}

	@Override
	public byte getCommandFlag() {
		return  FLAG;
	}
}

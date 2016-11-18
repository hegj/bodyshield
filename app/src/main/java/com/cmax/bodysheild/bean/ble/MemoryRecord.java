package com.cmax.bodysheild.bean.ble;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 数据记录
 * Created by allen on 15/11/9.
 */
public class MemoryRecord implements Parcelable{

	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("00");
	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

	/**
	 * 范围1～160
	 */
	int recordIndex;
	/**
	 * 范围0～60
	 */
	int second;
	/**
	 * 范围0～60
	 */
	int minute;
	/**
	 * 范围00～23
	 */
	int hour;
	/**
	 * 范围1～31
	 */
	int date;
	/**
	 * 范围1～12
	 */
	int month;
	/**
	 * 范围2000～2099
	 */
	int year;

	float tempreature;

	public MemoryRecord() {
	}

	protected MemoryRecord(Parcel in) {
		recordIndex = in.readInt();
		second = in.readInt();
		minute = in.readInt();
		hour = in.readInt();
		date = in.readInt();
		month = in.readInt();
		year = in.readInt();
		tempreature = in.readFloat();
	}

	public static final Creator<MemoryRecord> CREATOR = new Creator<MemoryRecord>() {
		@Override
		public MemoryRecord createFromParcel(Parcel in) {
			return new MemoryRecord(in);
		}

		@Override
		public MemoryRecord[] newArray(int size) {
			return new MemoryRecord[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(recordIndex);
		dest.writeInt(second);
		dest.writeInt(minute);
		dest.writeInt(hour);
		dest.writeInt(date);
		dest.writeInt(month);
		dest.writeInt(year);
		dest.writeFloat(tempreature);
	}

	public int getRecordIndex() {
		return recordIndex;
	}

	public void setRecordIndex(int recordIndex) {
		this.recordIndex = recordIndex;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getDate() {
		return date;
	}

	public void setDate(int date) {
		this.date = date;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public float getTempreature() {
		return tempreature;
	}

	public void setTempreature(float tempreature) {
		this.tempreature = tempreature;
	}

	public long getTimestamp(){

		String timeStr = ""+year+DECIMAL_FORMAT.format(month)+DECIMAL_FORMAT.format(date)+DECIMAL_FORMAT.format(hour)+DECIMAL_FORMAT.format(minute)+DECIMAL_FORMAT.format(second);
		long result = 0L;
		try {
			Date date = SIMPLE_DATE_FORMAT.parse(timeStr);
			result = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String toString() {
		return "MemoryRecord{" +
				"recordIndex=" + recordIndex +
				", second=" + second +
				", minute=" + minute +
				", hour=" + hour +
				", date=" + date +
				", month=" + month +
				", year=" + year +
				", tempreature=" + tempreature +
				'}';
	}
}

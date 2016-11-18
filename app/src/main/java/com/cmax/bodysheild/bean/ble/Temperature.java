package com.cmax.bodysheild.bean.ble;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by allen on 15-10-31.
 * 温度
 */
public class Temperature implements Parcelable {

	/**
	 * 时间戳
	 */
	long timestamp;

	/**
	 * 温度值
	 */
	float value;

	/**
	 * 原始数据
	 */
	byte[] bleData;

	public Temperature(long timestamp, float value, byte[] bleData) {
		this.timestamp = timestamp;
		this.value = value;
		this.bleData = bleData;
	}

	protected Temperature(Parcel in) {
		timestamp = in.readLong();
		value = in.readFloat();
		bleData = in.createByteArray();
	}

	public static final Creator<Temperature> CREATOR = new Creator<Temperature>() {
		@Override
		public Temperature createFromParcel(Parcel in) {
			return new Temperature(in);
		}

		@Override
		public Temperature[] newArray(int size) {
			return new Temperature[size];
		}
	};

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public byte[] getBleData() {
		return bleData;
	}

	public void setBleData(byte[] bleData) {
		this.bleData = bleData;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(timestamp);
		dest.writeFloat(value);
		dest.writeByteArray(bleData);
	}
}

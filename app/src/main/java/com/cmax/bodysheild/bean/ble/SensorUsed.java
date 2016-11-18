package com.cmax.bodysheild.bean.ble;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 传感器使用
 * Created by allen on 15-11-9.
 */
public class SensorUsed implements Parcelable{

	int sensorTypeUsed;

	public SensorUsed(int sensorTypeUsed) {
		this.sensorTypeUsed = sensorTypeUsed;
	}

	protected SensorUsed(Parcel in) {
		sensorTypeUsed = in.readInt();
	}

	public static final Creator<SensorUsed> CREATOR = new Creator<SensorUsed>() {
		@Override
		public SensorUsed createFromParcel(Parcel in) {
			return new SensorUsed(in);
		}

		@Override
		public SensorUsed[] newArray(int size) {
			return new SensorUsed[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(sensorTypeUsed);
	}

	public int getSensorTypeUsed() {
		return sensorTypeUsed;
	}

	public void setSensorTypeUsed(int sensorTypeUsed) {
		this.sensorTypeUsed = sensorTypeUsed;
	}
}

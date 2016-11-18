package com.cmax.bodysheild.bean.ble;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 存储状态
 * Created by allen on 15-11-8.
 */
public class MemoryStatus implements Parcelable{
	/**
	 * 温度记录数
	 */
	int recordsCount;
	/**
	 * 电池百分比
	 */
	int batteryLevel;

	public MemoryStatus(int recordsCount, int batteryLevel) {
		this.recordsCount = recordsCount;
		this.batteryLevel = batteryLevel;
	}

	protected MemoryStatus(Parcel in) {
		recordsCount = in.readInt();
		batteryLevel = in.readInt();
	}

	public static final Creator<MemoryStatus> CREATOR = new Creator<MemoryStatus>() {
		@Override
		public MemoryStatus createFromParcel(Parcel in) {
			return new MemoryStatus(in);
		}

		@Override
		public MemoryStatus[] newArray(int size) {
			return new MemoryStatus[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(recordsCount);
		dest.writeInt(batteryLevel);
	}

	public int getRecordsCount() {
		return recordsCount;
	}

	public void setRecordsCount(int recordsCount) {
		this.recordsCount = recordsCount;
	}

	public int getBatteryLevel() {
		return batteryLevel;
	}

	public void setBatteryLevel(int batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	@Override
	public String toString() {
		return "MemoryStatus{" +
				"recordsCount=" + recordsCount +
				", batteryLevel=" + batteryLevel +
				'}';
	}
}

package com.cmax.bodysheild.bean.ble;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 记录间隔
 * Created by allen on 15/11/9.
 */
public class LogInterval implements Parcelable{
	int interval;

	protected LogInterval(Parcel in) {
		interval = in.readInt();
	}

	public static final Creator<LogInterval> CREATOR = new Creator<LogInterval>() {
		@Override
		public LogInterval createFromParcel(Parcel in) {
			return new LogInterval(in);
		}

		@Override
		public LogInterval[] newArray(int size) {
			return new LogInterval[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(interval);
	}

	public LogInterval(int interval) {
		this.interval = interval;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	@Override
	public String toString() {
		return "LogInterval{" +
				"interval=" + interval +
				'}';
	}
}

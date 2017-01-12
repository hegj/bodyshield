package com.cmax.bodysheild.bean.ble;

import android.os.Parcel;
import android.os.Parcelable;

import com.cmax.bodysheild.bluetooth.DeviceType;
import com.cmax.bodysheild.util.BLEUtil;
import com.cmax.bodysheild.util.Constant;

/**
 * 蓝牙设备封装
 * Created by allen on 15-10-31.
 */
public class BLEDevice implements Parcelable{
	String address;
	String name;
	String userName;
	DeviceType deviceType;
	float deviceValue;
	boolean connectionState;


	public BLEDevice(){}

	protected BLEDevice(Parcel in) {
		address = in.readString();
		name = in.readString();
		userName = in.readString();
		deviceType = DeviceType.values()[in.readInt()];
		deviceValue = in.readFloat();
		connectionState = in.readByte() != 0;

	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public float getDeviceValue() {
		return deviceValue;
	}

	public void setDeviceValue(float deviceValue) {
		this.deviceValue = deviceValue;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		if (this.deviceType == DeviceType.Tempreature || this.deviceType == null){
			if (name.contains(BLEUtil.toIndex(this.address))){
				return name;
			}
			return name+ "-"+BLEUtil.toIndex(this.address);
		}else if (this.deviceType == DeviceType.UV){
			if (name.contains(BLEUtil.toIndex(this.address))){
				return name;
			}
			return name + "-"+BLEUtil.toIndex(this.address);
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isConnectionState() {
		return connectionState;
	}

	public void setConnectionState(boolean connectionState) {
		this.connectionState = connectionState;
	}

	public static final Creator<BLEDevice> CREATOR = new Creator<BLEDevice>() {
		@Override
		public BLEDevice createFromParcel(Parcel in) {
			return new BLEDevice(in);
		}

		@Override
		public BLEDevice[] newArray(int size) {
			return new BLEDevice[size];
		}
	};

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BLEDevice bleDevice = (BLEDevice) o;

		return !(address != null ? !address.equals(bleDevice.address) : bleDevice.address != null);

	}

	@Override
	public int hashCode() {
		return address != null ? address.hashCode() : 0;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(address);
		dest.writeString(name);
		dest.writeString(userName);
		if (deviceType != null){
			dest.writeInt(deviceType.ordinal());
		}else{
			dest.writeInt(DeviceType.Tempreature.ordinal());
		}

		dest.writeFloat(deviceValue);
		dest.writeByte((byte) (connectionState ? 1 : 0));

	}

	@Override
	public String toString() {
		return "BLEDevice{" +
				"address='" + address + '\'' +
				", name='" + name + '\'' +
				", userName='" + userName + '\'' +
				", deviceType=" + deviceType +
				", deviceValue=" + deviceValue +
				", connectionState=" + connectionState +
				'}';
	}
}

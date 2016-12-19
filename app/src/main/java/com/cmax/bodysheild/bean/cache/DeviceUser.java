package com.cmax.bodysheild.bean.cache;

import com.cmax.bodysheild.bluetooth.DeviceType;

/**
 * 设备和用户映射的缓存
 * Created by allen on 15-11-10.
 */
public class DeviceUser {

	String     address;
	String name;
	String     userId;
	DeviceType deviceType;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DeviceUser that = (DeviceUser) o;

		return address.equals(that.address);

	}

	@Override
	public int hashCode() {
		return address.hashCode();
	}

	@Override
	public String toString() {
		return "DeviceUser{" +
				"address='" + address + '\'' +
				", userId='" + userId + '\'' +
				", deviceType=" + deviceType +
				'}';
	}
}

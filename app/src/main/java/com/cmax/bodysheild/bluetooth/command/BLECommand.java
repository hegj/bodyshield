package com.cmax.bodysheild.bluetooth.command;

import com.cmax.bodysheild.bluetooth.DeviceType;

/**
 * BLE命令
 * Created by allen on 15-11-3.
 */
public abstract class BLECommand implements Command{

	String     address;
	DeviceType deviceType;

	public BLECommand(String address, DeviceType deviceType) {
		this.address = address;
		this.deviceType = deviceType;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	/**
	 * 获取命令标识
	 * @return 命令标识
	 */
	abstract public byte getCommandFlag();
}

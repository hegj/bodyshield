package com.cmax.bodysheild.bluetooth.command.temperature;

import com.cmax.bodysheild.bluetooth.command.BLECommand;
import com.cmax.bodysheild.bluetooth.DeviceType;

/**
 * 温度计命令的基类
 * Created by allen on 15-11-5.
 */
public abstract class TemperatureCommand extends BLECommand{
	public TemperatureCommand(String address) {
		super(address, DeviceType.Tempreature);
	}
}

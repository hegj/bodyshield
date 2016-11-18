package com.cmax.bodysheild.bluetooth.command.temperature;

/**
 * 设备地址设置命令
 * Created by allen on 15-11-7.
 */
public class DeviceAddressSettingCommand extends TemperatureCommand {

	public static final byte FLAG = 0x73;

	private byte[] command = new byte[2];

	public DeviceAddressSettingCommand(String address) {
		super(address);

		command[0]=FLAG;
	}

	@Override
	public byte[] getCommand() {
		return command;
	}

	@Override
	public byte getCommandFlag() {
		return  FLAG;
	}
}

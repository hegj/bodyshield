package com.cmax.bodysheild.bluetooth.command.temperature;

/**
 * 清除所有记录
 * Created by allen on 15-11-5.
 */
public class ClearMemoryCommand extends TemperatureCommand {

	public static final byte FLAG = 0x11;

	public ClearMemoryCommand(String address){
		super(address);
	}

	@Override
	public byte[] getCommand() {
		return new byte[]{FLAG};
	}

	@Override
	public byte getCommandFlag() {
		return FLAG;
	}
}

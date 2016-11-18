package com.cmax.bodysheild.bluetooth.command.temperature;

/**
 * 持续发送温度数据命令
 * Created by allen on 15-11-3.
 */
public class ContinuousDataCommand extends TemperatureCommand {

	public static final byte FLAG = 0x22;

	byte[] command = new byte[2];

	public enum ReqeustType{
		/**
		 * 停止发送
		 */
		StopModel(0x00),
		/**
		 * 开始发送
		 */
		StartModel(0x01);

		private byte data;

		ReqeustType(int i) {
			data = (byte) i;
		}
	}

	public ContinuousDataCommand(String address, ReqeustType reqeustType) {
		super(address);
		command[0] = FLAG;
		command[1] = reqeustType.data;
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
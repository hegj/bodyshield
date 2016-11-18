package com.cmax.bodysheild.bluetooth.command.temperature;

/**
 * 当前温度单片机计数的数据请求命令
 * Created by allen on 15-11-7.
 */
public class CurrentTemperatureMCUCountCommand extends TemperatureCommand {

	public static final byte FLAG = 0x72;

	private byte[] command = new byte[2];

	public enum ReqeustType{
		/**
		 * 停止读取模式
		 */
		StopModel(0x00),
		/**
		 * 开始模式
		 */
		StartModel(0x01);

		private byte data;

		ReqeustType(int i) {
			data = (byte) i;
		}
	}

	public CurrentTemperatureMCUCountCommand(String address,ReqeustType reqeustType) {
		super(address);

		command[0]=FLAG;
		command[1]=reqeustType.data;
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

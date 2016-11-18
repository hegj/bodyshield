package com.cmax.bodysheild.bluetooth.command.temperature;

import com.cmax.bodysheild.exception.IllegalCommandException;

/**
 * 设备记录的间隔命令,默认为读取间隔模式
 * Created by allen on 15-11-5.
 */
public class LogIntervalCommand extends TemperatureCommand {

	public static final byte FLAG = 0x12;

	private byte[] command = new byte[3];

	public enum ReqeustType{
		/**
		 * 停止发送
		 */
		WriteModel(0x01),
		/**
		 * 读取设备的间隔设置
		 */
		ReadModel(0x00);

		private byte data;

		ReqeustType(int i) {
			data = (byte) i;
		}
	}


	public LogIntervalCommand(String address,ReqeustType reqeustType){
		super(address);

		command[0] = FLAG;
		command[1] = reqeustType.data;
		command[2] = 0x01;
	}

	/**
	 * 设置设备的记录间隔
	 * @param interval 间隔(1~240)  间隔步长为30s
	 * @return
	 */
	public void writeInterval(int interval) {

		if (interval < 1 || interval > 0xf0) {
			throw new IllegalCommandException("interval must be between 1 and 240");
		}

		command[2] = (byte) interval;
	}

	@Override
	public byte[] getCommand() {
		if (command[1] == ReqeustType.WriteModel.data && command[2] == 0x00) {
			throw new IllegalCommandException("interval must be between 1 and  240 in WriteModel");
		}
		return command;
	}

	@Override
	public byte getCommandFlag() {
		return  FLAG;
	}
}

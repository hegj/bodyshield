package com.cmax.bodysheild.bluetooth.command.temperature;

import com.cmax.bodysheild.exception.IllegalCommandException;

/**
 * 传感器使用命令
 * Created by allen on 15-11-7.
 */
public class SensorUsedCommand extends TemperatureCommand {

	public static final byte FLAG = 0x71;

	private byte[] command = new byte[3];

	public enum ReqeustType{
		/**
		 * 读取模式
		 */
		ReadModel(0x00),
		/**
		 * 写入模式
		 */
		WriteModel(0x01);

		private byte data;

		ReqeustType(int i) {
			data = (byte) i;
		}
	}

	public SensorUsedCommand(String address,ReqeustType reqeustType) {
		super(address);
		command[0]=FLAG;
		command[1]=reqeustType.data;
		command[2]=0x01;
	}

	/**
	 * 设置传感器类型
	 * @param sensorType 传感器类型 范围（1～21）
	 */
	public void setSensorType(int sensorType) {
		if (sensorType > 21 || sensorType < 1) {
			throw new IllegalCommandException("index value must be between 1 and 21");
		}

		command[2] = (byte)sensorType;
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

package com.cmax.bodysheild.bluetooth.command.temperature;


import com.cmax.bodysheild.exception.IllegalCommandException;

/**
 * 请求数据命令
 * Created by allen on 15-11-5.
 */
public class DataRequestCommand extends TemperatureCommand {

	public static final byte FLAG = 0x21;

	private byte[] command = new byte[3];

	public enum ReqeustType{
		/**
		 * 内存状态
		 */
		MemoryStatus(0x00),
		/**
		 * 实时数据
		 */
		PresentData(0x01),
		/**
		 * 历史记录
		 */
		RecordWithIndex(0x02);

		private byte data;

		ReqeustType(int i) {
			data = (byte) i;
		}
	}

	public DataRequestCommand(String address, ReqeustType requestType) {
		super(address);
		command[0] = FLAG;
		command[1] = requestType.data;
		command[2] = 0x01;
	}

	/**
	 * 设置获取的记录的索引值
	 * @param index 记录索引值 范围（1～160）
	 */
	public void setRecordIndex(int index) {
		if (index > 160 || index < 1) {
			throw new IllegalCommandException("index value must be between 1 and 160");
		}

		command[2] = (byte)index;
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

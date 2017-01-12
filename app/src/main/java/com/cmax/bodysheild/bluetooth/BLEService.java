package com.cmax.bodysheild.bluetooth;

import com.cmax.bodysheild.bluetooth.command.BLECommand;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 蓝牙Service
 * Created by allen on 15-11-4.
 */
public interface BLEService {

	/**
	 * 执行蓝牙命令
	 * @param command 命令对象
	 */
	void executeCommand(BLECommand command);

	/**
	 * 连接指定设备
	 * @param address 设备地址
	 * @param deviceType 设备类型
	 * @return 是否连接成功
	 */
	boolean connect(final String address,DeviceType deviceType);

	/**
	 * 断开指定设备连接
	 * @param address 设备地址
	 */
	void disconnect(final String address);

	/**
	 * 扫描设备
	 * @param enable 开启/关闭 扫描
	 */
	void scanLeDevice(final boolean enable);

	/**
	 * 获取已连接的设备列表
	 * @return
	 */
	Map<String,Float> getConnectedDevicesValue();


}

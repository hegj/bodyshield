package com.cmax.bodysheild.exception;

import android.content.Context;

import com.cmax.bodysheild.bluetooth.BluetoothManage;
import com.cmax.bodysheild.bluetooth.BluetoothService;
import com.cmax.bodysheild.util.LogUtil;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序
 * Created by allen on 15-11-29.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler{

	public static final String TAG = "com.cmax.bodysheild.exception.CrashHandler";
	// 需求是 整个应用程序 只有一个 MyCrash-Handler
	private static CrashHandler INSTANCE ;
	//系统默认的UncaughtException处理类
	private Thread.UncaughtExceptionHandler mDefaultHandler;

	private Context context;

	//1.私有化构造方法
	private CrashHandler(){}

	public static synchronized CrashHandler getInstance(){
		if (INSTANCE == null)
			INSTANCE = new CrashHandler();
		return INSTANCE;
	}

	public void init(Context context){
		this.context = context;
		//获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		//设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}


	public void uncaughtException(Thread thread, Throwable ex) {

		BluetoothManage.disconnectAllDevices();

		if (mDefaultHandler != null) {
			//如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				LogUtil.e(TAG, e.getMessage());
			}
			//退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

}

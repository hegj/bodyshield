package com.cmax.bodysheild.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.cmax.bodysheild.AppContext;
import com.cmax.bodysheild.bean.HistoryData;
import com.cmax.bodysheild.bean.ble.Temperature;
import com.cmax.bodysheild.bean.cache.DeviceUser;
import com.cmax.bodysheild.bluetooth.command.BLECommand;
import com.cmax.bodysheild.bluetooth.command.temperature.ContinuousDataCommand;
import com.cmax.bodysheild.bluetooth.response.temperature.ACKResponse;
import com.cmax.bodysheild.bluetooth.response.temperature.PresentDataResponse;
import com.cmax.bodysheild.dao.DBManager;
import com.cmax.bodysheild.enums.AppModel;
import com.cmax.bodysheild.util.CommonUtil;
import com.cmax.bodysheild.util.Constant;
import com.cmax.bodysheild.util.LogUtil;
import com.cmax.bodysheild.util.SharedPreferencesUtil;
import com.orhanobut.logger.Logger;

import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

/**
 * BLE服务
 * Created by allen on 15-10-26.
 */
public class BluetoothService extends Service implements BLEService {
    private final IBinder mBinder = new LocalBinder();
    public final static String EXTRA_DEVICE = "com.cmax.bodysheild.bluetooth.EXTRA_DEVICE";
    public final static String EXTRA_ADDRESS = "com.cmax.bodysheild.bluetooth.EXTRA_ADDRESS";
    private static final Object writeLock = new Object();
    private Handler handler;
    private BluetoothManage myBluetoothManage;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d("BleService created");
        registerReceiver(notificationReceiver, makeIntentFilter());
        handler = new Handler();
        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        myBluetoothManage = BluetoothManage.getInstance();
        myBluetoothManage.init(this);
        //模拟数据
        if (AppContext.appModel == AppModel.Debug) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    testData();
                    handler.postDelayed(this, 1000);
                }
            }, 1000);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myBluetoothManage.disconnectAllDevices();
        unregisterReceiver(notificationReceiver);
        myBluetoothManage.closeDBManage();
    }

    public class LocalBinder extends Binder {
        /**
         * 获取Bluetooth服务
         *
         * @return BluetoothService
         */
        public BluetoothService getService() {
            return BluetoothService.this;
        }

        /**
         * 获取BLE服务主要接口功能
         *
         * @return BLEService
         */
        public BLEService getBLEService() {
            return BluetoothService.this;
        }
    }
    @Override
    public void executeCommand(BLECommand command) {
        synchronized (writeLock) {
            boolean isSuccess =false;
            for (int i = 0; i < 4; i++) {
                isSuccess= myBluetoothManage.writeCharacteristic(command.getAddress(), command.getCommand());
                if (isSuccess) {
                    break;
                } else {
                    try {
                        Thread.currentThread().sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (!isSuccess) {
                //失败
            }
        }
    }
    @Override
    public boolean connect(final String address, final DeviceType deviceType) {
        return myBluetoothManage.connect(address, deviceType);
    }

    @Override
    public void disconnect(final String address) {
        myBluetoothManage.disconnect(address);
    }

    @Override
    public void scanLeDevice(final boolean enable) {
        myBluetoothManage.scanLeDevice(enable);
    }

    @Override
    public Map<String, Float> getConnectedDevicesValue() {
        return myBluetoothManage.getConnectedDevicesValue();
    }
    private void testData() {
        final Intent intent = new Intent(PresentDataResponse.ACTION_REALTIME_TEMPRETURE);
        Random random = new Random();
        int s = random.nextInt(37) % (37 - 36 + 1) + 36;
        float tempre = random.nextFloat() + s;
        Temperature temperatureBean = new Temperature(System.currentTimeMillis(), tempre, new byte[]{});
        intent.putExtra(EXTRA_DEVICE, temperatureBean);
        intent.putExtra(PresentDataResponse.EXTRA_PRESENT_DATA, temperatureBean);
        intent.putExtra(EXTRA_ADDRESS, Constant.TEST_DEVICE_NAME_TEMPERATURE);
        sendBroadcast(intent);
    }

    private final BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Map<String, BluetoothManage.DeviceHolder> deviceHolders = myBluetoothManage.getDeviceHolders();
            if (ACKResponse.ACTION_ACK.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(EXTRA_DEVICE);

                BluetoothManage.DeviceHolder holder = deviceHolders.get(device.getAddress());
                if (holder != null && 0x10 == holder.lastCommand) {
                    ContinuousDataCommand command = new ContinuousDataCommand(device.getAddress(),
                            ContinuousDataCommand.ReqeustType.StartModel);
                    executeCommand(command);
                }
            } else if (PresentDataResponse.ACTION_REALTIME_TEMPRETURE.equals(action)) {
                Temperature temperature = PresentDataResponse.getResponseResult(intent);
                BluetoothDevice device = intent.getParcelableExtra(EXTRA_DEVICE);
                BluetoothManage.DeviceHolder holder = deviceHolders.get(device.getAddress());
                if (holder != null) {
                    holder.lastValue = temperature.getValue();
                }
            } else if (Constant.EXIT.equals(action)) {
                myBluetoothManage.disconnectAllDevices();
                stopSelf();
                Logger.e("get exit");
            }
        }
    };
    private static IntentFilter makeIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACKResponse.ACTION_ACK);
        intentFilter.addAction(PresentDataResponse.ACTION_REALTIME_TEMPRETURE);
        intentFilter.addAction(Constant.EXIT);
        return intentFilter;
    }


}

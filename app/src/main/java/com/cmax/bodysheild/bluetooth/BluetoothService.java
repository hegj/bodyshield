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
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.cmax.bodysheild.AppContext;
import com.cmax.bodysheild.bean.HistoryData;
import com.cmax.bodysheild.bean.ble.Temperature;
import com.cmax.bodysheild.bean.cache.DeviceUser;
import com.cmax.bodysheild.bean.cache.User;
import com.cmax.bodysheild.bluetooth.command.BLECommand;
import com.cmax.bodysheild.bluetooth.command.temperature.ContinuousDataCommand;
import com.cmax.bodysheild.bluetooth.response.temperature.ACKResponse;
import com.cmax.bodysheild.bluetooth.response.temperature.PresentDataResponse;
import com.cmax.bodysheild.dao.DBManager;
import com.cmax.bodysheild.enums.AppModel;
import com.cmax.bodysheild.http.HttpMethods;
import com.cmax.bodysheild.http.rxschedulers.RxSchedulersHelper;
import com.cmax.bodysheild.util.CommonUtil;
import com.cmax.bodysheild.util.Constant;
import com.cmax.bodysheild.util.DataUtils;
import com.cmax.bodysheild.util.DateUtils;
import com.cmax.bodysheild.util.JsonUtil;
import com.cmax.bodysheild.util.LogUtil;
import com.cmax.bodysheild.util.PortraitUtil;
import com.cmax.bodysheild.util.SPUtils;
import com.cmax.bodysheild.util.SharedPreferencesUtil;
import com.cmax.bodysheild.util.UIUtils;
import com.orhanobut.logger.Logger;

import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import rx.Subscriber;

/**
 * BLE服务
 * Created by allen on 15-10-26.
 */
public class BluetoothService extends Service implements BLEService {

    private final static String TAG = BluetoothService.class.getSimpleName();
    private final IBinder mBinder = new LocalBinder();

    private final static Map<String, DeviceHolder> DEVICE_HOLDERS = new HashMap<String, DeviceHolder>();

    private static final String TEMPRETURE_SERVICE_UUID_STR = "00005307-0000-0041-4C50-574953450000";
    private static final UUID TEMPRETURE_SERVICE_UUID = UUID.fromString(TEMPRETURE_SERVICE_UUID_STR); //服务UUID
    public static final UUID TEMPRETURE_WRITE_UUID = UUID.fromString("00005308-0000-0041-4C50-574953450000"); //特征值(写)
    public static final UUID TEMPRETURE_READ_UUID = UUID.fromString("00005309-0000-0041-4C50-574953450000"); //特征值(读)


    public final static String ACTION_BLE_UNSUPPORT = "com.cmax.bodysheild.bluetooth.ACTION_BLE_UNSUPPORT";
    public final static String ACTION_BLUETOOTH_UNSUPPORT = "com.cmax.bodysheild.bluetooth.ACTION_BLUETOOTH_UNSUPPORT";
    public final static String ACTION_BLUETOOTH_UNABLED = "com.cmax.bodysheild.bluetooth.ACTION_BLUETOOTH_UNABLED";
    public final static String ACTION_BLE_NEW_DEVICE = "com.cmax.bodysheild.bluetooth.ACTION_BLE_NEW_DEVICE";
    public final static String ACTION_GATT_CONNECTED = "com.cmax.bodysheild.bluetooth.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.cmax.bodysheild.bluetooth.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.cmax.bodysheild.bluetooth.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_BLE_FINISH_SCANNING = "com.cmax.bodysheild.bluetooth.ACTION_BLE_FINISH_SCANNING";

    public final static String EXTRA_DEVICE = "com.cmax.bodysheild.bluetooth.EXTRA_DEVICE";
    public final static String EXTRA_ADDRESS = "com.cmax.bodysheild.bluetooth.EXTRA_ADDRESS";


    private BluetoothAdapter bluetoothAdapter;
    private static final Object scanLock = new Object();
    private static final Object writeLock = new Object();


    private static final Set<String> SCANNING_DEVICE_ADDRESS = new HashSet<String>();

    // 8秒后停止查找搜索.
    public static final long SCAN_PERIOD = 10000;
    private static boolean scanning = false;
    private Handler handler;

    private final String CONNECT_DEVICE_NAME1 = "Body Temp";
    private final String CONNECT_DEVICE_NAME2 = "THUV";
    private final String CONNECT_DEVICE_NAME3 = "AB01";
    private final String CONNECT_DEVICE_NAME4 = "AB02";
    private BluetoothManager bluetoothManager;

    private long recordTime = 0L;

    private DBManager dbManager;
    private long recordNetTime = 0L;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "BleService created");
        registerReceiver(notificationReceiver, makeIntentFilter());
        handler = new Handler();
        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            // 广播:蓝牙不支持BLE
            broadcastUpdate(ACTION_BLE_UNSUPPORT);
        }
        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // 检查设备上是否支持蓝牙
        if (bluetoothAdapter == null) {
            // 广播:设备没有蓝牙
            broadcastUpdate(ACTION_BLUETOOTH_UNSUPPORT);
        }

        dbManager = new DBManager(this);

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
        UIUtils.getMainThreadHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Logger.d("更新数据");
                updateTemperatureHistoryDataToServer();
                UIUtils.getMainThreadHandler().postDelayed(this,1000*5*60);
            }
        }, 3000);


    }

    private void updateTemperatureHistoryDataToServer() {
        if (DEVICE_HOLDERS != null && DEVICE_HOLDERS.size() != 0) {
            Set<String> addresss = DEVICE_HOLDERS.keySet();
            if (addresss != null && addresss.size() != 0) {
                Iterator<String> iterator = addresss.iterator();
                while (iterator.hasNext()) {
                    final String address = iterator.next();
                    String userId = DataUtils.getUserIdForAddress(address);
                    if (TextUtils.isEmpty(userId)) return;
                    List<Temperature> temperatureList = dbManager.getHistory(DateUtils.getFormatTime("yyyyMMdd"), address, userId);
                    List<HistoryData> historyDataList = new ArrayList<>();
                    for (int i = 0; i < temperatureList.size(); i++) {
                        Temperature temperature = temperatureList.get(i);
                        long tempertureToServerRecordTime = SPUtils.getTempertureToServerRecordTime(address);

                            // 如果是在上次更新的时间之后的数据上传到服务器
                            HistoryData historyData = new HistoryData();
                            historyData.setDeviceAddress(address);
                            historyData.setTimestamp(temperature.getTimestamp());
                            historyData.setUid(userId);
                            historyData.setValue(temperature.getValue());
                            historyDataList.add(historyData);

                    }
                    String jsonString = JsonUtil.toJsonString(historyDataList);
                    if (!TextUtils.isEmpty(jsonString)) {
                        HttpMethods.getInstance().apiService.uploadTemperature(jsonString).compose(RxSchedulersHelper.applyIoTransformer())
                                .subscribe(new Subscriber() {
                                    @Override
                                    public void onCompleted() {
                                        Logger.d(" update temperature success");
                                        recordNetTime = System.currentTimeMillis();
                                        SPUtils.setTempertureToServerRecordTime(recordNetTime, address);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Logger.d("update temperature  onError");
                                    }

                                    @Override
                                    public void onNext(Object o) {
                                        onCompleted();
                                    }
                                });

                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnectAllDevices();
        unregisterReceiver(notificationReceiver);
        if (dbManager != null) {
            dbManager.close();
            dbManager = null;
        }
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

    /**
     * 设备数据绑定类
     */
    private class DeviceHolder {
        String address;
        DeviceType deviceType;
        BluetoothGatt gatt;
        byte lastCommand;
        float lastValue;
    }

    //设备扫描回调.
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

            if (device.getName() != null) {
                if (device.getName().indexOf(CONNECT_DEVICE_NAME1) != -1
                        || device.getName().indexOf(CONNECT_DEVICE_NAME2) != -1 || device.getName().indexOf(CONNECT_DEVICE_NAME3) != -1 || device.getName().indexOf(CONNECT_DEVICE_NAME4) != -1) {
                    if (!SCANNING_DEVICE_ADDRESS.contains(device.getAddress())) {
                        // 广播发现新设备
                        SCANNING_DEVICE_ADDRESS.add(device.getAddress());

                        final Intent intent = new Intent(ACTION_BLE_NEW_DEVICE);
                        intent.putExtra(EXTRA_DEVICE, device);
                        sendBroadcast(intent);
                    }
                }
            }
        }
    };

    //设备连接成功后的回调
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if (newState == BluetoothProfile.STATE_CONNECTED) {

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    LogUtil.e(TAG, e.getMessage());
                }

                gatt.discoverServices();
                broadcastUpdate(ACTION_GATT_CONNECTED, gatt.getDevice());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                LogUtil.i(TAG, gatt.getDevice().getAddress() + " is disconnected");
                DEVICE_HOLDERS.remove(gatt.getDevice().getAddress());

                broadcastUpdate(ACTION_GATT_DISCONNECTED, gatt.getDevice());
                gatt.close();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

                for (int i = 0; i < 4; i++) {
                    if (enableNotification(gatt, TEMPRETURE_SERVICE_UUID, TEMPRETURE_READ_UUID)) {
                        break;
                    } else {
                        try {
                            Thread.currentThread().sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);

            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                doAfterCharacteristicChanged(gatt.getDevice(), characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            doAfterCharacteristicChanged(gatt.getDevice(), characteristic);
        }
    };

    /**
     * 订阅读取字段
     */
    private boolean enableNotification(BluetoothGatt gatt, UUID ServiceUUID, UUID characteristicUUID) {
        if (bluetoothAdapter == null) {
            LogUtil.w(TAG, "BluetoothAdapter not initialized");
            return false;
        }
        BluetoothGattService RxService = null;
        for (int i = 0; i < 4; i++) {
            RxService = getService(TEMPRETURE_SERVICE_UUID, gatt);

            if (RxService == null) {
                try {
                    LogUtil.i(TAG, " service not found when enableNotification! try again");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }

        if (RxService == null) {
            LogUtil.e(TAG, "RXTX service not found!");
            return false;
        }


        BluetoothGattCharacteristic readChar = null;
        for (int i = 0; i < 4; i++) {
            readChar = getCharacteristic(characteristicUUID, RxService);

            if (readChar == null) {
                try {
                    LogUtil.e(TAG, "Read charateristic not found! try again");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }

//		final BluetoothGattCharacteristic readChar = RxService.getCharacteristic(characteristicUUID);
        if (readChar == null) {
            LogUtil.e(TAG, "Read charateristic not found!");
            return false;
        }

        boolean statu = gatt.setCharacteristicNotification(readChar, true);

        LogUtil.i(TAG, "setCharacteristicNotification:" + statu);

        //解决订阅后没有收到通知的问题
        List<BluetoothGattDescriptor> descriptors = readChar.getDescriptors();
        for (BluetoothGattDescriptor dp : descriptors) {
            dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(dp);
        }
        return statu;
    }

    @Override
    public void executeCommand(BLECommand command) {

        synchronized (writeLock) {
            for (int i = 0; i < 4; i++) {
                if (writeCharacteristic(command.getAddress(), command.getCommand())) {
                    break;
                } else {
                    try {
                        Thread.currentThread().sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 写入特性值
     *
     * @param address 设备地址
     * @param value   命令值
     */

    private boolean writeCharacteristic(String address, byte[] value) {

        DeviceHolder holder = DEVICE_HOLDERS.get(address);
        if (holder != null && holder.gatt != null) {
            BluetoothGattService RxService = null;

            for (int i = 0; i < 3; i++) {
                RxService = getService(TEMPRETURE_SERVICE_UUID, holder.gatt);

                if (RxService == null) {
                    try {
                        LogUtil.i(TAG, " service not found when writeCharacteristic! try again");
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }

            if (RxService == null) {
                LogUtil.e(TAG, " service not found when writeCharacteristic!");
                return false;
            }


            BluetoothGattCharacteristic characteristic = null;
            for (int i = 0; i < 3; i++) {
                characteristic = getCharacteristic(TEMPRETURE_WRITE_UUID, RxService);

                if (characteristic == null) {
                    try {
                        LogUtil.e(TAG, "write charateristic not found! try again");
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }

//			BluetoothGattCharacteristic characteristic = RxService.getCharacteristic(TEMPRETURE_WRITE_UUID);
            if (characteristic == null) {
                LogUtil.e(TAG, "write charateristic not found!");
                return false;
            }
            characteristic.setValue(value);

            boolean status = holder.gatt.writeCharacteristic(characteristic);
            if (status) {
                holder.lastCommand = value[0];
            }

            LogUtil.i(TAG, "write " + CommonUtil.bytesToHexString(value) + " - status=" + status);
            return status;
        } else {
            return false;
        }
    }

    private BluetoothGattService getService(UUID serviceUuid, BluetoothGatt gatt) {
        return gatt.getService(serviceUuid);
    }

    private BluetoothGattCharacteristic getCharacteristic(UUID characteristicUuid, BluetoothGattService service) {
        return service.getCharacteristic(characteristicUuid);
    }

    private void doAfterCharacteristicChanged(final BluetoothDevice device, BluetoothGattCharacteristic characteristic) {

        byte[] bytes = characteristic.getValue();

        int header = (bytes[0] & 0xFF);
//		LogUtil.i(TAG, "first byte 0x" + Integer.toHexString(header));

        DeviceHolder holder = DEVICE_HOLDERS.get(device.getAddress());
        Intent intent = null;

        if (holder != null) {
            intent = ResponseAnalyzer.analyze(bytes, holder.deviceType);
        }

        if (intent != null) {
            if (intent.getAction().equals(PresentDataResponse.ACTION_REALTIME_TEMPRETURE)) {
                int measureInterval = SharedPreferencesUtil.getIntValue(Constant.KEY_MEASURE_INTERVAL, 1);
                if (System.currentTimeMillis() >= (recordTime + measureInterval * 60 * 1000)) {
                    Temperature temperature = intent.getParcelableExtra(PresentDataResponse.EXTRA_PRESENT_DATA);
                    if (temperature != null && temperature.getValue() > 34) {
                        String currentUserName = "";
                        List<DeviceUser> deviceUsers = SharedPreferencesUtil.getList(Constant.DEVICE_USER_LIST, DeviceUser.class);
                        for (DeviceUser deviceuser : deviceUsers) {
                            if (deviceuser.getAddress().equalsIgnoreCase(device.getAddress())) {
                                currentUserName = deviceuser.getUserId();
                                break;
                            }
                        }
                        if (StringUtils.isNotBlank(currentUserName)) {
                            HistoryData historyData = new HistoryData();
                            historyData.setDeviceAddress(device.getAddress());
                            historyData.setTimestamp(temperature.getTimestamp());
                            historyData.setUserId(currentUserName);
                            historyData.setValue(temperature.getValue());
                            dbManager.addHistory(historyData);
                            recordTime = System.currentTimeMillis();
                        }
                    }

                }
            }

            intent.putExtra(EXTRA_DEVICE, device);
            intent.putExtra(EXTRA_ADDRESS, device.getAddress());

            sendBroadcast(intent);
        }
    }

    @Override
    public boolean connect(final String address, final DeviceType deviceType) {
        if (bluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        //尝试连接现有设备
        final DeviceHolder holder = DEVICE_HOLDERS.get(address);
        if (holder != null && holder.gatt != null) {
            try {
                if (holder.gatt.connect()) {
                    return true;
                } else {

                    holder.gatt.disconnect();

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        LogUtil.e(TAG, e.getMessage());
                    }

                    holder.gatt.close();
                }
            } catch (Exception e) {

                holder.gatt.disconnect();

                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    LogUtil.e(TAG, ex.getMessage());
                }

                holder.gatt.close();

            }
        }
        connectToDevice(address, deviceType);
        return true;
    }

    private void connectToDevice(final String address, DeviceType deviceType) {
        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        BluetoothGatt bluetoothGatt = device.connectGatt(this, false, gattCallback);

        Log.d(TAG, "Trying to create a new connection.");

        DeviceHolder _holder = new DeviceHolder();
        _holder.address = device.getAddress();
        _holder.gatt = bluetoothGatt;
        _holder.deviceType = deviceType;

        DEVICE_HOLDERS.put(address, _holder);
    }

    @Override
    public void disconnect(final String address) {

        final DeviceHolder holder = DEVICE_HOLDERS.get(address);
        if (holder == null || holder.gatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        holder.gatt.disconnect();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                holder.gatt.close();
            }
        }, 1000);
    }


    public static void disconnectAllDevices() {

        final Handler mhandler = new Handler();
        for (Map.Entry<String, DeviceHolder> entry : DEVICE_HOLDERS.entrySet()) {
            final DeviceHolder holder = entry.getValue();
            if (holder != null && holder.gatt != null) {
                try {
                    holder.gatt.disconnect();

                    mhandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.gatt.close();
                        }
                    }, 500);

                    LogUtil.i(TAG, "disconnect:" + holder.address);
                } catch (Exception e) {
                    LogUtil.e(TAG, e.getMessage());
                }

            }
        }
    }

    /**
     * 广播消息
     *
     * @param action 动作
     */
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    /**
     * 广播消息
     *
     * @param action 动作
     * @param device 设备
     */
    private void broadcastUpdate(final String action, BluetoothDevice device) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_DEVICE, device);
        sendBroadcast(intent);
    }

    @Override
    public void scanLeDevice(final boolean enable) {

        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            // 广播:设备蓝牙没开启
            broadcastUpdate(ACTION_BLUETOOTH_UNABLED);
            return;
        }

        if (enable) {
            if (!scanning) {
                synchronized (scanLock) {

                    disconnectAllDevices();

                    // Stops scanning after a pre-defined scan period.
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            scanning = false;
                            bluetoothAdapter.stopLeScan(leScanCallback);
                            sendBroadcast(new Intent(ACTION_BLE_FINISH_SCANNING));

                        }
                    }, SCAN_PERIOD);

                    scanning = true;
                    SCANNING_DEVICE_ADDRESS.clear();
                    bluetoothAdapter.startLeScan(leScanCallback);
                }
            }
        } else {
            SCANNING_DEVICE_ADDRESS.clear();
            scanning = false;
            bluetoothAdapter.stopLeScan(leScanCallback);
            sendBroadcast(new Intent(ACTION_BLE_FINISH_SCANNING));
        }
    }

    @Override
    public Map<String, Float> getConnectedDevicesValue() {
        Map<String, Float> map = new TreeMap<String, Float>();
        DeviceHolder holder;
        for (Map.Entry<String, DeviceHolder> entry : DEVICE_HOLDERS.entrySet()) {
            holder = entry.getValue();

            if (holder != null && holder.gatt != null) {
                try {
                    int connectionState = bluetoothManager.getConnectionState(
                            holder.gatt.getDevice(), BluetoothProfile.GATT);

                    if (connectionState == BluetoothProfile.STATE_CONNECTED) {
                        map.put(entry.getKey(), holder.lastValue);
                    }
                } catch (Exception e) {
                    LogUtil.e(TAG, e.getMessage());
                }

            }
        }

        return map;
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

    /**
     * android 4.3 无法通过扫描过滤含有128位UUID的Service的设备，
     * 只能在LeScanCallback函数中解析uuid过滤
     *
     * @param advertisedData 广播的数据
     * @return uuid集合
     */
    private List<UUID> parseUUIDs(final byte[] advertisedData) {
        List<UUID> uuids = new ArrayList<UUID>();

        int offset = 0;
        while (offset < (advertisedData.length - 2)) {
            int len = advertisedData[offset++];
            if (len == 0)
                break;

            int type = advertisedData[offset++];
            switch (type) {
                case 0x02: // Partial list of 16-bit UUIDs
                case 0x03: // Complete list of 16-bit UUIDs
                    while (len > 1) {
                        int uuid16 = advertisedData[offset++];
                        uuid16 += (advertisedData[offset++] << 8);
                        len -= 2;
                        uuids.add(UUID.fromString(String.format(
                                "%08x-0000-1000-8000-00805f9b34fb", uuid16)));
                    }
                    break;
                case 0x06:// Partial list of 128-bit UUIDs
                case 0x07:// Complete list of 128-bit UUIDs
                    // Loop through the advertised 128-bit UUID's.
                    while (len >= 16) {
                        try {
                            // Wrap the advertised bits and order them.
                            ByteBuffer buffer = ByteBuffer.wrap(advertisedData,
                                    offset++, 16).order(ByteOrder.LITTLE_ENDIAN);
                            long mostSignificantBit = buffer.getLong();
                            long leastSignificantBit = buffer.getLong();
                            uuids.add(new UUID(leastSignificantBit,
                                    mostSignificantBit));
                        } catch (IndexOutOfBoundsException e) {
                            // Defensive programming.
                            LogUtil.e(TAG, e.toString());
                            continue;
                        } finally {
                            // Move the offset to read the next uuid.
                            offset += 15;
                            len -= 16;
                        }
                    }
                    break;
                default:
                    offset += (len - 1);
                    break;
            }
        }
        return uuids;
    }

    private final BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (ACKResponse.ACTION_ACK.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(EXTRA_DEVICE);
                DeviceHolder holder = DEVICE_HOLDERS.get(device.getAddress());
                if (holder != null && 0x10 == holder.lastCommand) {
                    ContinuousDataCommand command = new ContinuousDataCommand(device.getAddress(),
                            ContinuousDataCommand.ReqeustType.StartModel);

                    executeCommand(command);
                }
            } else if (PresentDataResponse.ACTION_REALTIME_TEMPRETURE.equals(action)) {
                Temperature temperature = PresentDataResponse.getResponseResult(intent);

                BluetoothDevice device = intent.getParcelableExtra(EXTRA_DEVICE);
                DeviceHolder holder = DEVICE_HOLDERS.get(device.getAddress());
                if (holder != null) {
                    holder.lastValue = temperature.getValue();
                }
            } else if (Constant.EXIT.equals(action)) {
                disconnectAllDevices();
                stopSelf();
                LogUtil.i(TAG, "get exit");
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

    public Set<String> getScanningDeviceAddress() {
        return SCANNING_DEVICE_ADDRESS;
    }
}

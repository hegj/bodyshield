package com.cmax.bodysheild.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.cmax.bodysheild.bean.HistoryData;
import com.cmax.bodysheild.bean.ble.Temperature;
import com.cmax.bodysheild.bean.cache.DeviceUser;
import com.cmax.bodysheild.bluetooth.response.temperature.PresentDataResponse;
import com.cmax.bodysheild.dao.DBManager;
import com.cmax.bodysheild.http.HttpMethods;
import com.cmax.bodysheild.util.CommonUtil;
import com.cmax.bodysheild.util.Constant;
import com.cmax.bodysheild.util.LogUtil;
import com.cmax.bodysheild.util.SharedPreferencesUtil;
import com.cmax.bodysheild.util.UIUtils;
import com.orhanobut.logger.Logger;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Created by Administrator on 2016/12/26 0026.
 */

public class BluetoothManage {
    private final String CONNECT_DEVICE_NAME1 = "Body Temp";
    private final String CONNECT_DEVICE_NAME2 = "THUV";
    private final String CONNECT_DEVICE_NAME3 = "AB01";
    private final String CONNECT_DEVICE_NAME4 = "AB02";
    public final static String ACTION_BLE_UNSUPPORT            = "com.cmax.bodysheild.bluetooth.ACTION_BLE_UNSUPPORT";
    public final static String ACTION_BLUETOOTH_UNSUPPORT      = "com.cmax.bodysheild.bluetooth.ACTION_BLUETOOTH_UNSUPPORT";
    public final static String ACTION_BLUETOOTH_UNABLED        = "com.cmax.bodysheild.bluetooth.ACTION_BLUETOOTH_UNABLED";
    public final static String ACTION_BLE_NEW_DEVICE           = "com.cmax.bodysheild.bluetooth.ACTION_BLE_NEW_DEVICE";
    public final static String ACTION_GATT_CONNECTED           = "com.cmax.bodysheild.bluetooth.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED        = "com.cmax.bodysheild.bluetooth.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.cmax.bodysheild.bluetooth.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_BLE_FINISH_SCANNING      = "com.cmax.bodysheild.bluetooth.ACTION_BLE_FINISH_SCANNING";
    public final static String ACTION_BLE_NOTIFICATION_ERROR      = "com.cmax.bodysheild.bluetooth.ACTION_BLE_NOTIFICATION_ERROR";
    public static final String TEMPRETURE_SERVICE_UUID_STR = "00005307-0000-0041-4C50-574953450000";
    public static final UUID TEMPRETURE_SERVICE_UUID     = UUID.fromString(TEMPRETURE_SERVICE_UUID_STR); //服务UUID
    public static final  UUID   TEMPRETURE_WRITE_UUID       = UUID.fromString("00005308-0000-0041-4C50-574953450000"); //特征值(写)
    public static final  UUID   TEMPRETURE_READ_UUID        = UUID.fromString("00005309-0000-0041-4C50-574953450000"); //特征值(读)
    public static final java.lang.String ACTION_BLE_GET_SERVICE_ERROR = "com.cmax.bodysheild.bluetooth.ACTION_BLE_GET_SERVICE_ERROR";
    public static final java.lang.String ACTION_BLE_GET_CHARACTERISTIC_ERROR = "com.cmax.bodysheild.bluetooth.ACTION_BLE_GET_CHARACTERISTIC_ERROR";
    public static final java.lang.String ACTION_BLE_CONNECT_ERROR = "com.cmax.bodysheild.bluetooth.ACTION_BLE_CONNECT_ERROR";
    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private long recordTime = 0L;
    // 8秒后停止查找搜索.
    public static final long    SCAN_PERIOD = 10000;
    private static      boolean scanning    = false;
    private DBManager dbManager;
    private static final Object scanLock = new Object();

    /**
     * 连接成功的设备的集合
     */
    private final static Map<String, BluetoothManage.DeviceHolder> DEVICE_HOLDERS = new HashMap<String, BluetoothManage.DeviceHolder>();
    /**
     * 新的设备的集合, 用来判断当发现一个设备时,是否是新的设备,还是已经连接过的设备
     */
    private static final Set<String> SCANNING_DEVICE_ADDRESS = new HashSet<String>();
    /**
     * 初始化数据
     */
    public void init(Context context) {
        this.context =context;
        dbManager = new DBManager(context);
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            // 广播:蓝牙不支持BLE
            BluetoothManage.getInstance().broadcastUpdate(BluetoothManage.ACTION_BLE_UNSUPPORT);
        }
        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        // 检查设备上是否支持蓝牙
        if (bluetoothAdapter == null) {
            // 广播:设备没有蓝牙
            BluetoothManage.getInstance().broadcastUpdate(BluetoothManage.ACTION_BLUETOOTH_UNSUPPORT);
        }

    }
    /**
     * 设备扫描的回调
     */
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

            if (device.getName() != null){
                if (device.getName().indexOf(CONNECT_DEVICE_NAME1) != -1
                        || device.getName().indexOf(CONNECT_DEVICE_NAME2) != -1 || device.getName().indexOf(CONNECT_DEVICE_NAME3) != -1 || device.getName().indexOf(CONNECT_DEVICE_NAME4) != -1) {
                    if (!SCANNING_DEVICE_ADDRESS.contains(device.getAddress())) {
                        Logger.d("-----------------leScanCallback");
                        // 广播发现新设备
                        SCANNING_DEVICE_ADDRESS.add(device.getAddress());
                        BluetoothManage.getInstance().broadcastUpdate(BluetoothManage.ACTION_BLE_NEW_DEVICE,device);
                    }
                }
            }
        }
    };

    /**
     *  gatt协议 设备连接以及读取数据的回调
     */
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        /**
         * 连接设备改变
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Logger.d("-----------------onConnectionStateChange");
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();
                BluetoothManage.getInstance().broadcastUpdate(BluetoothManage.ACTION_GATT_CONNECTED, gatt.getDevice());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Logger.d( gatt.getDevice().getAddress() + " is disconnected");
                DEVICE_HOLDERS.remove(gatt.getDevice().getAddress());
                BluetoothManage.getInstance().broadcastUpdate(BluetoothManage.ACTION_GATT_DISCONNECTED, gatt.getDevice());
                gatt.close();
            }
        }

        /**
         * 发现设备
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Logger.d("-----------------onServicesDiscovered");
                boolean isNotification = false;
                for (int i = 0; i < 4; i++) {
                    isNotification = enableNotification(gatt, TEMPRETURE_SERVICE_UUID, TEMPRETURE_READ_UUID);
                    if (isNotification) {
                        break;
                    } else {
                        try {
                            Thread.currentThread().sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                // boolean isNotification = enableNotification(gatt, BluetoothManage.TEMPRETURE_SERVICE_UUID, BluetoothManage.TEMPRETURE_READ_UUID);
                if (isNotification){
                    BluetoothManage.getInstance().broadcastUpdate(BluetoothManage.ACTION_GATT_SERVICES_DISCOVERED);
                }else {
                    BluetoothManage.getInstance().broadcastUpdate(BluetoothManage.ACTION_BLE_NOTIFICATION_ERROR);
                }

            } else {
                Logger.d( "onServicesDiscovered received: " + status);
            }
        }

        /**
         * 主动读写设备的回调
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Logger.d("-----------------onCharacteristicRead");
                doAfterCharacteristicChanged(gatt.getDevice(), characteristic);
            }
        }

        /**
         * 订阅的Characteristic发生变化的回调
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Logger.d("-----------------onCharacteristicChanged");
            doAfterCharacteristicChanged(gatt.getDevice(), characteristic);
        }
    };

    /**
     *  连接设备
     * @param address 设备地址值
     * @param deviceType 设备的类型
     * @return 是否连接成功
     */
    public boolean connect(String address, DeviceType deviceType) {
        if (bluetoothAdapter == null || address == null) {
            BluetoothManage.getInstance().broadcastUpdate(BluetoothManage.ACTION_BLE_CONNECT_ERROR);
            Logger.d( "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        //尝试连接现有设备
        final BluetoothManage.DeviceHolder holder = DEVICE_HOLDERS.get(address);
        if (holder != null && holder.gatt != null) {
            try {
                if (holder.gatt.connect()) {
                    return true;
                } else {
                    BluetoothManage.getInstance().closeGatt(holder.gatt);
                }
            } catch (Exception e) {
                BluetoothManage.getInstance().closeGatt(holder.gatt);
            }
        }
        connectToDevice(address, deviceType);
        return  true;
    }

    /**
     *  连接设备
     *  DEVICE_HOLDERS 保存设备的信息
     * @param address 设备地址值
     * @param deviceType 设备的类型
     * @return 是否连接成功
     */
    private void connectToDevice(final String address, DeviceType deviceType) {
        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

        if (device == null) {
            Logger.d( "Device not found.  Unable to connect.");
        }

        BluetoothGatt bluetoothGatt = device.connectGatt(context, false, gattCallback);
        Logger.d("Trying to create a new connection.");
        BluetoothManage.DeviceHolder _holder = new BluetoothManage.DeviceHolder();
        _holder.address = device.getAddress();
        _holder.gatt = bluetoothGatt;
        _holder.deviceType = deviceType;
        DEVICE_HOLDERS.put(address, _holder);
    }

    /**
     * 订阅 characteristic
     * @param gatt gatt
     * @param tempretureServiceUuid service 的UUID
     * @param characteristicUUID characteristic的UUID
     * @return 是否订阅characteristic成功
     */
    public boolean enableNotification(BluetoothGatt gatt, UUID tempretureServiceUuid, UUID characteristicUUID) {

        if (bluetoothAdapter == null) {
            Logger.d("BluetoothAdapter not initialized");
            return false;
        }
        BluetoothGattService RxService = null;
        for (int i = 0; i < 4; i++) {
            RxService = getService(TEMPRETURE_SERVICE_UUID, gatt);
            if (RxService == null) {
                try {
                    Logger.i( " service not found when enableNotification! try again");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }

        if (RxService == null) {
            BluetoothManage.getInstance().broadcastUpdate(BluetoothManage.ACTION_BLE_GET_SERVICE_ERROR);
            Logger.i( "RXTX service not found!");
            return false;
        }


        BluetoothGattCharacteristic readChar = null;
        for (int i = 0; i < 4; i++) {
            readChar = getCharacteristic(characteristicUUID, RxService);

            if (readChar == null) {
                try {
                    Logger.i( "Read charateristic not found! try again");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }

        if (readChar == null) {
            BluetoothManage.getInstance().broadcastUpdate(BluetoothManage.ACTION_BLE_GET_CHARACTERISTIC_ERROR);
            Logger.i("Read charateristic not found!");
            return false;
        }

        boolean statu = gatt.setCharacteristicNotification(readChar, true);

        Logger.i( "setCharacteristicNotification:" + statu);

        //解决订阅后没有收到通知的问题
        List<BluetoothGattDescriptor> descriptors = readChar.getDescriptors();
        for (BluetoothGattDescriptor dp : descriptors) {
            dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(dp);
        }
        return statu;
    }

    /**
     * 关闭所有连接
     * @param address 设备地址值
     */
    public void disconnect(String address) {
        final DeviceHolder holder = DEVICE_HOLDERS.get(address);
        if (holder == null || holder.gatt == null) {
            Logger.d("BluetoothAdapter not initialized");
            return;
        }
        closeGatt(holder.gatt);

    }

    /**
     * @return 获取到连接的设备
     */
    public Map<String, Float> getConnectedDevicesValue() {
        Map<String,Float> map = new TreeMap<String, Float>();
        DeviceHolder holder;
        for (Map.Entry<String,DeviceHolder> entry :DEVICE_HOLDERS.entrySet()){
            holder = entry.getValue();

            if (holder != null && holder.gatt != null ){
                try {
                    int connectionState = bluetoothManager.getConnectionState(
                            holder.gatt.getDevice(),BluetoothProfile.GATT);
                    if (connectionState == BluetoothProfile.STATE_CONNECTED){
                        map.put(entry.getKey(),holder.lastValue);
                    }
                }catch (Exception e){
                    Logger.d(e.getMessage());
                }
            }
        }
        return map;
    }

    /**
     * @param enable 是否在扫描,如果在扫描在停止扫描,没有扫描 则开始扫描
     */
    public void scanLeDevice(boolean enable) {
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            // 广播:设备蓝牙没开启
            BluetoothManage.getInstance().broadcastUpdate(BluetoothManage.ACTION_BLUETOOTH_UNABLED);
            return;
        }
        if (enable) {
            if (!scanning) {
                synchronized (scanLock){
                    disconnectAllDevices();
                    UIUtils.getMainThreadHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scanning = false;
                            bluetoothAdapter.stopLeScan(leScanCallback);
                            BluetoothManage.getInstance().broadcastUpdate(BluetoothManage.ACTION_BLE_FINISH_SCANNING);
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
            BluetoothManage.getInstance().broadcastUpdate(BluetoothManage.ACTION_BLE_FINISH_SCANNING);
        }
    }

    public void closeDBManage() {
        if(dbManager != null){
            dbManager.close();
            dbManager = null;
        }
    }

    private  static    class SingleBluetoothManage{
        private static  final BluetoothManage INSTANCE=new BluetoothManage();
    }
    //获取单例
    public static BluetoothManage getInstance() {
        return BluetoothManage.SingleBluetoothManage.INSTANCE;
    }



    /**
     * 读取改变的Characteristic的数据以及修改保存在本地的数据
     */
    private void doAfterCharacteristicChanged(BluetoothDevice device, BluetoothGattCharacteristic characteristic) {
        byte[] bytes = characteristic.getValue();
        int header = (bytes[0] & 0xFF);
//		LogUtil.i(TAG, "first byte 0x" + Integer.toHexString(header));
        DeviceHolder holder = DEVICE_HOLDERS.get(device.getAddress());
        Intent       intent = null;
        if (holder != null) {
            intent = ResponseAnalyzer.analyze(bytes, holder.deviceType);
        }
        if (intent != null) {
            if(intent.getAction().equals(PresentDataResponse.ACTION_REALTIME_TEMPRETURE)){
                int measureInterval = SharedPreferencesUtil.getIntValue(Constant.KEY_MEASURE_INTERVAL,1);
                if(System.currentTimeMillis() >= (recordTime + measureInterval*60*1000)){
                    Temperature temperature = intent.getParcelableExtra(PresentDataResponse.EXTRA_PRESENT_DATA);
                    if(temperature != null&&temperature.getValue()>34){
                        String currentUserName = "";
                        List<DeviceUser> deviceUsers = SharedPreferencesUtil.getList(Constant.DEVICE_USER_LIST, DeviceUser.class);
                        for (DeviceUser deviceuser:deviceUsers) {
                            if (deviceuser.getAddress().equalsIgnoreCase(device.getAddress())){
                                currentUserName = deviceuser.getUserId();
                                break;
                            }
                        }
                        if(StringUtils.isNotBlank(currentUserName)){
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
            intent.putExtra(BluetoothService.EXTRA_DEVICE, device);
            intent.putExtra(BluetoothService.EXTRA_ADDRESS, device.getAddress());
           context.sendBroadcast(intent);
        }
    }

    /**
     * 断开所有设备的连接
     */
    public static void disconnectAllDevices() {
        for (Map.Entry<String, DeviceHolder> entry : DEVICE_HOLDERS.entrySet()) {
            final DeviceHolder holder = entry.getValue();
            if (holder != null && holder.gatt != null) {
                try {
                    BluetoothManage.getInstance().closeGatt(holder.gatt);
                    Logger.i(  "disconnect:" + holder.address);
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }

            }
        }
    }

    /**
     * @return 获取到本地保存的设备信息
     */
    public static Map<String, DeviceHolder> getDeviceHolders() {
        return DEVICE_HOLDERS;
    }

    private BluetoothManage(){}
    /**
     *  广播消息
     * @param action 动作
     */
    public void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
       UIUtils.getContext(). sendBroadcast(intent);
    }

    /**
     * 广播消息
     * @param action 动作
     * @param device 设备
     */
    public void broadcastUpdate(final String action, BluetoothDevice device) {
        final Intent intent = new Intent(action);
        intent.putExtra(BluetoothService.EXTRA_DEVICE, device);
        UIUtils.getContext(). sendBroadcast(intent);
    }

    /**
     * 获取到BluetoothGattService
     * @param serviceUuid
     * @param gatt
     * @return
     */
    public BluetoothGattService getService(UUID serviceUuid, BluetoothGatt gatt) {
        return gatt.getService(serviceUuid);
    }

    /**获取到 BluetoothGattCharacteristic
     * @param characteristicUuid
     * @param service
     * @return
     */
    public BluetoothGattCharacteristic getCharacteristic(UUID characteristicUuid, BluetoothGattService service) {
        return service.getCharacteristic(characteristicUuid);
    }
    public void closeGatt(final BluetoothGatt gatt){
        UIUtils.getMainThreadHandler().post(new Runnable() {
            @Override
            public void run() {
               gatt.disconnect();
                gatt.close();
            }
        });

    }

    /**
     * 写入特性值
     * @param address 设备地址
     * @param value 命令值
     */

    public boolean writeCharacteristic(String address, byte[] value) {
        DeviceHolder holder = DEVICE_HOLDERS.get(address);
        if (holder != null && holder.gatt != null) {
            BluetoothGattService RxService = null;
            for (int i = 0; i < 3; i++) {
                RxService = getService(TEMPRETURE_SERVICE_UUID, holder.gatt);

                if (RxService == null) {
                    try {
                        Logger.i( " service not found when writeCharacteristic! try again");
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
            if (RxService == null) {
                Logger.e( " service not found when writeCharacteristic!");
                return false;
            }
            BluetoothGattCharacteristic characteristic = null;
            for (int i = 0; i < 3; i++) {
                characteristic = getCharacteristic(TEMPRETURE_WRITE_UUID, RxService);

                if (characteristic == null) {
                    try {
                        Logger.e( "write charateristic not found! try again");
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
            if (characteristic == null) {
                Logger.e("write charateristic not found!");
                return false;
            }
            characteristic.setValue(value);

            boolean status = holder.gatt.writeCharacteristic(characteristic);
            if (status) {
                holder.lastCommand = value[0];
            }

            Logger.i( "write " + CommonUtil.bytesToHexString(value) + " - status=" + status);
            return status;
        } else {
            return false;
        }
    }


    /**
     * 设备数据绑定类
     */
    public class DeviceHolder {
        String        address;
        DeviceType    deviceType;
        BluetoothGatt gatt;
        byte          lastCommand;
        float		  lastValue;
    }



}

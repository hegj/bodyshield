package com.cmax.bodysheild.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.activity.fragment.TemperatureChartFragment;
import com.cmax.bodysheild.activity.fragment.TemperatureInfoImgFragment;
import com.cmax.bodysheild.alert.AlertService;
import com.cmax.bodysheild.bean.ble.BLEDevice;
import com.cmax.bodysheild.bean.ble.Temperature;
import com.cmax.bodysheild.bean.cache.DeviceUser;
import com.cmax.bodysheild.bean.cache.User;
import com.cmax.bodysheild.bluetooth.BluetoothService;
import com.cmax.bodysheild.bluetooth.response.temperature.PresentDataResponse;
import com.cmax.bodysheild.util.CommonUtil;
import com.cmax.bodysheild.util.Constant;
import com.cmax.bodysheild.util.PortraitUtil;
import com.cmax.bodysheild.util.SharedPreferencesUtil;
import com.cmax.bodysheild.util.UIUtils;
import com.cmax.bodysheild.widget.CircleImageView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TemperatureInfoActivity extends FragmentActivity {

    public final static String EXTRA_DEVICE = "com.cmax.bodysheild.Temperature.EXTRA_DEVICE";
    private final static String TAG = TemperatureInfoActivity.class.getSimpleName();

    @Bind(R.id.pager)
    ViewPager pager;
    @Bind(R.id.temperature_info_date)
    TextView  dateTextView;
    @Bind(R.id.temperature_info_time)
    TextView  timeTextView;
    @Bind(R.id.currentDeviceName)
    TextView currentDeviceName;
    @Bind(R.id.currentUserName)
    TextView  currentUserName;
    @Bind(R.id.userImageBtn)
    CircleImageView userImageBtn;
    MyPagerAdapter             adapter;
    TemperatureChartFragment   chartFragment;
    TemperatureInfoImgFragment imgFragment;
    private BLEDevice device;
    private AlertService alertService;
//    private BLEService bleService;
    private ServiceConnection serviceConnection;
//    private ServiceConnection bluetoothServiceConnection;
    private AlertService.LocalBinder alertBinder;
//    private BluetoothService.LocalBinder bleBinder;
    private boolean isRegister = false; //是否已注册广播接收器
    private boolean isBind = false;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    DecimalFormat df = new DecimalFormat("#.00");
    private              Handler          handler     = new Handler();

    private final BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (PresentDataResponse.ACTION_REALTIME_TEMPRETURE.equals(action)) {
                Temperature receiverTemperature = intent.getParcelableExtra(PresentDataResponse.EXTRA_PRESENT_DATA);
                String address = intent.getStringExtra(BluetoothService.EXTRA_ADDRESS);
                if (device.getAddress().equals(address)) {
                    float y = receiverTemperature.getValue();
//                    if(y > 33 && y < 34){
//                        String str = "";
//                        for(byte b : receiverTemperature.getBleData()){
//                            str += b + ",";
//                        }
//                        dateTextView.setText(CommonUtil.bytesToHexString(receiverTemperature.getBleData()));
//                        currentDeviceName.setText(str);
//                        Log.i("实际数据",str);
//                    }
                    Log.i(TAG,"实时数据："+y);
                    if (imgFragment != null) {
                        imgFragment.setTempValue(CommonUtil.getTemperature(y));
                        imgFragment.refreshTemperature(receiverTemperature);
                    }
                    if (chartFragment != null && y != -1 && y != 1) {
                        chartFragment.setPoint(receiverTemperature);
                    }


//                    DBManager dbManager = new DBManager(context);
//                    HistoryData historyData = new HistoryData();
//                    historyData.setDeviceAddress(address);
//                    historyData.setTimestamp(new Date().getTime());
//                    historyData.setUserId(SharedPreferencesUtil.getStringValue(device.getAddress(), ""));
//                    historyData.setValue(y);
//                    dbManager.addHistory(historyData);
                }
            }
//            if (LogIntervalResponse.ACTION_LOG_INTERVAL.equals(action)) {
//                Log.i(TAG, "获取到测量间隔");
//                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothService.EXTRA_DEVICE);
//                if (device.getAddress().equalsIgnoreCase(btDevice.getAddress())) {
//                    LogInterval logInterval = LogIntervalResponse.getResponseResult(intent);
//                    if (logInterval != null) {
//                        Log.i(TAG, "测量间隔是(s)：" + logInterval.getInterval() * 30);
//                        SharedPreferencesUtil.setIntValue(Constant.KEY_MEASURE_INTERVAL,logInterval.getInterval());
//                    }
//                }
//            }
        }
    };


    @OnClick(R.id.backBtn)
    void backClick(){
        Intent intent = new Intent(this,DeviceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature_info);
        handler.post(refreshTimeRunnable);
        ButterKnife.bind(this);
        adapter = new MyPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(adapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        Bundle extras = getIntent().getExtras();
        device = extras.getParcelable(TemperatureInfoActivity.EXTRA_DEVICE);
//        deviceName = device.getName();
//        if(deviceName.indexOf("Body Temp") >= 0){
//            deviceName = "AB01";
//        }
//        currentDeviceName.setText(device.getName());
//        SharedPreferencesUtil.setIntValue(Constant.KEY_UNIT,1);
        serviceConnection = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                alertBinder = (AlertService.LocalBinder) service;
                alertService = alertBinder.getService();
                alertService.setDevice(device);
            }
        };

        Intent intent = new Intent(TemperatureInfoActivity.this, AlertService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        isBind = true;
//        bluetoothServiceConnection = new ServiceConnection() {
//
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//
//            }
//
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder service) {
//                bleBinder = (BluetoothService.LocalBinder) service;
//                bleService = bleBinder.getBLEService();
//                LogIntervalCommand logIntervalCommand = new LogIntervalCommand(device.getAddress(),LogIntervalCommand.ReqeustType.ReadModel);
//                bleService.executeCommand(logIntervalCommand);
//                Log.i(TAG,"发送测量间隔命令成功");
//
//            }
//
//        };
//        Intent intent2 = new Intent(TemperatureInfoActivity.this, BluetoothService.class);
//        bindService(intent2, bluetoothServiceConnection, Context.BIND_AUTO_CREATE);



//        SharedPreferencesUtil.setIntValue(Constant.KEY_TIME_FLAG,(int)new Date().getTime());
    }

    @Override
    protected void onResume() {
        super.onResume();
        dateTextView.setText(DATE_FORMAT.format(new Date()));

        DeviceUser deviceUser = getCurrentDeviceUser(device);
        if(deviceUser != null){
            setUser(deviceUser.getUserId(),device);
        }

        currentUserName.setText(device.getUserName());
        currentDeviceName.setText(device.getName());

        if (!isRegister) {
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(PresentDataResponse.ACTION_REALTIME_TEMPRETURE);
            registerReceiver(notificationReceiver, intentFilter);
            isRegister = true;
        }
    }

    private DeviceUser getCurrentDeviceUser(BLEDevice device){
        List<DeviceUser> deviceUsers = SharedPreferencesUtil.getList(Constant.DEVICE_USER_LIST,
                DeviceUser.class);
        for (DeviceUser deviceUser:deviceUsers) {
            if (deviceUser.getAddress().equalsIgnoreCase(device.getAddress())){
                return deviceUser;
            }
        }

        return null;
    }

    private void setUser(String id,BLEDevice device){
        List<User> users = SharedPreferencesUtil.getList(Constant.USER_LIST,
                User.class);
        for (User user:users) {
            if (user.getId().equals(id)){
                device.setUserName(user.getUserName());
                String userImage = user.getImage();
                Bitmap bm = PortraitUtil.getBitmap(this,userImage);
                userImageBtn.setImageBitmap(bm);
                UIUtils.setUserId(Integer.parseInt(id));
                break;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isRegister) {
            unregisterReceiver(notificationReceiver);
            isRegister = false;
        }
        if(isBind) {
            unbindService(serviceConnection);
//            unbindService(bluetoothServiceConnection);
            isBind = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRegister) {
            unregisterReceiver(notificationReceiver);
            isRegister = false;
        }
        if(isBind) {
            unbindService(serviceConnection);
//            unbindService(bluetoothServiceConnection);
            isBind = false;
        }
    }


    @OnClick(R.id.historyBtn)
    public void showHistory(){
        Intent intent = new Intent(this,TemperatureHistoryInfoActivity.class);
        intent.putExtra(TemperatureInfoActivity.EXTRA_DEVICE, device);
        startActivity(intent);
    }

    @OnClick(R.id.settingBtn)
    public void setting(){
        Intent intent = new Intent(this,SettingActivity.class);
        intent.putExtra(TemperatureInfoActivity.EXTRA_DEVICE, device);
        startActivity(intent);
    }

    public void onColorClicked(View view){
        Log.i(this.getClass().getSimpleName(), view.toString());
    }

    @OnClick(R.id.userImageBtn)
    void userImageClick(){
        final Intent intent = new Intent(this, UserListActivity.class);
        intent.putExtra(TemperatureInfoActivity.EXTRA_DEVICE, device);
        startActivity(intent);
    }

    private Runnable refreshTimeRunnable = new Runnable() {
        @Override
        public void run() {
            timeTextView.setText(TIME_FORMAT.format(new Date()));
            handler.postDelayed(refreshTimeRunnable, 1000);
        }
    };


    public class MyPagerAdapter extends FragmentPagerAdapter {


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    imgFragment = new TemperatureInfoImgFragment();
                    return imgFragment;
                case 1:
                    chartFragment = new TemperatureChartFragment();
                    return chartFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {

            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position==0){
                return "img";
            }
            else if (position==1){
                return "chart";
            } else {
                return "";
            }
        }
    }
}

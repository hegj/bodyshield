package com.cmax.bodysheild.activity;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.alert.AlertService;
import com.cmax.bodysheild.bean.ble.BLEDevice;
import com.cmax.bodysheild.bean.ble.LogInterval;
import com.cmax.bodysheild.bean.ble.MemoryStatus;
import com.cmax.bodysheild.bluetooth.BLEService;
import com.cmax.bodysheild.bluetooth.BluetoothService;
import com.cmax.bodysheild.bluetooth.command.temperature.ContinuousDataCommand;
import com.cmax.bodysheild.bluetooth.command.temperature.DataRequestCommand;
import com.cmax.bodysheild.bluetooth.command.temperature.LogIntervalCommand;
import com.cmax.bodysheild.bluetooth.response.temperature.LogIntervalResponse;
import com.cmax.bodysheild.bluetooth.response.temperature.MemoryStatusResponse;
import com.cmax.bodysheild.util.CommonUtil;
import com.cmax.bodysheild.util.Constant;
import com.cmax.bodysheild.util.SharedPreferencesUtil;
import com.cmax.bodysheild.widget.LoadingMask;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {
    @Bind(R.id.battery_level)
    TextView  batteryLevel;
//    @Bind(R.id.switch_unit)
//    Switch    tempUnit;
    @Bind(R.id.switch_unit)
    ToggleButton tempUnit;
    @Bind(R.id.high_fever)
    EditText  highFever;
    @Bind(R.id.low_fever)
    EditText  lowFever;
//    @Bind(R.id.switch_vibration)
//    Switch    vibration;
    @Bind(R.id.switch_vibration)
    ToggleButton vibration;
    @Bind(R.id.backBtn)
    ImageView backBtn;
    @Bind(R.id.alert_interval)
    SeekBar alertInterval;
    @Bind(R.id.measure_interval)
    SeekBar measureInterval;
    @Bind(R.id.volume)
    SeekBar volume;
    @Bind(R.id.alert_interval_text)
    TextView alertIntervalText;
    @Bind(R.id.measure_interval_text)
    TextView measureIntervalText;
    @Bind(R.id.deg_high_fever)
    TextView degHighFever;
    @Bind(R.id.deg_low_fever)
    TextView degLowFever;
    @BindString(R.string.setting_every)
    String every;
    @BindString(R.string.setting_min)
    String min;

    private final static String TAG            = SettingActivity.class.getSimpleName();
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.00");

    private              int    unitCode       = 0;   //0是摄氏度，1是华氏度
    private              float    highFeverValue = 0;
    private              float    lowFeverValue  = 0;
    private              int    vibratedCode   = 0;
    private int alertIntervalValue = 1;
    private int measureIntervalValue = 1;
    private int volumeValue = 1;

    private BLEService                   bleService;
    private ServiceConnection            serviceConnection;
    private BluetoothService.LocalBinder bleBinder;
    private AlertService alertService;
    private ServiceConnection alertServiceConnection;
    private AlertService.LocalBinder alertBinder;
    private boolean isBind = false;
    private boolean isRegister = false;
    private BLEDevice device;
    private LoadingMask mask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        device = extras.getParcelable(TemperatureInfoActivity.EXTRA_DEVICE);

        initView();
        initEvent();

        serviceConnection = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                bleBinder = (BluetoothService.LocalBinder) service;
                bleService = bleBinder.getBLEService();

                DataRequestCommand command = new DataRequestCommand(device.getAddress(),
                        DataRequestCommand.ReqeustType.MemoryStatus);
//                LogIntervalCommand logIntervalCommand = new LogIntervalCommand(device.getAddress(),LogIntervalCommand.ReqeustType.ReadModel);
                bleService.executeCommand(command);
//                bleService.executeCommand(logIntervalCommand);

            }
        };

        bindService(new Intent(SettingActivity.this, BluetoothService.class), serviceConnection,
                Context.BIND_AUTO_CREATE);



        alertServiceConnection = new ServiceConnection() {

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
        Intent intent = new Intent(this, AlertService.class);
        bindService(intent, alertServiceConnection, Context.BIND_AUTO_CREATE);
        isBind = true;

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isRegister){
            registerReceiver(notificationReceiver, makeIntentFilter());
            isRegister = true;
        }
        if (!isBind){
            bindService(new Intent(SettingActivity.this, BluetoothService.class), serviceConnection, Context.BIND_AUTO_CREATE);

            Intent intent = new Intent(this, AlertService.class);
            bindService(intent, alertServiceConnection, Context.BIND_AUTO_CREATE);
            isBind = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if(mask != null){
//            mask.hide();
//        }
        if(isBind){
            unbindService(serviceConnection);
            unbindService(alertServiceConnection);
            isBind = false;
        }
        if (isRegister) {
            unregisterReceiver(notificationReceiver);
            isRegister = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isBind){
            unbindService(serviceConnection);
            unbindService(alertServiceConnection);
            isBind = false;
        }
        if (isRegister) {
            unregisterReceiver(notificationReceiver);
            isRegister = false;
        }
//        if (mask != null){
//            mask.close();
//            mask = null;
//        }
    }

    //    @OnClick(R.id.backBtn)
//    public void backAndSave(){
//
//    }
    @OnCheckedChanged(R.id.switch_unit)
    void selectUnit(boolean isChecked) {
        if (isChecked) {
            if(0 == unitCode){
                String hfv = highFever.getText().toString();
                String lfv = lowFever.getText().toString();
                float hfvf = Float.parseFloat(hfv);
                float lfvf = Float.parseFloat(lfv);
                highFever.setText(DECIMAL_FORMAT.format(CommonUtil.centigradeToFahrenheit(hfvf)));
                lowFever.setText(DECIMAL_FORMAT.format(CommonUtil.centigradeToFahrenheit(lfvf)));
            }
            unitCode = 1;
            degHighFever.setText("℉");
            degLowFever.setText("℉");
            Log.i(TAG, "温度单位是华氏度");
        } else {
            if(1 == unitCode){
                String hfv = highFever.getText().toString();
                String lfv = lowFever.getText().toString();
                float hfvf = Float.parseFloat(hfv);
                float lfvf = Float.parseFloat(lfv);
                highFever.setText(DECIMAL_FORMAT.format(CommonUtil.fahrenheitToCentigrade(hfvf)));
                lowFever.setText(DECIMAL_FORMAT.format(CommonUtil.fahrenheitToCentigrade(lfvf)));
            }
            unitCode = 0;
            degHighFever.setText("℃");
            degLowFever.setText("℃");
            Log.i(TAG, "温度单位是摄氏度");
        }
    }

    @OnCheckedChanged(R.id.switch_vibration)
    void selectVibrated(boolean isChecked) {
        if (isChecked) {
            vibratedCode = 1;
            Log.i(TAG, "开启了震动告警");
        } else {
            vibratedCode = 0;
            Log.i(TAG,"关闭了震动告警");
        }
    }


    @OnClick(R.id.backBtn)
    void finishActivity(View view){
        ContinuousDataCommand continuousDataCommand = new ContinuousDataCommand(device.getAddress(), ContinuousDataCommand.ReqeustType.StartModel);
        bleService.executeCommand(continuousDataCommand);
        finish();
    }

    @OnClick(R.id.saveBtn)
    void save(){
//        if(mask == null) {
//            mask = new LoadingMask(this);
//        }
//        mask.show();
        saveParams();
        /*发送设置测量间隔命令*/
//        LogIntervalCommand command = new LogIntervalCommand(device.getAddress(),LogIntervalCommand.ReqeustType.WriteModel);
//        command.writeInterval(measureIntervalValue * 2);
//        bleService.executeCommand(command);
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        ContinuousDataCommand continuousDataCommand = new ContinuousDataCommand(device.getAddress(), ContinuousDataCommand.ReqeustType.StartModel);
        bleService.executeCommand(continuousDataCommand);
//        mask.hide();
        finish();
    }

    @OnClick(R.id.go_user_guide)
    void showUserGuide(){
        Intent intent = new Intent(this,UserGuideActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.go_feedback)
    void showFeedback(){
        Intent intent = new Intent(this,FeedbackActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.go_about_us)
    void showAboutUs(){
        Intent intent = new Intent(this, AboutUsActivity.class);
        startActivity(intent);
    }
    private void initView(){
        highFeverValue = SharedPreferencesUtil.getFloatValue(Constant.KEY_HIGHT_FEVER, 39);
        lowFeverValue = SharedPreferencesUtil.getFloatValue(Constant.KEY_LOW_FEVER, 38);
        unitCode = SharedPreferencesUtil.getIntValue(Constant.KEY_UNIT,0);
        vibratedCode = SharedPreferencesUtil.getIntValue(Constant.KEY_VIBRATION,0);
        alertIntervalValue = SharedPreferencesUtil.getIntValue(Constant.KEY_ALERT_INTERVAL, 1);
        measureIntervalValue = SharedPreferencesUtil.getIntValue(Constant.KEY_MEASURE_INTERVAL, 1);
        volumeValue = SharedPreferencesUtil.getIntValue(Constant.KEY_VOLUME, 1);



        if(0 == unitCode){
            highFever.setText(""+highFeverValue);
            lowFever.setText(""+lowFeverValue);
            degHighFever.setText("℃");
            degLowFever.setText("℃");
            tempUnit.setChecked(false);
        }else {
            highFever.setText(""+CommonUtil.centigradeToFahrenheit(highFeverValue));
            lowFever.setText(""+CommonUtil.centigradeToFahrenheit(lowFeverValue));
            degHighFever.setText("℉");
            degLowFever.setText("℉");
            tempUnit.setChecked(true);
        }
        if(0 == vibratedCode){
            vibration.setChecked(false);
        }else {
            vibration.setChecked(true);
        }
        alertInterval.setProgress(alertIntervalValue);
        alertIntervalText.setText(every + alertIntervalValue + min);
        measureInterval.setProgress(measureIntervalValue);
        measureIntervalText.setText(every + measureIntervalValue + min);
        volume.setProgress(volumeValue);
    }

    private void initEvent(){
        /*告警间隔设置*/
        alertInterval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * 拖动条停止拖动的时候调用
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(0 == seekBar.getProgress()){
                    seekBar.setProgress(1);
                }
                Log.i(TAG,"拖动停止");
            }
            /**
             * 拖动条开始拖动的时候调用
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i(TAG, "开始拖动");
            }
            /**
             * 拖动条进度改变的时候调用
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                alertIntervalText.setText(every + progress + min);
            }
        });

        /*测量间隔设置*/
        measureInterval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * 拖动条停止拖动的时候调用
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int value = seekBar.getProgress();
                if(0 == seekBar.getProgress()){
                    seekBar.setProgress(1);
                }
                Log.i(TAG,"拖动停止,值为："+value);
            }
            /**
             * 拖动条开始拖动的时候调用
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i(TAG, "开始拖动");
            }
            /**
             * 拖动条进度改变的时候调用
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                measureIntervalText.setText(every + progress + min);
            }
        });

        /*音量设置*/
        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * 拖动条停止拖动的时候调用
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int value = seekBar.getProgress();
                Log.i(TAG,"拖动停止,值为："+value);
            }
            /**
             * 拖动条开始拖动的时候调用
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i(TAG, "开始拖动");
            }
            /**
             * 拖动条进度改变的时候调用
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //TODO
            }
        });
    }

    private void saveParams(){
        highFeverValue = Float.valueOf(highFever.getText().toString());
        lowFeverValue = Float.valueOf(lowFever.getText().toString());
        if(1 == unitCode){
            highFeverValue = CommonUtil.fahrenheitToCentigrade(highFeverValue);
            lowFeverValue = CommonUtil.fahrenheitToCentigrade(lowFeverValue);
        }
        alertIntervalValue = alertInterval.getProgress();
        measureIntervalValue = measureInterval.getProgress();
        volumeValue = volume.getProgress();

        SharedPreferencesUtil.setFloatValue(Constant.KEY_HIGHT_FEVER,highFeverValue);
        SharedPreferencesUtil.setFloatValue(Constant.KEY_LOW_FEVER,lowFeverValue);
        SharedPreferencesUtil.setIntValue(Constant.KEY_UNIT, unitCode);
        SharedPreferencesUtil.setIntValue(Constant.KEY_VIBRATION, vibratedCode);
        SharedPreferencesUtil.setIntValue(Constant.KEY_ALERT_INTERVAL,alertIntervalValue);
        SharedPreferencesUtil.setIntValue(Constant.KEY_MEASURE_INTERVAL,measureIntervalValue);
        SharedPreferencesUtil.setIntValue(Constant.KEY_VOLUME,volumeValue);
        alertService.refresh(); //刷新告警服务的信息
    }

    private final BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (MemoryStatusResponse.ACTION_MEMORY_STATUS.equals(action)) {
                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothService.EXTRA_DEVICE);

                if (device.getAddress().equalsIgnoreCase(btDevice.getAddress())) {
                    MemoryStatus status = MemoryStatusResponse.getResponseResult(intent);
                    if (status != null) {
                        batteryLevel.setText(status.getBatteryLevel() + "%");
                    }
//                    LogIntervalCommand logIntervalCommand = new LogIntervalCommand(device.getAddress(), LogIntervalCommand.ReqeustType.ReadModel);
//                    bleService.executeCommand(logIntervalCommand);
                }
            }
//            if (LogIntervalResponse.ACTION_LOG_INTERVAL.equals(action)) {
//
//                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothService.EXTRA_DEVICE);
//                if (device.getAddress().equalsIgnoreCase(btDevice.getAddress())) {
//                    LogInterval logInterval = LogIntervalResponse.getResponseResult(intent);
//                    if (logInterval != null) {
//                        measureInterval.setProgress(logInterval.getInterval() / 2);
//                        SharedPreferencesUtil.setIntValue(Constant.KEY_MEASURE_INTERVAL, logInterval.getInterval() / 2);
////                        measureIntervalValue = SharedPreferencesUtil.getIntValue(Constant.KEY_MEASURE_INTERVAL, 1);
////                        measureInterval.setProgress(measureIntervalValue);
//                        Log.i(TAG, "测量间隔是(s)：" + logInterval.getInterval() * 30);
//                        /*发送连续获取温度数据的命令*/
//                        ContinuousDataCommand continuousDataCommand = new ContinuousDataCommand(device.getAddress(), ContinuousDataCommand.ReqeustType.StartModel);
//                        bleService.executeCommand(continuousDataCommand);
//                    }
//                }
//            }
        }
    };

    private static IntentFilter makeIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(MemoryStatusResponse.ACTION_MEMORY_STATUS);
//        intentFilter.addAction(LogIntervalResponse.ACTION_LOG_INTERVAL);

        return intentFilter;
    }
}

package com.cmax.bodysheild.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.alert.AlertService;
import com.cmax.bodysheild.bluetooth.BluetoothService;

public class WelcomeActivity extends Activity {
    private static final int GOTO_MAIN_ACTIVITY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //启动蓝牙服务
        startService(new Intent(getApplicationContext(), BluetoothService.class));

        //启动告警服务
        startService(new Intent(getApplicationContext(), AlertService.class));

        mHandler.sendEmptyMessageDelayed(GOTO_MAIN_ACTIVITY, 3000);//3秒跳转
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case GOTO_MAIN_ACTIVITY:
                    Intent intent = new Intent();
                    intent.setClass(WelcomeActivity.this, DeviceActivity.class);
                    intent.putExtra("autoScan",true);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
        };
    };

}

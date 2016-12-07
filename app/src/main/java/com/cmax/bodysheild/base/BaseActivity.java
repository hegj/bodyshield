package com.cmax.bodysheild.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.MotionEvent;

import com.cmax.bodysheild.util.Constant;

/**
 * Created by allen on 15/9/27.
 */
public class BaseActivity extends Activity{

    private long lastClickTime = 0L;
    private boolean isRegister = false;

    @Override
    protected void onResume() {
        super.onResume();
        if(!isRegister){
            // 在当前的activity中注册广播
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constant.EXIT);
            this.registerReceiver(this.finishAppReceiver, filter);
            isRegister = true;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this.finishAppReceiver);
        isRegister = false;
    }

    /**
     * 关闭Activity的广播，放在自定义的基类中，让其他的Activity继承这个Activity就行
     */
    protected BroadcastReceiver finishAppReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isFastDoubleClick()) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (timeD >= 0 && timeD <= 600) {
            return true;
        } else {
            lastClickTime = time;
            return false;
        }
    }
}

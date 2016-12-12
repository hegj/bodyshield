package com.cmax.bodysheild.util;

import android.app.Activity;
import android.content.Intent;

import com.cmax.bodysheild.activity.RegisterActivity;
import com.cmax.bodysheild.activity.TemperatureInfoActivity;
import com.cmax.bodysheild.activity.UserListActivity;
import com.cmax.bodysheild.activity.login.LoginActivity;
import com.cmax.bodysheild.activity.login.LoginActivity2;
import com.cmax.bodysheild.bean.ble.BLEDevice;
import com.cmax.bodysheild.bean.cache.User;

/**
 * Created by Administrator on 2016/12/12 0012.
 */

public class IntentUtils {
    public static void toLoginActivity(Activity activity, User user, BLEDevice device) {
        final Intent intent = new Intent(activity, LoginActivity.class);
        if (device != null)
            intent.putExtra(TemperatureInfoActivity.EXTRA_DEVICE, device);
        if (user != null)
            intent.putExtra(UserListActivity.CURRENT_USER, user);
        activity.startActivity(intent);
    }

    public static void toRegisterActivity(Activity activity) {
        Intent intent = new Intent(activity, RegisterActivity.class);
        activity.startActivityForResult(intent,10);
    }
}

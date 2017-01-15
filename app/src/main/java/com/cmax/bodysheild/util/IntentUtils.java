package com.cmax.bodysheild.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.cmax.bodysheild.activity.FeedbackActivity;
import com.cmax.bodysheild.activity.RegisterActivity;
import com.cmax.bodysheild.activity.SettingActivity;
import com.cmax.bodysheild.activity.TemperatureInfoActivity;
import com.cmax.bodysheild.activity.UserListActivity;
import com.cmax.bodysheild.activity.user.UserProfileEditActivity;
import com.cmax.bodysheild.activity.login.LoginActivity2;
import com.cmax.bodysheild.bean.ble.BLEDevice;
import com.cmax.bodysheild.bean.cache.User;

/**
 * Created by Administrator on 2016/12/12 0012.
 */

public class IntentUtils {
    public static void toLoginActivity(Activity activity, User user, BLEDevice device) {
          Intent intent = new Intent(activity, LoginActivity2.class);
        if (device != null)
            intent.putExtra(TemperatureInfoActivity.EXTRA_DEVICE, device);
        if (user != null)
            intent.putExtra(UserListActivity.CURRENT_USER, user);
        activity.startActivity(intent);
    }

    public static void toRegisterActivity(Activity activity,BLEDevice device) {
        Intent intent = new Intent(activity, RegisterActivity.class);
        intent.putExtra(TemperatureInfoActivity.EXTRA_DEVICE, device);
        activity.startActivity(intent);
    }
    public static void toTemperatureInfoActivity(Activity activity,BLEDevice device) {
        Intent intent = new Intent(activity, TemperatureInfoActivity.class);
        intent.putExtra(TemperatureInfoActivity.EXTRA_DEVICE, device);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void toEditProfile( Activity activity, User user) {
        Intent intent = new Intent(activity, UserProfileEditActivity.class);
        intent.putExtra(UserListActivity.CURRENT_USER, user);
        activity.startActivityForResult(intent,2);

    }    public static void toFeedBack( Activity activity ) {
        Intent intent = new Intent(activity, FeedbackActivity.class);
        activity.startActivity(intent);

    }
    public  static  void toPermissionSetting(Activity activity){
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package",activity. getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName",activity. getPackageName());
        }
        activity.startActivity(localIntent);
    }
}

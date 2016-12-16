package com.cmax.bodysheild.util;

import android.app.Activity;
import android.graphics.Bitmap;

import com.cmax.bodysheild.bean.ble.BLEDevice;
import com.cmax.bodysheild.bean.cache.DeviceUser;
import com.cmax.bodysheild.bean.cache.User;

import java.util.List;

/**
 * Created by Administrator on 2016/12/16 0016.
 */

public class DataUtils {
    public static void addUserToSp(User user){
        List<User> users = getUserList();
        if (users.contains(user)){
            users.remove(user);
        }
        users.add(user);
        SharedPreferencesUtil.setList(Constant.USER_LIST, users);
    }

    public static   List<User> getUserList(){
      return SharedPreferencesUtil.getList(Constant.USER_LIST,
                User.class);
    }
    public static void addDeviceToSp(BLEDevice device, User user) {
        if (device==null)return;
        List<DeviceUser> deviceUsers = SharedPreferencesUtil.getList(Constant.DEVICE_USER_LIST,
                DeviceUser.class);
        DeviceUser temp = null;
        for (DeviceUser deviceuser : deviceUsers) {
            if (deviceuser.getAddress().equalsIgnoreCase(device.getAddress())) {
                temp = deviceuser;
                break;
            }
        }
        if (temp == null) {
            temp = new DeviceUser();
        }
        temp.setDeviceType(device.getDeviceType());
        temp.setAddress(device.getAddress());
        temp.setUserId(user.getId());
        deviceUsers.add(temp);
        SharedPreferencesUtil.setList(Constant.DEVICE_USER_LIST, deviceUsers);
        //保存设备名称
        SharedPreferencesUtil.setStringValue(device.getAddress(), user.getUserName());
    }

    public static void saveUserPortrait(Bitmap bitmap, Activity activity, User user) {
        if (bitmap != null) {
            String imagePath = PortraitUtil.writeBitmap(activity, bitmap, user.getImage());
            user.setImage(imagePath);
        }
    }
}

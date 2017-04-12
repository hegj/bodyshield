package com.cmax.bodysheild.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.util.List;

/**
 *
 * 需要添加依赖：
 *  compile 'com.mylhyl:acp:1.0.0'
 *
 * Created by Administrator on 2016/6/15 0015.
 */
public class PermissionUtils {


    private static boolean isCancle;
    private static String permissionDesc;

    /**
     *  compile 'com.mylhyl:acp:1.0.0'
     * @param listener
     * @param permission
     */
    private static void askPermission(final PermissionListener listener,String... permission){
        if (Build.VERSION.SDK_INT >= 23) {
            Acp.getInstance(UIUtils.getContext()).request(new AcpOptions.Builder()
                            .setPermissions(permission)
//                .setDeniedMessage()
//                .setDeniedCloseBtn()
//                .setDeniedSettingBtn()
//                .setRationalMessage()
//                .setRationalBtn()
                            .build(),
                    new AcpListener() {
                        @Override
                        public void onGranted() {
                            listener.onGranted();
                        }

                        @Override
                        public void onDenied(List<String> permissions) {
                            ToastUtils.showToast("被拒绝");
                         //   listener.onDenied(permissions);

                        }
                    });
        } else {
            // Pre-Marshmallow
            listener.onGranted();
        }
    }

    /**
     * group:android.permission-group.CALENDAR
     permission:android.permission.READ_CALENDAR
     permission:android.permission.WRITE_CALENDAR

     */
    public static void askCalendar(PermissionListener listener){
        askPermission(listener, Manifest.permission.READ_CALENDAR);
    }

    /**
     * group:android.permission-group.CAMERA
     permission:android.permission.CAMERA

     */
    public static void askCamera(PermissionListener listener){
        askPermission(listener, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static void askCamera(Activity activity,PermissionListener listener ){
        isCancle=true;
        permissionDesc = "选择头像需要您设置权限,否则选择不了头像"+"\n"+"请点击申请,在应用信息界面的权限管理中同意\"读写手机存储的权限\"";
        performRequestPermissions(activity,permissionDesc,2,listener,isCancle, Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE);
    }
    /**
     * group:android.permission-group.STORAGE
     permission:android.permission.READ_EXTERNAL_STORAGE
     permission:android.permission.WRITE_EXTERNAL_STORAGE

     */
    public static void askExternalStorage(PermissionListener listener){
        askPermission(listener, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
    public static void askExternalStorage(Activity activity,PermissionListener listener ){
        isCancle=true;
        permissionDesc = "选择头像需要您设置权限,否则选择不了头像"+"\n"+"请点击申请,在应用信息界面的权限管理中同意\"读写手机存储的权限\"";
        performRequestPermissions(activity,permissionDesc,2,listener,isCancle, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    /**
     * group:android.permission-group.PHONE
     *
     permission:android.permission.READ_CALL_LOG
     permission:android.permission.READ_PHONE_STATE
     permission:android.permission.CALL_PHONE
     permission:android.permission.WRITE_CALL_LOG
     permission:android.permission.USE_SIP
     permission:android.permission.PROCESS_OUTGOING_CALLS
     permission:com.android.voicemail.permission.ADD_VOICEMAIL

     */
    public static void askPhone(PermissionListener listener){
        askPermission(listener, Manifest.permission.READ_PHONE_STATE);
    }
    public static void askCallPhone(PermissionListener listener){
        askPermission(listener, Manifest.permission.CALL_PHONE);
    }

    /**
     * group:android.permission-group.SMS
     *
     permission:android.permission.READ_SMS
     permission:android.permission.RECEIVE_WAP_PUSH
     permission:android.permission.RECEIVE_MMS
     permission:android.permission.RECEIVE_SMS
     permission:android.permission.SEND_SMS
     permission:android.permission.READ_CELL_BROADCASTS

     */
    public static void askSms(PermissionListener listener){
        askPermission(listener, Manifest.permission.SEND_SMS);
    }




    /**
     * group:android.permission-group.LOCATION
     permission:android.permission.ACCESS_FINE_LOCATION
     permission:android.permission.ACCESS_COARSE_LOCATION
     */
    public static void askLocationInfo(Activity activity,PermissionListener listener){
        isCancle=true;
        permissionDesc = new StringBuilder().append("蓝牙连接设备需要您同意定位app的权限,请务必同意此权限,否则此app的所有功能将使用不了")
                .append("\n").append("请点击申请,在应用信息界面的权限管理中同意\"定位权限\"").append("\n").append("如点击取消,将会退出应用").toString() ;
        performRequestPermissions(activity, permissionDesc,1,listener,isCancle, Manifest.permission.ACCESS_FINE_LOCATION);
    }
    public static void askLocationInfo(PermissionListener listener){
        askPermission(listener, Manifest.permission.ACCESS_COARSE_LOCATION);
    }


    /**
     * group:android.permission-group.MICROPHONE
     permission:android.permission.RECORD_AUDIO

     */
    public static void askRecord(PermissionListener listener){
        askPermission(listener, Manifest.permission.RECORD_AUDIO);
    }


    /**
     * group:android.permission-group.SENSORS
     permission:android.permission.BODY_SENSORS

     */
    public static void askSensors(PermissionListener listener){
        //askPermission(listener, Manifest.permission.BODY_SENSORS);
    }

    /**
     * group:android.permission-group.CONTACTS
     permission:android.permission.WRITE_CONTACTS
     permission:android.permission.GET_ACCOUNTS
     permission:android.permission.READ_CONTACTS

     */
    public static void askContacts(PermissionListener listener){
        askPermission(listener, Manifest.permission.READ_CONTACTS);
    }

    public interface  PermissionListener{
        void onGranted();
        void onDenied(String[] permissions,String permissionDesc,boolean isCancle,int requestCode);


    }

    private static PermissionListener mListener;

    private static int mRequestCode;

    /**
     * 其他 Activity 继承 BaseActivity 调用 performRequestPermissions 方法
     *
     * @param desc        首次申请权限被拒绝后再次申请给用户的描述提示
     * @param permissions 要申请的权限数组
     * @param requestCode 申请标记值
     * @param listener    实现的接口
     */
    protected  static void performRequestPermissions(Activity activity,String desc, int requestCode, PermissionListener listener,boolean isCancle, String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return;
        }
        mRequestCode = requestCode;
        mListener = listener;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkEachSelfPermission(activity,permissions)) {
                // 检查是否声明了权限
                requestEachPermissions(activity,desc, permissions, requestCode,isCancle);
            } else {// 已经申请权限
                if (mListener != null) {
                    mListener.onGranted();
                }
            }
        } else {
            if (mListener != null) {
                mListener.onGranted();
            }
        }
    }

    /**
     * 申请权限前判断是否需要声明
     *  @param activity
     * @param desc
     * @param permissions
     * @param requestCode
     * @param isCancle
     */
    private static void requestEachPermissions(Activity activity, String desc, String[] permissions, int requestCode, boolean isCancle) {
        if (shouldShowRequestPermissionRationale(activity,permissions)) {// 需要再次声明
            showRationaleDialog(activity,desc, permissions, requestCode,isCancle);
        } else {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
    }

    /**
     * 弹出声明的 Dialog
     *
     * @param activity
     * @param desc
     * @param permissions
     * @param requestCode
     */
    private static void showRationaleDialog(final Activity activity, String desc, final String[] permissions, final int requestCode,boolean isCancle) {
        DialogUtils.showRequestPermissionDialog(activity,desc,requestCode,isCancle,permissions);

    }


    /**
     * 再次申请权限时，是否需要声明
     *
     *
     * @param activity
     * @param permissions
     * @return
     */
    private static boolean shouldShowRequestPermissionRationale(Activity activity, String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 检察每个权限是否申请
     *
     *
     * @param activity
     * @param permissions
     * @return true 需要申请权限,false 已申请权限
     */
    private static boolean checkEachSelfPermission(Activity activity, String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
/*

        AppOpsManager appOpsManager = (AppOpsManager)activity. getSystemService(Context.APP_OPS_SERVICE);
        int checkOp = appOpsManager.checkOp(AppOpsManager.OPSTR_FINE_LOCATION, Process.myUid(), activity.getPackageName());
        if (checkOp == AppOpsManager.MODE_IGNORED) {
            // 权限被拒绝了 .
        }
*/
        return false;
    }

    /**
     * 申请权限结果的回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == mRequestCode) {
            if (checkEachPermissionsGranted(grantResults)) {
                if (mListener != null) {
                    mListener.onGranted();
                }
            } else {// 用户拒绝申请权限
                if (mListener != null) {
                    mListener.onDenied(permissions,permissionDesc,isCancle,requestCode);
                }
            }
        }
    }

    /**
     * 检查回调结果
     *
     * @param grantResults
     * @return
     */
    private static boolean checkEachPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}

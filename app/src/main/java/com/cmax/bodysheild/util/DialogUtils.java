package com.cmax.bodysheild.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.activity.user.view.EditProfileDialog;
import com.cmax.bodysheild.listeners.SimpleDialogListeners;

import org.hybridsquad.android.library.CropParams;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/12/12 0012.
 */

public class DialogUtils {
    public static  void   createSimpleDialog(final Activity activity, String title, String message, String confirmText, String cancleText, final SimpleDialogListeners listeners){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title).setMessage(message).setPositiveButton(confirmText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listeners.confirm();
            }
        });
        if (!TextUtils.isEmpty(cancleText)) {
            builder.setNegativeButton(cancleText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listeners.cancel();
                    dialog.dismiss();
                }
            });
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public  static Dialog showChoosePortraitDialog(final Activity activity) {
        View view =activity. getLayoutInflater().inflate(R.layout.photo_choose_dialog, null);
        final Dialog  dialog = new Dialog(activity, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        Point p = new Point();
        activity.  getWindowManager().getDefaultDisplay().getSize(p);
        wl.y = p.y;
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        //
        ButterKnife.findById(view, R.id.galleryBtn) .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            /*    mCropParams.enable = true;
                mCropParams.compress = true;
                Intent intent = CropHelper.buildGalleryIntent(mCropParams);
                activity.  startActivityForResult(intent, CropHelper.REQUEST_CROP);*/
                CropUtils.pickFromGallery(activity,CropUtils.RATIO_4_3);
                dialog.dismiss();
            }
        });
        ButterKnife.findById(view, R.id.cameraBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  mCropParams.enable = true;
                mCropParams.compress = true;
                Intent intent = CropHelper.buildCameraIntent(mCropParams);
                activity. startActivityForResult(intent, CropHelper.REQUEST_CAMERA);*/
                CropUtils.pickFromCamera(activity,CropUtils.RATIO_4_3);
                dialog.dismiss();
            }
        });
        ButterKnife.findById(view, R.id.canelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        return dialog;
    }

    public static ProgressDialog showProgressDialog(Activity activity,String text){
        ProgressDialog   loginDialog = new ProgressDialog(activity );
        loginDialog.setMessage(text);
        return  loginDialog;
    }
    public  static  Dialog showRequestPermissionDialog(final Activity activity, String desc, final int requestCode,boolean isCancle , final String...permissions){
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        AlertDialog alertDialog = builder.setTitle("权限申请")
                .setMessage(desc)
                .setPositiveButton("申请", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ActivityCompat.requestPermissions(activity, permissions, requestCode);
                        IntentUtils.toPermissionSetting(activity);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();

        alertDialog .setCancelable(isCancle);
        alertDialog.setCanceledOnTouchOutside(isCancle);
        alertDialog  .show();
        return  alertDialog;
    }

    public static Dialog showEditProfileDialog(Activity context ,int profileType) {
        EditProfileDialog dialog = new EditProfileDialog(context);
        return  null;
    }
}

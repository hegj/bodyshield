package com.cmax.bodysheild.util;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.listeners.CropPickListeners;
import com.orhanobut.logger.Logger;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2016/12/19 0019.
 */

public class CropUtils {
    public static final int RATIO_1_1 = 0x04;
    public static final int RATIO_4_3 = 0x05;
    public static final int RATIO_16_9 = 0x06;
    public static final int RATIO_19_13 = 0x07;
    private static final int RATIO_0_0 = 0x08;
    private   static   int mRatio;
    public static final int REQUEST_SELECT_PICTURE = 0x01;
    private static final int REQUEST_CAMERA = 0x02;

    private static  Uri uri;
    private static int resultSizeWidth;
    private static int resultSizeheight;
    private static Dialog permisstionDialog;

    public static void pickFromGallery(final Activity activity, int ratio){
       mRatio =ratio;
            PermissionUtils.askExternalStorage(activity, new PermissionUtils.PermissionListener() {
                @Override
                public void onGranted() {
                    setPermissionDialogDismiss();
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    activity. startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.label_select_picture)), REQUEST_SELECT_PICTURE);
                }

                @Override
                public void onDenied(String[] permissions, String permissionDesc, boolean isCancle, int requestCode) {
                       setPermissionDialogDismiss();
                        permisstionDialog = DialogUtils.showRequestPermissionDialog(activity, permissionDesc, requestCode, isCancle, permissions);
                }
            });

    }

    public static void pickFromCamera(final Activity activity, int ratio){
        mRatio=ratio;
            PermissionUtils.askCamera(activity,new PermissionUtils.PermissionListener() {
                @Override
                public void onGranted() {
                    Uri cacheUri = getCameraCacheUri();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            .putExtra(MediaStore.EXTRA_OUTPUT, cacheUri);
                    activity.startActivityForResult(intent, REQUEST_CAMERA);
                }

                @Override
                public void onDenied(String[] permissions, String permissionDesc, boolean isCancle, int requestCode) {
                    setPermissionDialogDismiss();
                    permisstionDialog=DialogUtils.showRequestPermissionDialog(activity,permissionDesc,requestCode,isCancle,permissions);
                }
            });
    }


    public static void handleResult(Activity context, CropPickListeners cropPickListeners, int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_SELECT_PICTURE) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    startCropActivity(context, data.getData());
                } else {
                    Toast.makeText(context, "Cannot retrieve selected image", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                cropPickListeners.cropResult(data);
            } else if (requestCode == REQUEST_CAMERA) {
                startCropActivity(context, uri);
            }
        }
        if (resultCode == UCrop.RESULT_ERROR) {
            cropPickListeners.cropError(data);
        }
    }
    private static   void startCropActivity(Activity context, Uri sourceUri) {
        Uri mDestinationUri = getCameraCacheUri();
        UCrop uCrop = UCrop.of(sourceUri, mDestinationUri);

        uCrop = basisConfig(uCrop);
        uCrop = advancedConfig(uCrop);

        uCrop.start(context);
    }

    private static   UCrop basisConfig(@NonNull UCrop uCrop) {
        switch (mRatio) {
            case RATIO_1_1:
                uCrop = uCrop.withAspectRatio(1, 1);

                break;
            case RATIO_4_3:
                uCrop = uCrop.withAspectRatio(4, 3);
                break;
            case RATIO_16_9:
                uCrop = uCrop.withAspectRatio(16, 9);
                break;
            case RATIO_19_13:
                uCrop = uCrop.withAspectRatio(19, 13);
                break;
            case RATIO_0_0:
                uCrop = uCrop.withAspectRatio(0, 0);
                break;

            default:
                uCrop = uCrop.withAspectRatio(1, 1);
                break;
        }

        if (resultSizeWidth != 0 && resultSizeheight != 0) {
            uCrop = uCrop.withMaxResultSize(resultSizeWidth, resultSizeheight);
        } else {
            uCrop = uCrop.withMaxResultSize(1080, 1920);
        }
        return uCrop;
    }


    private  static  Uri getCameraCacheUri() {
        File picture = FileUtils.getAppPictureDir("Picture");
        String name = String.format("imagecrop-%d.jpg", System.currentTimeMillis());
        uri = Uri
                .fromFile(picture)
                .buildUpon()
                .appendPath(name)
                .build();

        String filePath = uri.toString();
        int first = filePath.indexOf("/");
        int second = filePath.indexOf("/", first + 1);
        second = second + first;
        String replace = second == filePath.length() - 1 ? filePath.substring(0, second) : filePath.substring(0, second) + filePath.substring(second + 1, filePath.length());

        Logger.e("create new uri:" + replace);
        return uri;
    }
    private static   UCrop advancedConfig(@NonNull UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(90);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false);
        return uCrop.withOptions(options);
    }
    public  static  void setPermissionDialogDismiss(){
        if (permisstionDialog!=null && permisstionDialog.isShowing())permisstionDialog.dismiss();
        permisstionDialog=null;
    }
}

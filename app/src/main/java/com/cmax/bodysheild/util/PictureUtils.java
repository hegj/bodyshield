package com.cmax.bodysheild.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import static com.cmax.bodysheild.util.FileUtils.getCacheFile;
import static com.cmax.bodysheild.util.FileUtils.getDiskCacheDir;

/**
 * Created by Administrator on 2017/3/27 0027.
 */

public class PictureUtils {
    public static final int REQUEST_CAMERA = 0x02;
    public static final int CROP_PHOTO = 0x03;
    private static Uri uri;
    private static File cameraFile;
    private static String cachPath;
    private static File cacheFile;


/*
    public static void handleResult(Activity a, ImagePickerSingleLisenter cropPickListeners, int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CAMERA) {
            startPhotoZoom(a, cameraFile, 500);
        } else if (requestCode == CROP_PHOTO) {
            try {
                if (resultCode == RESULT_OK) {
                    cropPickListeners.getSingleImage(cachPath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
*/



    public static void startActionCapture(Activity activity, int requestCode) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getUriForFile(activity));
        activity.startActivityForResult(intent, requestCode);
    }

    public static void openCamera(Activity activity) {
        cachPath= getDiskCacheDir()+ "/crop_image.jpg";
        cacheFile =getCacheFile(new File(getDiskCacheDir()),"crop_image.jpg");
        cameraFile = getCacheFile(new File(getDiskCacheDir()), "avatar.jpg");
        if (cameraFile.exists()) {
            cameraFile.delete();
        }
        try {
            cameraFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(cameraFile);
        } else {

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(activity, "cn.natrip.android.civilizedcommunity.fileprovider", cameraFile);

        }
        // 启动相机程序
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent, REQUEST_CAMERA);
    }

    /**
     * 剪裁图片
     */
    private static void startPhotoZoom(Activity activity, File file, int size) {
        Log.i("TAG", getImageContentUri(activity, file) + "裁剪照片的真实地址");
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(getImageContentUri(activity, file), "image/*");//自己使用Content Uri替换File Uri
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 600);
            intent.putExtra("outputY", 600);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cacheFile));//定义输出的File Uri
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true);
            activity.startActivityForResult(intent, CROP_PHOTO);
        } catch (ActivityNotFoundException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }


    public static Uri getUriForFile(Activity context) {
    /*    if (context == null) {
            throw new NullPointerException();
        }

        //  File picture = FileUtil.getAppPictureDir("picture");
        String name = String.format("imagecrop%d.jpg", System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context, "cn.natrip.android.civilizedcommunity.fileprovider", picture);
        } else {
            uri = Uri
                    .fromFile(picture)
                    .buildUpon()
                    .appendPath(name)
                    .build();
        }*/
        return uri;
    }

}

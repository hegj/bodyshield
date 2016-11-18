package com.cmax.bodysheild.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cmax.bodysheild.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by token on 15/11/21.
 * 头像工具类
 */
public class PortraitUtil {
    public static String writeBitmap(Context context, Bitmap bitmap, String oldFile){
        String fileName = System.currentTimeMillis()+".jpg";
        if(oldFile!=null&&!"".equals(oldFile)){
            context.deleteFile(oldFile);
        }
        FileOutputStream out = null;
        try {
            out =  context.openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG,90,out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(out!=null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return fileName;
    }

    public static Bitmap getBitmap(Context context, String fileName){

        if(fileName==null||"".equals(fileName)){
            return getDefaultPortrait(context);
        }
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(context.openFileInput(fileName));
        } catch (FileNotFoundException e) {
            return getDefaultPortrait(context);
        }
        return bitmap;
    }

    public static Bitmap getCameraImageBitmap(Context context){
        return BitmapFactory.decodeStream(context.getResources().openRawResource(R.mipmap.photo));
    }

    public static Bitmap getDefaultPortrait(Context context){
        return BitmapFactory.decodeStream(context.getResources().openRawResource(R.mipmap.people));
    }
}

package com.cmax.bodysheild.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2016/12/19 0019.
 */

public class FileUtils {

    public  static File getAppPictureDir(String fileName){
        File appRootDir = getAppRootDir();
        File file = new File(appRootDir,fileName);
        if (!file.exists())file.mkdirs();
        return file;
    }
    public static  File getAppRootDir(){
        return getSdRootDir("bodyshield");
    }

    public static File getSdRootDir(String fileName){
        File dir;
        if (Environment. MEDIA_MOUNTED.equals(Environment.getExternalStorageState())||!Environment.isExternalStorageRemovable()){
            dir = Environment.getExternalStorageDirectory();
            if (dir==null)dir=UIUtils.getContext().getCacheDir();
        }else {
            dir=UIUtils.getContext().getCacheDir();
        }
        dir= new File(dir.getAbsolutePath(),fileName);
        if (!dir.exists())dir.mkdirs();
        return  dir;
    }
}

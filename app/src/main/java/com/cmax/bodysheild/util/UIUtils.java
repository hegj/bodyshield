package com.cmax.bodysheild.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.TypedValue;

import com.cmax.bodysheild.AppContext;
import com.cmax.bodysheild.R;

import java.util.Locale;

/**
 * Created by Administrator on 2016/12/7 0007.
 */

public class UIUtils {


    /**
     * 得到上下文
     */
    public static Context getContext() {
        return AppContext.getContext();
    }

    /**
     * 得到Resouce对象
     */
    public static Resources getResource() {
        return getContext().getResources();
    }  public static int getResourceColor(int c) {
        return getContext().getResources().getColor(c);
    }

    public static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext(). getResources().getDisplayMetrics());
    }
    public static Handler getMainThreadHandler() {
        return AppContext.getMainHandler();
    }


    public static String getString(int res) {
        return   getContext().getString(res);
    }

    public static  int getUserId(){
     return    AppContext.getUserId();
    }

    public static void setUserId(int id) {
        AppContext.setUserId(id);
    }

    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }
}

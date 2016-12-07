package com.cmax.bodysheild.util;

import android.content.Context;
import android.content.res.Resources;

import com.cmax.bodysheild.AppContext;

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
    }
}

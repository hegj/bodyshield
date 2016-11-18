package com.cmax.bodysheild.util;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by allen on 15-9-20.
 */
public class LogUtil {

    public static final int VERBOSE = 1;

    public static final int DEBUG = 2;

    public static final int INFO = 3;

    public static final int WARN = 4;

    public static final int ERROR = 5;

    public static final int LEVEL = VERBOSE;

    public static void v(String tag, String msg) {
        if (LEVEL <= VERBOSE) {
            if (StringUtils.isEmpty(msg)) {
                msg = StringUtils.EMPTY;
            }
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (LEVEL <= DEBUG) {
            if (StringUtils.isEmpty(msg)) {
                msg = StringUtils.EMPTY;
            }
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (LEVEL <= INFO) {
            if (StringUtils.isEmpty(msg)) {
                msg = StringUtils.EMPTY;
            }
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (LEVEL <= WARN) {
            if (StringUtils.isEmpty(msg)) {
                msg = StringUtils.EMPTY;
            }
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (LEVEL <= ERROR) {
            if (StringUtils.isEmpty(msg)) {
                msg = StringUtils.EMPTY;
            }
            Log.e(tag, msg);
        }
    }
}

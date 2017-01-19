package com.cmax.bodysheild.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2017/1/19 0019.
 */

public class DateUtils {
    public  static String getFormatTime(String format){
        SimpleDateFormat simpleDateFormat     = new SimpleDateFormat(format, Locale.getDefault());
       return simpleDateFormat.format(new Date());

    }
}

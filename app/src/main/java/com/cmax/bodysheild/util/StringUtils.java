package com.cmax.bodysheild.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/12/16 0016.
 */

public class StringUtils {
    /**
     * 利用正则表达式判断字符串是否是数字
     * @param str
     * @return
     */
    public static boolean isNumber(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
    /**
     * 是否是电话号码
     */
    public static boolean isPhoneNumber(String emailString) {
        //String format = "^(1(([357][0-9])|(47)|[8][0126789]))\\d{8}$";
        String format = "^[1][3,4,5,7,8][0-9]{9}$";
        return isMatch(format, emailString);
    }
    public static boolean isMatch(String regex, String string) {

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }
}

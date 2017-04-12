package com.cmax.bodysheild.api;

/**
 * Created by Administrator on 2016/11/24 0024.
 */
public class Url {
    public static final String BASE_URL = "http://api.apexto.com:8080/bodyshield/";
    public static final String LOGIN = "user/login.json";
    public static final String REGISTER = "user/register.json";
    public static final String IS_REGISTER = "user/checkName.json";
    public static final String CHANGE_PASSWORD = "user/modify/password.json";
    public static    String UPLOAD_HEADIMAGE ="user/upload/headImg.json";
    public static final String FEEDBACK ="user/feedback.json";
    public static final String UPLOAD_TEMPERATURE = "temperature/upload/data.json";
    public static final String DOWNLOAD_TEMPERATURE = "temperature/load/data.json";
    public static final String THIRD_LOGIN="user/third/login.json";
    public static final String SEND_CAPTCHA="captcha/send/v1.json";
    public static final String SEND_CAPTCHA_BY_USERNAME="captcha/sendByUserName/v1.json";
}

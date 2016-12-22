package com.cmax.bodysheild.http.rxexception;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.util.UIUtils;

import java.net.ConnectException;
import java.net.SocketTimeoutException;



/**
 * Created by Administrator on 2016/11/24 0024.
 */
public class ErrorManager {

    public static void setCode(int code) {
        ErrorManager.code = code;
    }

    private static int code=0;

    public ErrorManager() {
    }

    /**
     * 对错误数据进行处理，返回对应字符串提示信息
     *
     * @param e 错误数据接口
     * @return 返回对应错误提示信息
     */
    public static String handleError(ErrorBundle e) {
        e.getException().printStackTrace();
        String message;
       // if (!NetUtils.isNetworkConnected()) {
        if (!true) {
            message = UIUtils.getString(R.string.network_interruption_error_message);
        } else if (e.getException() instanceof SocketTimeoutException) {
            message =   UIUtils.getString(R.string.network_timeout_error_message);
        } else if (e.getException() instanceof ConnectException) {
            message =  UIUtils.getString(R.string.network_unusual_error_message);
        } else if (e.getException() instanceof NetworkConnectionException) {
            message = UIUtils.getString(R.string.network_interruption_error_message);
        } else if (e.getException() instanceof ServerException) {
            code = ((ServerException) e.getException()).getCode();
            //在这里你可以获取code来判断是什么类型，进行相应处理，比如token失效了可以实现跳转到登录页面
            message = e.getMessage();

        } else {
            message = UIUtils.getString(R.string.network_server_error_message);
        }
        return message;
    }
}

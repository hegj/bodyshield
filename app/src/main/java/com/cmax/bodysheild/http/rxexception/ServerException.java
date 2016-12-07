package com.cmax.bodysheild.http.rxexception;

/**
 * Created by Administrator on 2016/11/24 0024.
 */
public class ServerException extends Exception {

    private String message;
    private   int code;

    public ServerException(int code, String message) {
        super(message);
        this.code=code;
        this.message=message;
    }


    public int getCode() {
        return code;
    }

}

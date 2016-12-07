package com.cmax.bodysheild.base.bean;

/**
 * Created by Administrator on 2016/11/24 0024.
 */
public class BaseRequestData<T> {
    public  int error_code;
    public  T data;
    public boolean success;
    public String message;
}

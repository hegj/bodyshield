package com.cmax.bodysheild.http.rxexception;

/**
 * Created by Administrator on 2016/11/24 0024.
 */
public interface ErrorBundle {
    Exception getException();
    String getMessage();
}

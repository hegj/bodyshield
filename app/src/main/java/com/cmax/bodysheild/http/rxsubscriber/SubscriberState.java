package com.cmax.bodysheild.http.rxsubscriber;

/**
 * Created by Administrator on 2016/11/24 0024.
 */
public interface SubscriberState<T> {
    public void _onStart();

    public   void _onError();

    public   void _onNext(T t);

    public   void _onCompleted();
}

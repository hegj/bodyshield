package com.cmax.bodysheild.http;

import com.cmax.bodysheild.base.bean.BaseRequestData;
import com.cmax.bodysheild.http.rxexception.NetworkConnectionException;
import com.cmax.bodysheild.http.rxexception.ServerException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.orhanobut.logger.Logger;


import org.json.JSONObject;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by Administrator on 2016/11/24 0024.
 */
public class RxJavaHttpHelper {
     // 将访问网络成功拿到的observable对象预处理通过compose方法进行转换
        public  static <T> Observable.Transformer<BaseRequestData<T>,T>handleResult(){

            Observable.Transformer<BaseRequestData<T>,T> tTransformer = new Observable.Transformer<BaseRequestData<T>, T>() {
                @Override
                public Observable<T> call(Observable<BaseRequestData<T>> baseRequestDataObservable) {
                    final Observable<T> observable = baseRequestDataObservable.flatMap(new Func1<BaseRequestData<T>, Observable<T>>() {
                        @Override
                        public Observable<T> call(BaseRequestData<T> tBaseRequestData) {
                            if (tBaseRequestData==null){
                                 return Observable.error(new NetworkConnectionException(""));
                            }else if (tBaseRequestData.code ==0){
                                //创建一个观察者
                                if (tBaseRequestData==null){
                                    Logger.d("........");
                                }
                                return createObservable(tBaseRequestData.data);
                            }else {
                                return Observable.error(new ServerException(tBaseRequestData.code,tBaseRequestData.message));
                            }
                        }
                    });
                    return observable;
                }
            };
            return  tTransformer;
        }
        
    private static <T> Observable<T> createObservable(final T data) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {

                    subscriber.onNext( data);
                    subscriber.onCompleted();
                }catch (Exception e){
                    subscriber.onError(e);
                }

            }
        });
    }


}

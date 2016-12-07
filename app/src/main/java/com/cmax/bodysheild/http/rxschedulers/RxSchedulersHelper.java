package com.cmax.bodysheild.http.rxschedulers;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/11/24 0024.
 */
public class RxSchedulersHelper  {
    private  static  final Observable.Transformer ioTransformer= new Observable.Transformer() {
        @Override
        public Object call(Object o) {
            return ((Observable)o).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        }
    };
    public  static <T> Observable.Transformer<T,T>applyIoTransformer(){
        return ioTransformer;
    }
}

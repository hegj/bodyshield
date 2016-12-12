package com.cmax.bodysheild.activity.login;

import com.cmax.bodysheild.http.HttpMethods;
import com.cmax.bodysheild.http.RxJavaHttpHelper;
import com.cmax.bodysheild.http.rxschedulers.RxSchedulersHelper;

import rx.Observable;

/**
 * Created by Administrator on 2016/11/23 0023.
 */
public class LoginModel {
    public  void getUserProfile(int uid){

    }

    public   Observable<Object>   login(String userName, String password){
        return HttpMethods.getInstance().apiService.login(userName, password)
                        .compose(RxJavaHttpHelper.<Object>handleResult())
                .compose( RxSchedulersHelper.applyIoTransformer());
    }
}

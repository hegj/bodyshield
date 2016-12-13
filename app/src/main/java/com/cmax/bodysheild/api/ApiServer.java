package com.cmax.bodysheild.api;

import com.cmax.bodysheild.base.bean.BaseRequestData;
import com.cmax.bodysheild.bean.UserProfileInfo;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Administrator on 2016/11/24 0024.
 */
public interface ApiServer {
    @FormUrlEncoded
    @POST( Url.LOGIN)
    Observable<BaseRequestData<Object>> login(@Field("name") String username, @Field("password") String password);
    @FormUrlEncoded
    @POST( Url.REGISTER)
    Observable<BaseRequestData<UserProfileInfo>> register(@Field("name") String username, @Field("password") String password);
    @FormUrlEncoded
    @POST( Url.IS_REGISTER)
    Observable<BaseRequestData> isUserRegister(@Field("name") String username);
}

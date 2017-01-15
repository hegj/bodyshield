package com.cmax.bodysheild.api;

import com.cmax.bodysheild.base.bean.BaseRequestData;
import com.cmax.bodysheild.bean.UserProfileInfo;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * Created by Administrator on 2016/11/24 0024.
 */
public interface ApiServer {
    @FormUrlEncoded
    @POST( Url.LOGIN)
    Observable<BaseRequestData<UserProfileInfo>> login(@Field("name") String username, @Field("password") String password);
    @FormUrlEncoded
    @POST( Url.REGISTER)
    Observable<BaseRequestData<UserProfileInfo>> register(@FieldMap Map<String,String> map);
    @FormUrlEncoded
    @POST( Url.IS_REGISTER)
    Observable<BaseRequestData> isUserRegister(@Field("name") String username);
    @FormUrlEncoded
    @POST( Url.FEEDBACK)
    Observable<BaseRequestData<Object>> feedBack(@FieldMap Map<String, String>map);
    @Multipart
    @POST(Url.UPLOAD_HEADIMAGE)
    Observable<BaseRequestData> updateImage(@Part MultipartBody.Part  file,@Field("uid") String uid);

}

package com.cmax.bodysheild.http;

import com.cmax.bodysheild.api.ApiServer;
import com.cmax.bodysheild.api.Url;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.concurrent.TimeUnit;


import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Administrator on 2016/11/24 0024.
 */
public class HttpMethods {

    public ApiServer apiService;
    private Retrofit retrofit;


    private static class SingleHttpMethods {
        private static final HttpMethods INSTANCE = new HttpMethods();
    }

    //获取单例
    public static HttpMethods getInstance() {
        return SingleHttpMethods.INSTANCE;
    }

    private HttpMethods() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(Url.BASE_URL)
                .client(getOkHttpClient())
                .build();
        apiService = retrofit.create(ApiServer.class);
    }

    private OkHttpClient getOkHttpClient() {
        //日志显示级别
        HttpLoggingInterceptor.Level level= HttpLoggingInterceptor.Level.BODY;
        //新建log拦截器
        HttpLoggingInterceptor loggingInterceptor=new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Logger.d(message);
            }
        });
        loggingInterceptor.setLevel(level);
        //定制OkHttp
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient
                .Builder();
        //OkHttp进行添加拦截器loggingInterceptor
        httpClientBuilder.addInterceptor(loggingInterceptor);
        httpClientBuilder.connectTimeout(12, TimeUnit.SECONDS);
        httpClientBuilder.addNetworkInterceptor(new StethoInterceptor());
        return httpClientBuilder.build();
    }

    public RequestBody toTextRequestBody(String s) {
        return RequestBody.create(MediaType.parse("text/plain"), s);
    }

    public RequestBody toImageRequestBody(File file) {
        return RequestBody.create(MediaType.parse("image/jpg"), file);
    }
    public void  thirdLogin(String token ,String uid ,String type,String name){

    }
}

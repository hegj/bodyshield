package com.cmax.bodysheild.http;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cmax.bodysheild.bean.UserProfileInfo;
import com.cmax.bodysheild.bean.ble.BLEDevice;
import com.cmax.bodysheild.bean.cache.User;
import com.cmax.bodysheild.util.DataUtils;
import com.cmax.bodysheild.util.IntentUtils;
import com.cmax.bodysheild.util.PortraitUtil;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Subscriber;

/**
 * Created by Administrator on 2017/1/19 0019.
 */

public class OkHttpApi {


    private final OkHttpClient okHttpClient;

    private static class SingleHttpMethods {
        private static final OkHttpApi INSTANCE = new OkHttpApi();
    }

    //获取单例
    public static OkHttpApi getInstance() {
        return OkHttpApi.SingleHttpMethods.INSTANCE;
    }

    private OkHttpApi() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(12, TimeUnit.SECONDS);
        httpClientBuilder.addNetworkInterceptor(new StethoInterceptor());
        okHttpClient = httpClientBuilder.build();
    }

    Bitmap bmp = null;

    public Bitmap requestBitMap(final UserProfileInfo info, final Activity activity, final BLEDevice device, final Subscriber<UserProfileInfo> subscriber, final ProgressDialog loginDialog) {
        //创建OkHttpClient针对某个url的数据请求
        Request request = new Request.Builder().url(info.getHeadImg()).build();

        Call call = okHttpClient.newCall(request);

        //请求加入队列
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //此处处理请求失败的业务逻辑
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //response的body是图片的byte字节
                byte[] bytes = response.body().bytes();
                //response.body().close();
                //把byte字节组装成图片
                bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                User user = new User();
                if (bmp != null) {
                    String imagePath = PortraitUtil.writeBitmap(activity, bmp, "");
                    user.setImage(imagePath);
                }
                user.setId(info.getId() + "");
                user.setUserName(info.getName());
                user.setPassword(info.getPassword());
                user.setPhone(info.getMobile());
                DataUtils.addDeviceToSp(device, user);
                DataUtils.addUserToSp(user);
                loginDialog.dismiss();
                subscriber.onCompleted();
                IntentUtils.toTemperatureInfoActivity(activity,device);
            }
        });
        return bmp;
    }
}

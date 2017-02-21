package com.cmax.bodysheild.util;

import android.app.Activity;
import android.app.ProgressDialog;

import com.cmax.bodysheild.activity.login.LoginModel;
import com.cmax.bodysheild.activity.login.LoginPresenter;
import com.cmax.bodysheild.http.HttpMethods;
import com.orhanobut.logger.Logger;
import com.umeng.socialize.Config;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

/**
 * Created by Administrator on 2017/1/17 0017.
 */

public class UMUtils {
    private static final int THIRD_LOGIN_TYPE_WECHAT = 1;
    private static final int THIRD_LOGIN_TYPE_QQ = 2;
    private static final int THIRD_LOGIN_TYPE_FACEBOOK = 3;
    public static Activity mActivity;
    private UMShareAPI mShareAPI;
    private int thirdLoginType;
    private LoginPresenter loginPresenter;
    private ProgressDialog thirdLoginProgressDialog;

    private UMUtils() {
        mShareAPI = UMShareAPI.get(mActivity);
    }

    public void setLoginPresenter(LoginPresenter loginPresenter) {
        this.loginPresenter = loginPresenter;
    }

    private static class SingleUMUtils {
        private static final UMUtils INSTANCE = new UMUtils();
    }

    //获取单例
    public static UMUtils getInstance(Activity activity) {
        mActivity = activity;
        return UMUtils.SingleUMUtils.INSTANCE;
    }



    public void thirdLoginOfQQ() {
        initThirdLoginProgressDialog();
        thirdLoginType = THIRD_LOGIN_TYPE_QQ;
        mShareAPI.getPlatformInfo(mActivity, SHARE_MEDIA.QQ, umAuthListener);
    }

    public void thirdLoginOfWeChat() {
        initThirdLoginProgressDialog();
        thirdLoginType = THIRD_LOGIN_TYPE_WECHAT;
        mShareAPI.getPlatformInfo(mActivity, SHARE_MEDIA.WEIXIN, umAuthListener);
    }

    public void thirdLoginOfFaceBook() {

        thirdLoginType = THIRD_LOGIN_TYPE_FACEBOOK;
        mShareAPI.getPlatformInfo(mActivity, SHARE_MEDIA.FACEBOOK, umAuthListener);
    }

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            if (thirdLoginProgressDialog!=null)
            thirdLoginProgressDialog.dismiss();
            switch (thirdLoginType) {
                case THIRD_LOGIN_TYPE_QQ:
                    parsedQQLoginData(data);
                    break;
                case THIRD_LOGIN_TYPE_WECHAT:
                    parsedWeChatLoginData(data);
                    break;
                case THIRD_LOGIN_TYPE_FACEBOOK:
                    parsedFaceBookLoginData(data);
                    break;
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            ToastUtils.showFailToast("授权失败,请点击重试");
            if (thirdLoginProgressDialog!=null)
                thirdLoginProgressDialog.dismiss();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            if (thirdLoginProgressDialog!=null)
                thirdLoginProgressDialog.dismiss();
        }
    };

    private void parsedQQLoginData(Map<String, String> data) {
        String openid = data.get("openid");
        String name = data.get("name");
        String uid = data.get("uid");
        String iconurl = data.get("iconurl");
        // "iconurl" -> "http://q.qlogo.cn/qqapp/1105901585/5C1C912FAD3D0E187469027F8A05B324/100"
        if (loginPresenter != null) {
            loginPresenter.startThirdLogin(openid, uid, thirdLoginType + "", name, iconurl);
        }

    }

    private void parsedWeChatLoginData(Map<String, String> data) {
        String accessToken = data.get("accessToken");
        String name = data.get("name");
        String uid = data.get("uid");
        String openid = data.get("openid");
        String iconurl = data.get("profile_image_url");
        HttpMethods.getInstance().thirdLogin(accessToken, uid, thirdLoginType + "", name);
        if (loginPresenter != null) {
            loginPresenter.startThirdLogin(openid, uid, thirdLoginType + "", name, iconurl);
        }
    }

    private void parsedFaceBookLoginData(Map<String, String> data) {
        String name = data.get("name");
        String uid = data.get("uid");
        HttpMethods.getInstance().thirdLogin("", uid, thirdLoginType + "", name);
    }

    public void initThirdLoginProgressDialog() {
        Config.dialogSwitch = false;
        if (thirdLoginProgressDialog == null) {
            thirdLoginProgressDialog = DialogUtils.showProgressDialog(mActivity, "授权中,请稍后");
        }
        try {
            thirdLoginProgressDialog.show();
        }catch (Exception e){

        }

    }
}

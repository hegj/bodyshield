package com.cmax.bodysheild.activity.login;


import android.graphics.Bitmap;

import com.cmax.bodysheild.base.view.IStateView;

/**
 * Created by Administrator on 2016/11/23 0023.
 */
public interface ILoginView extends IStateView {
    void setUserName(String userName);
    String getUserName();
    void setPassWord(String passWord);
    String getPassWord();
    void showProgressDialog();
    void hideProgressDialog();
    void setUserNameError(String error);
    void setPasswordError(String error);
    void setPortraitBitmap(Bitmap bitmap);
}

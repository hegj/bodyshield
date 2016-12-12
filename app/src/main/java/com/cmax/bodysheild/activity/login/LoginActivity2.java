package com.cmax.bodysheild.activity.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.activity.TemperatureInfoActivity;
import com.cmax.bodysheild.activity.UserListActivity;
import com.cmax.bodysheild.base.BaseMvpActivity;
import com.cmax.bodysheild.bean.ble.BLEDevice;
import com.cmax.bodysheild.bean.cache.User;
import com.cmax.bodysheild.inject.component.ActivityComponent;
import com.cmax.bodysheild.util.LogUtil;
import com.cmax.bodysheild.util.PortraitUtil;
import com.cmax.bodysheild.util.ToastUtils;

import org.hybridsquad.android.library.CropHandler;
import org.hybridsquad.android.library.CropHelper;
import org.hybridsquad.android.library.CropParams;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/11/23 0023.
 */
public class LoginActivity2 extends BaseMvpActivity<LoginPresenter> implements ILoginView{

    @Bind(R.id.userName)
    EditText userName;
    @Bind(R.id.portrait)
    ImageView portrait;
    @Bind(R.id.userPassword)
    EditText userPassword;
    private ProgressDialog loginDialog;
    private LoginPresenter loginPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void setActivityComponent(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        loginDialog = new ProgressDialog(this);
        loginDialog.setMessage("登陆中,请稍后");
        loginPresenter = new LoginPresenter();
        loginPresenter.setActivity(this);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        loginPresenter.initIntentData(extras);

    }

    @Override
    protected void initEvent(Bundle savedInstanceState) {
        super.initEvent(savedInstanceState);
    }

    @Override
    public void setUserName(String name) {
        userName.setText(name);
    }

    @Override
    public String getUserName() {
        return userName.getText().toString().trim();
    }

    @Override
    public void setPassWord(String passWord) {
        userPassword .setText(passWord);
    }

    @Override
    public String getPassWord() {
        return userPassword.getText().toString().trim();
    }

    @Override
    public void showProgressDialog() {
        loginDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (loginDialog.isShowing()) loginDialog.dismiss();
    }

    @Override
    public void setUserNameError(String error) {
        ToastUtils.showFailToast(error);
    }

    @Override
    public void setPasswordError(String error) {
        ToastUtils.showFailToast(error);
    }

    @Override
    public void setPortraitBitmap(Bitmap bitmap) {
        portrait.setImageBitmap(bitmap);
    }

    @OnClick(R.id.tvRegister)
    void register(View view){
            loginPresenter.startLogin();
    }
    @OnClick(R.id.tvLogin)
    void login(View view){
            loginPresenter.startLogin();
    }
    @OnClick(R.id.backBtn)
    void finishActivity(View view){
        finish();
    }
    @Override
    protected void onDestroy() {
        CropHelper.clearCacheDir();
        super.onDestroy();
    }
    @OnClick(R.id.portrait)
    void setPortrait(){
      loginPresenter.showChoosePortraitDialog();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CropHelper.handleResult(presenter, requestCode, resultCode, data);
        if (requestCode == 1) {
        }
    }
}
package com.cmax.bodysheild.activity.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.base.BaseActivity;
import com.cmax.bodysheild.inject.component.ActivityComponent;
import com.cmax.bodysheild.util.DialogUtils;
import com.cmax.bodysheild.util.ToastUtils;
import com.cmax.bodysheild.util.UIUtils;
import com.cmax.bodysheild.util.UMUtils;
import com.umeng.socialize.UMShareAPI;

import org.hybridsquad.android.library.CropHelper;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/11/23 0023.
 */
public class LoginActivity2 extends BaseActivity<LoginPresenter> implements ILoginView {

    @Bind(R.id.userName)
    EditText userName;
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
        loginDialog = DialogUtils.showProgressDialog(this, UIUtils.getString(R.string.login_loading));
        loginPresenter = new LoginPresenter();
        loginPresenter.setActivity(this);
        basePresenter = loginPresenter;
        UMUtils.mActivity = this;
        UMUtils.getInstance().setLoginPresenter(loginPresenter);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        //   KeyBoardUtils.openKeybord(userName,this);
        loginPresenter.initIntentData(getIntent());

    }

    @Override
    protected LoginPresenter setBasePresenter() {
        return loginPresenter;
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
        userPassword.setText(passWord);
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
        //  portrait.setImageBitmap(bitmap);
    }

    @OnClick(R.id.tvRegister)
    void register(View view) {
        loginPresenter.toRegisterActivity();

    }

    @OnClick(R.id.ib_login_qq)
    void thirdOfQQ(View view) {
        //ToastUtils.showFailToast("需要到qq 申请APP的第三方登录的权限");
        UMUtils.getInstance().thirdLoginOfQQ();
    }

    @OnClick(R.id.ib_login_weixin)
    void thirdofWeixin(View view) {
        UMUtils.getInstance().thirdLoginOfWeChat();
    }

    @OnClick(R.id.ib_login_facebook)
    void thirdOfFacebook(View view) {
        UMUtils.getInstance().thirdLoginOfFaceBook();
        //ToastUtils.showFailToast("需要到Facebook申请APP的第三方登录的权限");
    }

    @OnClick(R.id.tvLogin)
    void login(View view) {
        loginPresenter.startLogin();
    }

    @OnClick(R.id.backBtn)
    void finishActivity(View view) {
        finish();
    }

    @OnClick(R.id.tvForgetPassword)
    void forgetPassword(View view) {
        loginPresenter.forgetPassword();
    }

    @Override
    protected void onDestroy() {
        CropHelper.clearCacheDir();
        UMUtils.mActivity = null;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        UMUtils.getInstance().onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }
}

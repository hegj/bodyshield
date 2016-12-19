package com.cmax.bodysheild.activity.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.cmax.bodysheild.activity.TemperatureInfoActivity;
import com.cmax.bodysheild.activity.UserListActivity;
import com.cmax.bodysheild.base.presenter.BasePresenter;
import com.cmax.bodysheild.bean.UserProfileInfo;
import com.cmax.bodysheild.bean.ble.BLEDevice;
import com.cmax.bodysheild.bean.cache.DeviceUser;
import com.cmax.bodysheild.bean.cache.User;
import com.cmax.bodysheild.http.rxsubscriber.ProgressSubscriber;
import com.cmax.bodysheild.util.Constant;
import com.cmax.bodysheild.util.DataUtils;
import com.cmax.bodysheild.util.DialogUtils;
import com.cmax.bodysheild.util.IntentUtils;
import com.cmax.bodysheild.util.KeyBoardUtils;
import com.cmax.bodysheild.util.PortraitUtil;
import com.cmax.bodysheild.util.SharedPreferencesUtil;
import com.cmax.bodysheild.util.ToastUtils;

import org.hybridsquad.android.library.BitmapUtil;
import org.hybridsquad.android.library.CropHandler;
import org.hybridsquad.android.library.CropParams;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;


/**
 * Created by Administrator on 2016/11/23 0023.
 */
public class LoginPresenter extends BasePresenter<ILoginView> implements CropHandler {
    private FragmentActivity activity;
    private BLEDevice device;
    private User currentUser;
    CropParams mCropParams;
    private LoginModel loginModel;
    private ILoginView loginView;
    private Bitmap bitmap;
    private Dialog choosePortraitDialog;

    // @Inject
    public LoginPresenter() {
        loginModel = new LoginModel();
    }

    public void setActivity(FragmentActivity activity) {
        this.activity = activity;
    }

    @Override
    public void attachView(ILoginView view) {
        super.attachView(view);
        this.loginView = view;
    }

    @Override
    public void detachView() {
        super.detachView();
        loginView = null;
    }

    public void startLogin() {
        KeyBoardUtils.closeKeybord(activity);
        final String userName = loginView.getUserName();
        if (TextUtils.isEmpty(userName)) {
            loginView.setUserNameError("用户名不能为空");
            return;
        }
        final String passWord = loginView.getPassWord();
        if (TextUtils.isEmpty(passWord)) {
            loginView.setPasswordError("密码不能为空");
            return;
        }

        loginModel.login(userName, passWord).subscribe(new ProgressSubscriber<UserProfileInfo>(getView()) {
            @Override
            public void _onError(String message) {

            }

            @Override
            public void _onNext(UserProfileInfo info) {

            }

            @Override
            public void _onCompleted() {
  /*  if (bitmap != null) {
                    String imagePath = PortraitUtil.writeBitmap(activity, bitmap, finalUser.getImage());
                    finalUser.setImage(imagePath);
                }*/
                final List<User> users = SharedPreferencesUtil.getList(Constant.USER_LIST,
                        User.class);

                User user = new User();
                user.setId(userName);
                user.setUserName(userName);
                user.setPassword(passWord);
                if (users.contains(user)) {
                    //包含
                    users.remove(user);
                }
                users.add(user);
                SharedPreferencesUtil.setList(Constant.USER_LIST, users);

                DataUtils.addDeviceToSp(device,user) ;

                 IntentUtils.toTemperatureInfoActivity(activity,device);

            }
        });
    }

    public void initIntentData(Intent extras) {
        if (extras!=null) {
            device = extras.getParcelableExtra(TemperatureInfoActivity.EXTRA_DEVICE);
            currentUser = extras.getParcelableExtra(UserListActivity.CURRENT_USER);
        }
        if (currentUser != null) {
            Bitmap bm = PortraitUtil.getBitmap(activity, currentUser.getImage());
            loginView.setPortraitBitmap(bm);
            loginView.setUserName(currentUser.getUserName());
        }
        mCropParams = new CropParams(activity);
    }

    @Override
    public void onPhotoCropped(Uri uri) {
        if (!mCropParams.compress) {
            bitmap = BitmapUtil.decodeUriAsBitmap(activity, uri);
            loginView.setPortraitBitmap(bitmap);
        }
    }

    @Override
    public void onCompressed(Uri uri) {
        if (mCropParams.compress) {
            bitmap = BitmapUtil.decodeUriAsBitmap(activity, uri);
            loginView.setPortraitBitmap(bitmap);
        }
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onFailed(String message) {
        ToastUtils.showFailToast(message);
    }

    @Override
    public void handleIntent(Intent intent, int requestCode) {
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public CropParams getCropParams() {
        return mCropParams;
    }

    public void showChoosePortraitDialog() {
        if (choosePortraitDialog == null) {
            choosePortraitDialog = DialogUtils.showChoosePortraitDialog(activity );
        } else {
            choosePortraitDialog.show();
        }

    }

    public void toRegisterActivity() {
        IntentUtils.toRegisterActivity(activity,device);
    }
}

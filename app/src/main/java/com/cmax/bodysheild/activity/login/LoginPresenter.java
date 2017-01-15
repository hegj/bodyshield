package com.cmax.bodysheild.activity.login;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.cmax.bodysheild.AppContext;
import com.cmax.bodysheild.R;
import com.cmax.bodysheild.activity.TemperatureInfoActivity;
import com.cmax.bodysheild.activity.UserListActivity;
import com.cmax.bodysheild.base.presenter.BasePresenter;
import com.cmax.bodysheild.bean.UserProfileInfo;
import com.cmax.bodysheild.bean.ble.BLEDevice;
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
import com.cmax.bodysheild.util.UIUtils;

import org.hybridsquad.android.library.BitmapUtil;
import org.hybridsquad.android.library.CropHandler;
import org.hybridsquad.android.library.CropParams;

import java.util.List;


/**
 * Created by Administrator on 2016/11/23 0023.
 */
public class LoginPresenter extends BasePresenter<ILoginView>  {
    private FragmentActivity activity;
    private BLEDevice device;
    private User currentUser;
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
            loginView.setUserNameError(UIUtils.getString(R.string.username_error_message));
            return;
        }
        final String passWord = loginView.getPassWord();
        if (TextUtils.isEmpty(passWord)) {
            loginView.setPasswordError(UIUtils.getString(R.string.password_error_message));
            return;
        }

        loginModel.login(userName, passWord).subscribe(new ProgressSubscriber<UserProfileInfo>(getView()) {
            @Override
            public void _onError(String message) {
                ToastUtils.showFailToast(UIUtils.getString(R.string.login_failed));
            }

            @Override
            public void _onNext(UserProfileInfo info) {
                AppContext.setUserId(info.getId());
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

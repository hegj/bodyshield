package com.cmax.bodysheild.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.base.BaseActivity;
import com.cmax.bodysheild.base.bean.BaseRequestData;
import com.cmax.bodysheild.base.presenter.BasePresenter;
import com.cmax.bodysheild.bean.UserProfileInfo;
import com.cmax.bodysheild.bean.ble.BLEDevice;
import com.cmax.bodysheild.bean.cache.User;
import com.cmax.bodysheild.http.rxexception.DefaultErrorBundle;
import com.cmax.bodysheild.http.rxexception.ErrorManager;
import com.cmax.bodysheild.http.rxexception.ServerException;
import com.cmax.bodysheild.inject.component.ActivityComponent;
import com.cmax.bodysheild.listeners.BelowMenuPopupWindowListener;
import com.cmax.bodysheild.model.RegisterModel;
import com.cmax.bodysheild.util.DataUtils;
import com.cmax.bodysheild.util.DialogUtils;
import com.cmax.bodysheild.util.IntentUtils;
import com.cmax.bodysheild.util.KeyBoardUtils;
import com.cmax.bodysheild.util.StringUtils;
import com.cmax.bodysheild.util.ToastUtils;
import com.cmax.bodysheild.util.UIUtils;
import com.cmax.bodysheild.widget.ChoosePopupWindow;


import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by Administrator on 2016/12/13 0013.
 */

public class RegisterActivity extends BaseActivity implements  BelowMenuPopupWindowListener {
    @Bind(R.id.userName)
    EditText userName;
    @Bind(R.id.userPassword)
    EditText userPassword;
    @Bind(R.id.tvLogin)
    TextView tvLogin;
    @Bind(R.id.userSex)
    TextView userSex;
    @Bind(R.id.userAge)
    EditText userAge;
    @Bind(R.id.userMobile)
    EditText userMobile;
    @Bind(R.id.backBtn)
    ImageView backBtn;
    @Bind(R.id.tvRegister)
    TextView tvRegister;
    @Bind(R.id.rlTitle)
    RelativeLayout rlTitle;
    private RegisterModel registerModel;
    private ChoosePopupWindow popupWindow;
    private int sex;
    private BLEDevice device;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void setActivityComponent(ActivityComponent activityComponent) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        registerModel = new RegisterModel();
        device = getIntent().getParcelableExtra(TemperatureInfoActivity.EXTRA_DEVICE);
      KeyBoardUtils.openKeybord(userName,this);

    }

    @Override
    protected BasePresenter setBasePresenter() {
        return null;
    }

    @Override
    protected void initEvent(Bundle savedInstanceState) {
        super.initEvent(savedInstanceState);
    }

    @OnClick(R.id.backBtn)
    void finishActivity(View view) {
        finish();
    }

    @OnClick(R.id.tvLogin)
    public void onClick() {
        KeyBoardUtils.closeKeybord(userSex);
        final String username = userName.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            ToastUtils.showFailToast(UIUtils.getString(R.string.username_error_message));
            return;
        }
        final String password = userPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtils.showFailToast(UIUtils.getString(R.string.password_error_message));
            return;
        }
        String userAge = this.userAge.getText().toString().trim();
        String userMobil = userMobile.getText().toString().trim();
        if (!TextUtils.isEmpty(userAge)&&(!StringUtils.isNumber(userAge)||Integer.parseInt(userAge)>99)){
            ToastUtils.showFailToast(UIUtils.getString(R.string.age_error_message));
            return;
        }

        final Map<String,String> map = new HashMap();
        map.put("name",username);
        map.put("password",password);
        map.put("sex",sex+"");
        map.put("age", TextUtils.isEmpty(userAge)?"0": userAge);
        map.put("mobile", TextUtils.isEmpty(userMobil)?"": userMobil);

        final ProgressDialog progressDialog = DialogUtils.showProgressDialog(RegisterActivity.this, UIUtils.getString(R.string.register_loading));
        progressDialog.show();
        registerModel.isRegister(username).flatMap(new Func1<BaseRequestData, Observable<UserProfileInfo>>() {
            @Override
            public Observable<UserProfileInfo> call(BaseRequestData baseRequestData) {
                if (baseRequestData.code == 0) {
                    return registerModel.register(map);
                } else {
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    return Observable.error(new ServerException(baseRequestData.code, UIUtils.getString(R.string.username_already_register)));
                }

            }
        }).subscribe(new Subscriber<UserProfileInfo>() {
            @Override
            public void onCompleted() {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                ToastUtils.showSuccessToast(UIUtils.getString(R.string.registered_successfully));
                IntentUtils.toTemperatureInfoActivity(RegisterActivity.this,device);

            }

            @Override
            public void onError(Throwable e) {
                String s = ErrorManager.handleError(new DefaultErrorBundle((Exception) e));
                if (TextUtils.isEmpty(s)) {
                    s = UIUtils.getString(R.string.register_failed_message);
                }
                ToastUtils.showFailToast(s);
                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
            }

            @Override
            public void onNext(UserProfileInfo userProfileInfo) {
                User user = new User();
                user.setId(userProfileInfo.getId()+"");
                user.setUserName(userProfileInfo.getName());
                user.setPassword(userProfileInfo.getPassword());
                DataUtils.addUserToSp(user);
                DataUtils.addDeviceToSp(device,user) ;



            }
        });
    }

    @OnClick(R.id.userSex)
    public void userSex() {
        KeyBoardUtils.closeKeybord(userSex);
        if (popupWindow==null){
            popupWindow = new ChoosePopupWindow(this);
            popupWindow.showMenuPopupwindow();
            popupWindow.setMenuTextGravity();
            popupWindow.setMenuLeftText(UIUtils.getString(R.string.woman) ,UIUtils.getString(R.string.man),UIUtils.getString(R.string.register_select_sex));
            popupWindow.setBelowMenuPopupWindowListener(this);
        } else {
            popupWindow.showMenuPopupwindow();
        }
    }

    @Override
    public void menuItemClickByType(int type) {
        switch (type) {
            case BelowMenuPopupWindowListener.TYPE_2:
                sex=2;
                userSex.setText(UIUtils.getString(R.string.woman));
                popupWindow.dismiss();
                break;
            case BelowMenuPopupWindowListener.TYPE_3:
                sex=1;

                userSex.setText(UIUtils.getString(R.string.man));
                popupWindow.dismiss();
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
     //   KeyBoardUtils.closeKeybord(this);
    }

    @OnClick(R.id.tvToLogin )
    void register(View view){
       IntentUtils.toLoginActivity(this,null,device);
        finish();
    }
}

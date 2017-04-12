package com.cmax.bodysheild.activity.user;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.base.BaseActivity;
import com.cmax.bodysheild.base.view.IStateView;
import com.cmax.bodysheild.bean.SendMessageInfo;
import com.cmax.bodysheild.bean.ble.BLEDevice;
import com.cmax.bodysheild.bean.cache.User;
import com.cmax.bodysheild.http.rxsubscriber.ProgressSubscriber;
import com.cmax.bodysheild.model.RegisterModel;
import com.cmax.bodysheild.util.DataUtils;
import com.cmax.bodysheild.util.DialogUtils;
import com.cmax.bodysheild.util.RxUtils;
import com.cmax.bodysheild.util.ToastUtils;
import com.cmax.bodysheild.util.UIUtils;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Subscriber;

/**
 * Created by Administrator on 2017/3/29 0029.
 */
public class UserEditPasswordActivity extends BaseActivity  implements IStateView {
    @Bind(R.id.et_userName)
    TextInputEditText etUserName;
    @Bind(R.id.et_userPassword)
    TextInputEditText etUserPassword;
    @Bind(R.id.et_userCountryCode)
    TextInputEditText etUserCountryCode;
    @Bind(R.id.et_phone)
    TextInputEditText etPhone;
    @Bind(R.id.et_code)
    TextInputEditText etCode;
    @Bind(R.id.tv_send_code)
    TextView tvSendCode;
    private RegisterModel registerModel;
    public String captcha;
    private ProgressDialog progressDialog;
    private User user;
    private BLEDevice device;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_edit_password;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        user = getIntent().getParcelableExtra("user");
        device = getIntent().getParcelableExtra("device");
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        registerModel = new RegisterModel();
    }

    @Override
    protected void initEvent(Bundle savedInstanceState) {
        super.initEvent(savedInstanceState);
        tvSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvGetVerifyCode();

            }
        });
    }

    public void tvGetVerifyCode() {

        String userName = etUserName.getText().toString().trim();
        if (TextUtils.isEmpty(userName) ){
            ToastUtils.showFailToast(UIUtils.getString(R.string.edit_text_verification_code));
            return;
        }
        registerModel.sendVerifyCodeByUserName(userName).subscribe(new ProgressSubscriber<SendMessageInfo>(this) {

            @Override
            public void _onError(String message) {
                ToastUtils.showFailToast(UIUtils.getString(R.string.network_server_error_message));
            }

            @Override
            public void _onNext(SendMessageInfo sendMessageInfo) {
                ToastUtils.showSuccessToast(UIUtils.getString(R.string.edit_password_captcha_sucssess));
                captcha = sendMessageInfo.captcha;
                tvSendCode.setText(UIUtils.getString(R.string.login_get_captcha));
                tvSendCode.setTextColor(UIUtils.getResourceColor(R.color.thin_gray));
                tvSendCode.setClickable(false);
                countDown();
                onCompleted();

            }

            @Override
            public void _onCompleted() {

            }
        });
      /*  String mobile = etPhone.getText().toString().trim();
        String countryCode = etUserCountryCode.getText().toString();
        if (TextUtils.isEmpty(mobile) ){
            ToastUtils.showFailToast(UIUtils.getString(R.string.login_please_input_mobile_warning));
            return;
        }
        if (!StringUtils.isPhoneNumber(mobile)){
            ToastUtils.showFailToast(UIUtils.getString(R.string.login_please_input_mobile_correct_warning));
            return;
        }
        if (!TextUtils.isEmpty(UIUtils.getUserPhone())) {
            if (!UIUtils.getUserPhone().equals(mobile)) {
                ToastUtils.showFailToast(UIUtils.getString(R.string.edit_profile_password_phone_warning));
                return;
            }
        }
        if (TextUtils.isEmpty(countryCode)) {
            ToastUtils.showFailToast(UIUtils.getString(R.string.login_please_input_country_code));
            return;
        }
        tvSendCode.setClickable(false);
        mobile = countryCode + "-" + mobile;
        registerModel.sendVerifyCode(mobile).subscribe(new ProgressSubscriber<SendMessageInfo>(this) {

            @Override
            public void _onError(String message) {
                ToastUtils.showFailToast(UIUtils.getString(R.string.network_server_error_message));
            }

            @Override
            public void _onNext(SendMessageInfo sendMessageInfo) {
                captcha = sendMessageInfo.captcha;
                tvSendCode.setText(UIUtils.getString(R.string.login_get_captcha));
                tvSendCode.setTextColor(UIUtils.getResourceColor(R.color.thin_gray));
                tvSendCode.setClickable(false);
                countDown();
                onCompleted();

            }

            @Override
            public void _onCompleted() {

            }
        });*/
    }

    private void countDown() {
        tvSendCode.setClickable(false);
        RxUtils.countdown(60).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                tvSendCode.setTextColor(UIUtils.getResourceColor(R.color.white));
                tvSendCode.setClickable(true);
                tvSendCode.setText(UIUtils.getString(R.string.login_get_captcha));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                if (UIUtils.isZh(UserEditPasswordActivity.this)) {
                    tvSendCode.setText(integer + "秒后获取");
                } else {
                    tvSendCode.setText(integer + "seconds");

                }

            }
        });
    }
    @OnClick(R.id.backBtn)
    public  void back(){
        finish();
    }
    @OnClick(R.id.tvSave)
    public void save() {
     //   final String userMobil = etPhone.getText().toString().trim();
        String username = etUserName.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            ToastUtils.showFailToast(UIUtils.getString(R.string.username_error_message));
            return;
        }
        final String password = etUserPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtils.showFailToast(UIUtils.getString(R.string.password_error_message));
            return;
        }

       /* if (TextUtils.isEmpty(userMobil)) {
            ToastUtils.showFailToast(UIUtils.getString(R.string.login_please_input_mobile_warning));
            return;
        }
        if (!TextUtils.isEmpty(UIUtils.getUserPhone())) {
            if (!UIUtils.getUserPhone().equals(userMobil)) {
                ToastUtils.showFailToast(UIUtils.getString(R.string.edit_profile_password_phone_warning));
                return;
            }
        }
        if (!StringUtils.isPhoneNumber(userMobil)){
            ToastUtils.showFailToast(UIUtils.getString(R.string.login_please_input_mobile_correct_warning));
            return;
        }*/
        String captcha2 = etCode.getText().toString();
        if (TextUtils.isEmpty(captcha2)) {
            ToastUtils.showFailToast(UIUtils.getString(R.string.login_please_input_captcha_code_warning));
            return;
        }
        if (TextUtils.isEmpty(captcha)) {
            ToastUtils.showFailToast(UIUtils.getString(R.string.login_please_input_get_captcha_code_warning));
            return;
        }
        if (!captcha.equals(captcha2)) {
            ToastUtils.showFailToast(UIUtils.getString(R.string.login_please_input_correct_captcha_code_warning));
            return;
        }
        registerModel.changePassword(username,password).subscribe(new ProgressSubscriber(this) {
            @Override
            public void _onError(String message) {
                ToastUtils.showFailToast(UIUtils.getString(R.string.access_server_failed));
            }

            @Override
            public void _onNext(Object o) {
                ToastUtils.showSuccessToast(UIUtils.getString(R.string.save_server_success));
                if (user!=null) {
                    user.setPassword(password);
                    DataUtils.addUserToSp(user);
                }
                finish();
               // IntentUtils.toLoginActivity(UserEditPasswordActivity.this,user,device);
            }

            @Override
            public void _onCompleted() {

            }
        });
    }

    @Override
    public void showProgressDialog() {
        if (progressDialog==null)
            progressDialog = DialogUtils.showProgressDialog(this, UIUtils.getString(R.string.loading2));
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog!=null &&progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}

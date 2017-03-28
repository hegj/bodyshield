package com.cmax.bodysheild.activity.user.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.base.view.IStateView;
import com.cmax.bodysheild.bean.SendMessageInfo;
import com.cmax.bodysheild.http.rxsubscriber.ProgressSubscriber;
import com.cmax.bodysheild.listeners.ProfileDataSuccessListener;
import com.cmax.bodysheild.model.RegisterModel;
import com.cmax.bodysheild.util.DialogUtils;
import com.cmax.bodysheild.util.RxUtils;
import com.cmax.bodysheild.util.StringUtils;
import com.cmax.bodysheild.util.ToastUtils;
import com.cmax.bodysheild.util.UIUtils;

import rx.Subscriber;

import static com.cmax.bodysheild.R.id.et_userCountryCode;

/**
 * Created by Administrator on 2017/1/18 0018.
 */

public class UserProfilePasswordItemView extends UserProfileItemView implements IStateView{

    private Context context;
    private RegisterModel registerModel;
    private TextInputEditText etPhone;
    private TextInputEditText etCode;
    private TextView tvSendCode;
    private TextInputEditText etUserPassword;
    private TextInputEditText etUserName;
    private TextInputEditText etUserCountryCode;
    private String captcha;
    private ProgressDialog progressDialog;

    public UserProfilePasswordItemView(Context context) {
        super(context);
    }

    public UserProfilePasswordItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        registerModel = new RegisterModel();
        this.context = context;
    }

    @Override
    protected void itemSetConfig(EditProfileDialog.Builder builder, int profileType) {
        View view = View.inflate(UIUtils.getContext(), R.layout.view_user_profile_phone, null);
        builder.setContentView(view);
        builder.setTitle(UIUtils.getString(R.string.profile_change_password));
        builder.setDialogType(EditProfileDialog.TYPE4);

        etPhone = (TextInputEditText) view.findViewById(R.id.et_phone);
        etUserPassword = (TextInputEditText) view.findViewById(R.id.et_userPassword);
        etUserCountryCode = (TextInputEditText) view.findViewById(et_userCountryCode);
        etUserName = (TextInputEditText) view.findViewById(R.id.et_userName);
        etCode = (TextInputEditText) view.findViewById(R.id.et_code);
        tvSendCode = (TextView) view.findViewById(R.id.tv_send_code);
        tvSendCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSendCode.setClickable(false);
                tvSendCode.setEnabled(false);
                tvGetVerifyCode();
            }
        });
    }

    @Override
    protected void sendDataToServer(DialogInterface dialog, int profileType, ProfileDataSuccessListener profileDataSuccessListener) {
        super.sendDataToServer(dialog, profileType, profileDataSuccessListener);
        String userMobil = etPhone.getText().toString().trim();
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

        if (!TextUtils.isEmpty(userMobil) && (!StringUtils.isPhoneNumber(userMobil))) {
            ToastUtils.showFailToast(UIUtils.getString(R.string.login_please_input_mobile_warning));
            return;
        }
        String captcha2 = etCode.getText().toString();
        if (TextUtils.isEmpty(captcha2)) {
            ToastUtils.showFailToast(UIUtils.getString(R.string.login_please_input_captcha_code_warning));
            return;
        }
        if (TextUtils.isEmpty(captcha)) {
            ToastUtils.showFailToast(UIUtils.getString(R.string.login_please_input_get_captcha_code_warning));
            return;
        }
        if (captcha.equals(captcha2)) {
            ToastUtils.showFailToast(UIUtils.getString(R.string.login_please_input_correct_captcha_code_warning));
            return;
        }
        registerModel.changePassword(username,password);
    }

    public void tvGetVerifyCode() {
        String mobile = etPhone.getText().toString().trim();
        String countryCode = etUserCountryCode.getText().toString();
        if (TextUtils.isEmpty(countryCode)) {
            ToastUtils.showFailToast(UIUtils.getString(R.string.login_please_input_country_code));
            return;
        }
        if (!StringUtils.isPhoneNumber(mobile)) {
            ToastUtils.showFailToast(UIUtils.getString(R.string.login_please_input_mobile_warning));
            return;
        }

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

            }

            @Override
            public void _onCompleted() {

            }
        });
    }

    private void countDown() {
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
                if (UIUtils.isZh(context)) {
                    tvSendCode.setText(integer + "后获取");
                } else {
                    tvSendCode.setText(integer + "seconds");

                }

            }
        });
    }
    @Override
    public void showProgressDialog() {
        progressDialog = DialogUtils.showProgressDialog((Activity) context, UIUtils.getString(R.string.register_loading));
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}

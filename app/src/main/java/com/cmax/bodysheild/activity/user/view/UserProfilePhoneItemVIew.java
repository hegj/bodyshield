package com.cmax.bodysheild.activity.user.view;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.base.BaseCustomView;
import com.cmax.bodysheild.listeners.ProfileDataSuccessListener;
import com.cmax.bodysheild.util.UIUtils;
import com.orhanobut.logger.Logger;

/**
 * Created by Administrator on 2017/1/18 0018.
 */

public class UserProfilePhoneItemView extends UserProfileItemView {

    private TextInputEditText etPhone;
    private TextInputEditText etCode;
    private TextView tvSendCode;

    public UserProfilePhoneItemView(Context context) {
        super(context);
    }
    public UserProfilePhoneItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void itemSetConfig(EditProfileDialog.Builder builder, int profileType) {
        builder.setTitle(UIUtils.getString(R.string.login_please_input_mobile_warning));
        builder.setDialogType(EditProfileDialog.TYPE4);
        View view = View.inflate(UIUtils.getContext(), R.layout.view_user_profile_phone,null);
        etPhone = (TextInputEditText) view.findViewById(R.id.et_phone);
        etCode = (TextInputEditText) view.findViewById(R.id.et_code);
        tvSendCode = (TextView) view.findViewById(R.id.tv_send_code);
        builder.setContentView(view);
    }

    @Override
    protected void sendDataToServer(DialogInterface dialog, int profileType, ProfileDataSuccessListener profileDataSuccessListener) {
        super.sendDataToServer(dialog, profileType, profileDataSuccessListener);
        String phone = etPhone.getText().toString().trim();
        String code = etCode.getText().toString().trim();
        Logger.d(phone);
        Logger.d(code);
    }
}

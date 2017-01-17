package com.cmax.bodysheild.activity.user.view;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.listeners.ProfileDataSuccessListener;
import com.cmax.bodysheild.util.DataUtils;
import com.cmax.bodysheild.util.UIUtils;

/**
 * Created by Administrator on 2017/1/16 0016.
 */

public class UserProfileNameItemView extends  UserProfileItemView {
    public UserProfileNameItemView(Context context) {
        super(context);
    }

    public UserProfileNameItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void itemSetConfig(EditProfileDialog.Builder builder, int profileType) {
        builder.setDialogType(EditProfileDialog.TYPE3);
        builder.setTitle(UIUtils.getString(R.string.login_please_input_name_warning));
    }

    @Override
    protected void sendDataToServer(DialogInterface dialog, int profileType, ProfileDataSuccessListener profileDataSuccessListener) {
        super.sendDataToServer(dialog, profileType, profileDataSuccessListener);
        String etMessageContent = builder.getEtMessageContent();
        user.setUserName(etMessageContent);
        DataUtils.addUserToSp(user);
        dialog.dismiss();
        if (profileDataSuccessListener!=null)
        profileDataSuccessListener.success(profileType,user);
    }
}

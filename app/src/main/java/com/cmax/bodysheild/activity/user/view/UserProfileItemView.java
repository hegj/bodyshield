package com.cmax.bodysheild.activity.user.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.base.BaseCustomView;
import com.cmax.bodysheild.bean.cache.User;
import com.cmax.bodysheild.listeners.ProfileDataSuccessListener;
import com.cmax.bodysheild.util.DialogUtils;
import com.cmax.bodysheild.util.UIUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/1/12 0012.
 */

public abstract class UserProfileItemView extends BaseCustomView {


    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_profile_value)
    TextView tvProfileValue;
    private int profileType;
    private EditProfileDialog editProfileDialog;
    private Context context;
    protected EditProfileDialog.Builder builder;
    protected User user;
    ProfileDataSuccessListener profileDataSuccessListener;
    public UserProfileItemView(Context context) {
        super(context);
    }

    public UserProfileItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initEvent(Context context) {
        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profileType==0)return;
                 buildProfileDialog(profileType);
            }
        });
    }
    public  void setProfileDataSuccessListener( ProfileDataSuccessListener listener){
        profileDataSuccessListener=listener;
    }

    private void buildProfileDialog(final int profileType) {
        builder = new EditProfileDialog.Builder();
        itemSetConfig(builder,profileType);
        builder.setNegativeButton(UIUtils.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(UIUtils.getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 访问网络
                sendDataToServer(dialog,profileType,profileDataSuccessListener);
            }
        });
        if (editProfileDialog!=null) {
            editProfileDialog.dismiss();
        }
        builder.setContext((Activity) context);
        editProfileDialog = builder.create();
        editProfileDialog.show();
    }

    protected abstract void itemSetConfig(EditProfileDialog.Builder builder, int profileType) ;

    protected void sendDataToServer(DialogInterface dialog, int profileType, ProfileDataSuccessListener profileDataSuccessListener) {

    }

    public  void setProfileValue(String value){
        if (TextUtils.isEmpty(value))return;
        tvProfileValue.setText(value);
    }
    @Override
    protected void initData(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.UserProfileItemView);
        String profileName = typedArray.getString(R.styleable.UserProfileItemView_profileName);
        tvName.setText(profileName);
        profileType = typedArray.getInteger(R.styleable.UserProfileItemView_profileType, 0);
        this.context =context;
    }

    @Override
    protected void initView() {
        rootView = (ViewGroup) View.inflate(UIUtils.getContext(), R.layout.user_profile_item, null);
        ButterKnife.bind(this,rootView);
    }

    public void setUser(User user) {
        this.user =user;
    }
}

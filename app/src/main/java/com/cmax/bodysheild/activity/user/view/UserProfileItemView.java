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
import com.cmax.bodysheild.util.DialogUtils;
import com.cmax.bodysheild.util.UIUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/1/12 0012.
 */

public class UserProfileItemView extends BaseCustomView {


    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_profile_value)
    TextView tvProfileValue;
    private int profileType;
    private EditProfileDialog editProfileDialog;
    private Context context;

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

    private void buildProfileDialog(final int profileType) {
        final EditProfileDialog.Builder builder = new EditProfileDialog.Builder();
       String title ="";
        switch (profileType){
            case 1:
                builder.setDialogType(EditProfileDialog.TYPE3);
                title="请输入您要修改的用户名";
                break;
            case  2:
                title="请输入您要修改的用户名";
                builder.setDialogType(EditProfileDialog.TYPE4);
                break;
        }
        builder.setTitle(title);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 访问网络
                sendDataToServer(dialog,profileType);
            }
        });
        if (editProfileDialog!=null) {
            editProfileDialog.dismiss();
        }
        builder.setContext((Activity) context);
        editProfileDialog = builder.create();
        editProfileDialog.show();
    }

    private void sendDataToServer(DialogInterface dialog, int profileType) {

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
}

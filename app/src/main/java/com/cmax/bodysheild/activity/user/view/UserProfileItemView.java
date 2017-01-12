package com.cmax.bodysheild.activity.user.view;

import android.content.Context;
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

    private void buildProfileDialog(int profileType) {
        DialogUtils.showEditProfileDialog();
       /* String title ="";
        switch (profileType){
            case 1:
                title="请输入您要修改的用户名";
                break;

            case  2:
                title="请输入您要修改的用户名";

                break;
        }*/
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
    }

    @Override
    protected void initView() {
        rootView = (ViewGroup) View.inflate(UIUtils.getContext(), R.layout.user_profile_item, null);
        ButterKnife.bind(this,rootView);
    }
}

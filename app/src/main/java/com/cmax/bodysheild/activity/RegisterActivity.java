package com.cmax.bodysheild.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.activity.login.LoginActivity;
import com.cmax.bodysheild.base.BaseMvpActivity;
import com.cmax.bodysheild.bean.cache.User;
import com.cmax.bodysheild.inject.component.ActivityComponent;
import com.cmax.bodysheild.util.Constant;
import com.cmax.bodysheild.util.PortraitUtil;
import com.cmax.bodysheild.util.SharedPreferencesUtil;
import com.cmax.bodysheild.util.ToastUtils;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/13 0013.
 */

public class RegisterActivity extends BaseMvpActivity {
    @Bind(R.id.userName)
    EditText userName;
    @Bind(R.id.userPassword)
    EditText userPassword;
    @Bind(R.id.tvLogin)
    TextView tvLogin;

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
        String username = userName.getText().toString().trim();
        if (TextUtils.isEmpty(username)){
            ToastUtils.showFailToast("请填写用户名");
            return;
        }
        String password = userPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)){
            ToastUtils.showFailToast("请填写密码");
            return;
        }
        User  user = new User();
        user.setId(username);
        user.setUserName(username);
        user.setPassword(password);
        List<User> users = SharedPreferencesUtil.getList(Constant.USER_LIST,
                User.class);
            for (User u : users) {
                if (u.equals(user)) {
                    ToastUtils.showFailToast("已经有了这个账号啦,请您修改账号密码");
                      return;
                }
        }
        users.add(user);

        SharedPreferencesUtil.setList(Constant.USER_LIST, users);
        ToastUtils.showSuccessToast("注册成功");
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.putExtra("Register",user);
        setResult(2,intent);
        finish();
    }
}

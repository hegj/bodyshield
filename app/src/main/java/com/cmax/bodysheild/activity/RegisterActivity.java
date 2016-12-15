package com.cmax.bodysheild.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.activity.login.ILoginView;
import com.cmax.bodysheild.activity.login.LoginActivity;
import com.cmax.bodysheild.base.BaseActivity;
import com.cmax.bodysheild.base.BaseMvpActivity;
import com.cmax.bodysheild.base.bean.BaseRequestData;
import com.cmax.bodysheild.base.presenter.BasePresenter;
import com.cmax.bodysheild.base.view.IStateView;
import com.cmax.bodysheild.bean.UserProfileInfo;
import com.cmax.bodysheild.bean.cache.User;
import com.cmax.bodysheild.http.rxexception.DefaultErrorBundle;
import com.cmax.bodysheild.http.rxexception.ErrorManager;
import com.cmax.bodysheild.http.rxexception.ServerException;
import com.cmax.bodysheild.http.rxsubscriber.ProgressSubscriber;
import com.cmax.bodysheild.inject.component.ActivityComponent;
import com.cmax.bodysheild.model.RegisterModel;
import com.cmax.bodysheild.util.Constant;
import com.cmax.bodysheild.util.DialogUtils;
import com.cmax.bodysheild.util.PortraitUtil;
import com.cmax.bodysheild.util.SharedPreferencesUtil;
import com.cmax.bodysheild.util.ToastUtils;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by Administrator on 2016/12/13 0013.
 */

public class RegisterActivity extends BaseActivity implements IStateView {
    @Bind(R.id.userName)
    EditText userName;
    @Bind(R.id.userPassword)
    EditText userPassword;
    @Bind(R.id.tvLogin)
    TextView tvLogin;
    private RegisterModel registerModel;

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
        final String username = userName.getText().toString().trim();
        if (TextUtils.isEmpty(username)){
            ToastUtils.showFailToast("请填写用户名");
            return;
        }
        final String password = userPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)){
            ToastUtils.showFailToast("请填写密码");
            return;
        }
        final ProgressDialog   progressDialog = DialogUtils.showProgressDialog(RegisterActivity.this, "注册中,请稍后");
        registerModel.isRegister(username).flatMap(new Func1<BaseRequestData, Observable<UserProfileInfo>>() {
            @Override
            public Observable<UserProfileInfo> call(BaseRequestData baseRequestData) {
                if (baseRequestData.code==0){
                return     registerModel.register(username,password);
                } else {
                    if (progressDialog!=null&&progressDialog.isShowing())progressDialog.dismiss();
                   // ToastUtils.showFailToast();
                    return Observable.error(new ServerException(baseRequestData.code,"用户名已经被注册,请修改用户名"));
                }

            }
        }).subscribe(new Subscriber<UserProfileInfo>() {
            @Override
            public void onCompleted() {
                ToastUtils.showSuccessToast("注册成功");
            }

            @Override
            public void onError(Throwable e) {
                String s = ErrorManager.handleError(new DefaultErrorBundle((Exception) e));
                if (TextUtils.isEmpty(s)){
                    s="注册失败,请重试";
                }
                ToastUtils.showFailToast(s);
                if (progressDialog!=null&&progressDialog.isShowing())progressDialog.dismiss();
            }

            @Override
            public void onNext(UserProfileInfo userProfileInfo) {

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
        });
     /*  registerModel.isRegister(username).subscribe(new Subscriber<Object>() {

           @Override
           public void onStart() {
               super.onStart();
           }

           @Override
           public void onCompleted() {
               registerModel.register(username,password).subscribe(new Subscriber<UserProfileInfo>() {
                   @Override
                   public void onCompleted() {
                       if (progressDialog!=null&&progressDialog.isShowing())progressDialog.dismiss();
                   }

                   @Override
                   public void onError(Throwable e) {
                       String s = ErrorManager.handleError(new DefaultErrorBundle((Exception) e));
                       if (TextUtils.isEmpty(s)){
                           s="注册失败,请重试";
                       }
                       ToastUtils.showFailToast(s);
                       if (progressDialog!=null&&progressDialog.isShowing())progressDialog.dismiss();
                   }

                   @Override
                   public void onNext(UserProfileInfo userProfileInfo) {
                        //保存用户
                       onCompleted();
                   }
               });
           }

           @Override
           public void onError(Throwable e) {
               String s = ErrorManager.handleError(new DefaultErrorBundle((Exception) e));
               if (TextUtils.isEmpty(s)){
                   s="此账号已经被注册,请更换用户名";
               }
               ToastUtils.showFailToast(s);
               if (progressDialog!=null&&progressDialog.isShowing())progressDialog.dismiss();
           }

           @Override
           public void onNext(Object o) {
            onCompleted();
           }
       })  ;*/
       /* Observable.concat(registerModel.isRegister(username),registerModel.register(username,password)).first().subscribe(new ProgressSubscriber<UserProfileInfo>(this) {
            @Override
            public void _onError(String message) {

            }

            @Override
            public void _onNext(UserProfileInfo userProfileInfo) {

            }

            @Override
            public void _onCompleted() {

            }
        });
*/

    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void hideProgressDialog() {

    }
}

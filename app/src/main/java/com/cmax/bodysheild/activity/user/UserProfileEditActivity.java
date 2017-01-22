package com.cmax.bodysheild.activity.user;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.activity.UserListActivity;
import com.cmax.bodysheild.activity.user.view.UserProfileNameItemView;
import com.cmax.bodysheild.api.Url;
import com.cmax.bodysheild.base.BaseActivity;
import com.cmax.bodysheild.base.view.IStateView;
import com.cmax.bodysheild.bean.cache.User;
import com.cmax.bodysheild.http.HttpMethods;
import com.cmax.bodysheild.http.RxJavaHttpHelper;
import com.cmax.bodysheild.http.rxschedulers.RxSchedulersHelper;
import com.cmax.bodysheild.http.rxsubscriber.ProgressSubscriber;
import com.cmax.bodysheild.listeners.CropPickListeners;
import com.cmax.bodysheild.listeners.ProfileDataSuccessListener;
import com.cmax.bodysheild.util.CropUtils;
import com.cmax.bodysheild.util.DataUtils;
import com.cmax.bodysheild.util.DialogUtils;
import com.cmax.bodysheild.util.PermissionUtils;
import com.cmax.bodysheild.util.PortraitUtil;
import com.cmax.bodysheild.util.ToastUtils;
import com.cmax.bodysheild.util.UIUtils;
import com.cmax.bodysheild.widget.CircleImageView;
import com.yalantis.ucrop.UCrop;

import org.hybridsquad.android.library.BitmapUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2016/12/16 0016.
 */

public class UserProfileEditActivity extends BaseActivity implements CropPickListeners ,IStateView{
    @Bind(R.id.userImageBtn)
    CircleImageView userImageBtn;
    @Bind(R.id.tv_user_name)
    UserProfileNameItemView tvUserName;
  /*  @Bind(R.id.tv_user_phone)
    UserProfileItemView tvUserPhone;*/

    private Bitmap bitmap;
    private Dialog mCropParamsDialog;
    private Uri resultUri;
    private ProgressDialog progressDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_userprofile;
    }

    private User user;

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        user = getIntent().getParcelableExtra(UserListActivity.CURRENT_USER);
        if (user != null) {
            tvUserName.setProfileValue(user.getUserName());
            bitmap = PortraitUtil.getBitmap(this, user.getImage());
            userImageBtn.setImageBitmap(bitmap);
            tvUserName.setUser(user);
        }

    }

    @OnClick({R.id.backBtn, R.id.userImageBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.userImageBtn:
                if (mCropParamsDialog == null) {
                    mCropParamsDialog = DialogUtils.showChoosePortraitDialog(this);
                } else {
                    mCropParamsDialog.show();
                }

                break;
        }
    }

    @Override
    protected void initEvent(Bundle savedInstanceState) {
        super.initEvent(savedInstanceState);

       /* tvUserName.setProfileDataSuccessListener(new ProfileDataSuccessListener() {
            @Override
            public void success(int profileType, User user) {
                tvUserName.setProfileValue(user.getUserName());
            }

            @Override
            public void failed() {

            }
        });*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CropUtils.handleResult(this, this, requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @OnClick(R.id.tvSave)
    public void save() {
        if (resultUri==null)
            return;
        int userId = UIUtils.getUserId();
        File file = new File(resultUri.getPath());
        Map<String ,RequestBody > map = new HashMap<>();
        Url.UPLOAD_HEADIMAGE= Url.UPLOAD_HEADIMAGE+"?uid="+userId;
        map.put("file\";filename=\""+file.getName(),HttpMethods.getInstance().toImageRequestBody(file));
        HttpMethods.getInstance().apiService.updateImage(Url.UPLOAD_HEADIMAGE,map)
                .compose(RxJavaHttpHelper.<String>handleResult())  .compose( RxSchedulersHelper.<String>applyIoTransformer())
                .subscribe(new ProgressSubscriber<String>(this) {
            @Override
            public void _onError(String message) {
                ToastUtils.showFailToast(UIUtils.getString(R.string.access_server_failed));
            }

            @Override
            public void _onNext(String baseRequestData) {
                DataUtils.saveUserInfo(bitmap, UserProfileEditActivity.this, user);
                onCompleted();
            }

            @Override
            public void _onCompleted() {
                ToastUtils.showSuccessToast(UIUtils.getString(R.string.access_server_success));
                setResult(3);
                finish();
            }
        });

    }

    @Override
    public void cropResult(Intent data) {
        resultUri = UCrop.getOutput(data);
        bitmap = BitmapUtil.decodeUriAsBitmap(this, resultUri);
        userImageBtn.setImageBitmap(bitmap);
    }

    @Override
    public void cropError(Intent data) {
        ToastUtils.showFailToast(UIUtils.getString(R.string.get_pick_failure));
        resultUri=null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCropParamsDialog != null && mCropParamsDialog.isShowing()) {
            mCropParamsDialog.dismiss();
        }
        CropUtils.setPermissionDialogDismiss();
    }


    @Override
    public void showProgressDialog() {
        if (progressDialog==null)
            progressDialog = DialogUtils.showProgressDialog(this, UIUtils.getString(R.string.loading));
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog!=null &&progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

}

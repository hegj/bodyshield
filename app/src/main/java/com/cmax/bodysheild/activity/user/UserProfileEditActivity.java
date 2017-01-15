package com.cmax.bodysheild.activity.user;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.activity.UserListActivity;
import com.cmax.bodysheild.activity.user.view.UserProfileItemView;
import com.cmax.bodysheild.activity.user.view.UserProfileNameItemView;
import com.cmax.bodysheild.base.BaseActivity;
import com.cmax.bodysheild.bean.cache.User;
import com.cmax.bodysheild.listeners.CropPickListeners;
import com.cmax.bodysheild.util.CropUtils;
import com.cmax.bodysheild.util.DataUtils;
import com.cmax.bodysheild.util.DialogUtils;
import com.cmax.bodysheild.util.PermissionUtils;
import com.cmax.bodysheild.util.ToastUtils;
import com.cmax.bodysheild.util.UIUtils;
import com.cmax.bodysheild.widget.CircleImageView;
import com.yalantis.ucrop.UCrop;

import org.hybridsquad.android.library.BitmapUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/16 0016.
 */

public class UserProfileEditActivity extends BaseActivity implements CropPickListeners {
    @Bind(R.id.userImageBtn)
    CircleImageView userImageBtn;
    @Bind(R.id.tv_user_name)
    UserProfileNameItemView tvUserName;
  /*  @Bind(R.id.tv_user_phone)
    UserProfileItemView tvUserPhone;*/

    private Bitmap bitmap;
    private Dialog mCropParamsDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_userprofile;
    }

    private User user;

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        user = getIntent().getParcelableExtra(UserListActivity.CURRENT_USER);
        if (user != null)
            tvUserName.setProfileValue(user.getUserName());
        tvUserName.setUser(user);
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
        List<User> usertList = DataUtils.getUserList();
        if (usertList.contains(user)) usertList.remove(user);
        DataUtils.saveUserPortrait(bitmap, this, user);
        DataUtils.addUserToSp(user);
        setResult(3);
        finish();
    }

    @Override
    public void cropResult(Intent data) {
        final Uri resultUri = UCrop.getOutput(data);
        bitmap = BitmapUtil.decodeUriAsBitmap(this, resultUri);
        userImageBtn.setImageBitmap(bitmap);
    }

    @Override
    public void cropError(Intent data) {
        ToastUtils.showFailToast(UIUtils.getString(R.string.get_pick_failure));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCropParamsDialog != null && mCropParamsDialog.isShowing()) {
            mCropParamsDialog.dismiss();
        }
        CropUtils.setPermissionDialogDismiss();
    }


}

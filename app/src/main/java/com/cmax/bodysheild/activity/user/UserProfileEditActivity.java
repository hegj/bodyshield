package com.cmax.bodysheild.activity.user;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.activity.UserListActivity;
import com.cmax.bodysheild.base.BaseActivity;
import com.cmax.bodysheild.bean.cache.User;
import com.cmax.bodysheild.util.DataUtils;
import com.cmax.bodysheild.util.DialogUtils;
import com.cmax.bodysheild.util.ToastUtils;
import com.cmax.bodysheild.widget.CircleImageView;

import org.hybridsquad.android.library.BitmapUtil;
import org.hybridsquad.android.library.CropHandler;
import org.hybridsquad.android.library.CropHelper;
import org.hybridsquad.android.library.CropParams;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/16 0016.
 */

public class UserProfileEditActivity extends BaseActivity implements CropHandler {
    @Bind(R.id.userImageBtn)
    CircleImageView userImageBtn;
    @Bind(R.id.tvUserName)
    TextView tvUserName;
    private CropParams mCropParams;
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
        mCropParams = new CropParams(this);
        user = getIntent().getParcelableExtra(UserListActivity.CURRENT_USER);
        tvUserName.setText(user.getUserName());
    }

    @OnClick({R.id.backBtn, R.id.userImageBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.userImageBtn:
                if (mCropParamsDialog==null) {
                    mCropParamsDialog = DialogUtils.showChoosePortraitDialog(this, mCropParams);
                }else{
                    mCropParamsDialog.show();
                }

                break;
        }
    }

    @Override
    public void onPhotoCropped(Uri uri) {
        if (!mCropParams.compress) {
            bitmap = BitmapUtil.decodeUriAsBitmap(this, uri);
            userImageBtn.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onCompressed(Uri uri) {
        if (mCropParams.compress) {
            bitmap = BitmapUtil.decodeUriAsBitmap(this, uri);
            userImageBtn.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onFailed(String message) {
        ToastUtils.showFailToast(message);
    }

    @Override
    public void handleIntent(Intent intent, int requestCode) {
        this.startActivityForResult(intent, requestCode);
    }

    @Override
    public CropParams getCropParams() {
        return mCropParams;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CropHelper.handleResult(this, requestCode, resultCode, data);

    }



    @OnClick(R.id.tvSave)
    public void save() {
        List<User> usertList = DataUtils.getUserList();
        if (usertList.contains(user))usertList.remove(user);
        DataUtils.saveUserPortrait(bitmap,this,user);
        DataUtils.addUserToSp(user);
       setResult(3);
        finish();
    }
}

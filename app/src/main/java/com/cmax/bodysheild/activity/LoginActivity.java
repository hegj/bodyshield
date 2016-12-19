package com.cmax.bodysheild.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.bean.ble.BLEDevice;
import com.cmax.bodysheild.bean.cache.DeviceUser;
import com.cmax.bodysheild.bean.cache.User;
import com.cmax.bodysheild.util.Constant;
import com.cmax.bodysheild.util.LogUtil;
import com.cmax.bodysheild.util.PortraitUtil;
import com.cmax.bodysheild.util.SharedPreferencesUtil;

import org.apache.commons.lang3.StringUtils;
import org.hybridsquad.android.library.BitmapUtil;
import org.hybridsquad.android.library.CropHandler;
import org.hybridsquad.android.library.CropHelper;
import org.hybridsquad.android.library.CropParams;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements CropHandler, View.OnClickListener{

    public static final String TAG = "LoginActivity";
    @Bind(R.id.saveBtn)
    TextView saveBtn;
    @Bind(R.id.userName)
    EditText userName;
    @Bind(R.id.portrait)
    ImageView portrait;
    private BLEDevice device;
    CropParams mCropParams;
    private User currentUser;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Bundle extras = getIntent().getExtras();
        device = extras.getParcelable(TemperatureInfoActivity.EXTRA_DEVICE);
        currentUser = extras.getParcelable(UserListActivity.CURRENT_USER);
        if(currentUser!=null){
            Bitmap bm = PortraitUtil.getBitmap(this,currentUser.getImage());
            portrait.setImageBitmap(bm);
            userName.setText(currentUser.getUserName());
        }
        mCropParams = new CropParams(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.backBtn)
    void finishActivity(View view){
        finish();
    }


    @OnClick(R.id.saveBtn)
    void saveDeviceName(View view){
        String name = userName.getText().toString();

        List<User> users = SharedPreferencesUtil.getList(Constant.USER_LIST,
                User.class);

        if(StringUtils.isBlank(name)){
            //deviceName.setError(getString(R.string.login_please_input_name_warning));
            Toast.makeText(LoginActivity.this, R.string.login_please_input_name_warning, Toast.LENGTH_SHORT).show();
            return;
        }
        User user = null;
        if(currentUser!=null){
            for(User u : users){
                if(u.equals(currentUser)){
                    user = u;
                    break;
                }
            }
        }
        if(user==null){
            user = new User();
            users.add(user);
            SharedPreferencesUtil.setLongValue(Constant.KEY_TIME_FLAG, new Date().getTime());
        }
        user.setId(name);
        user.setUserName(name);
        if(bitmap!=null){
            String imagePath = PortraitUtil.writeBitmap(this,bitmap,user.getImage());
            user.setImage(imagePath);
        }

//        if (!users.contains(user)){
//            users.add(user);
//            SharedPreferencesUtil.setList(Constant.USER_LIST, users);
//        }
        SharedPreferencesUtil.setList(Constant.USER_LIST, users);

        List<DeviceUser> deviceUsers = SharedPreferencesUtil.getList(Constant.DEVICE_USER_LIST,
                DeviceUser.class);
        DeviceUser temp = null;
        for (DeviceUser deviceuser:deviceUsers) {
            if (deviceuser.getAddress().equalsIgnoreCase(device.getAddress())){
                temp = deviceuser;
                break;
            }
        }

        if (temp == null){
            temp = new DeviceUser();
        }

        temp.setDeviceType(device.getDeviceType());
        temp.setAddress(device.getAddress());
        temp.setUserId(user.getId());
        temp.setName(device.getName());
        deviceUsers.add(temp);

        SharedPreferencesUtil.setList(Constant.DEVICE_USER_LIST,deviceUsers);

        //保存设备名称
        SharedPreferencesUtil.setStringValue(device.getAddress(),name);

        final Intent intent = new Intent(this, TemperatureInfoActivity.class);
        intent.putExtra(TemperatureInfoActivity.EXTRA_DEVICE, device);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.portrait)
    void setPortrait(ImageView v){
        showDialog();
    }
    Dialog dialog = null;
    private void showDialog() {
        View view = getLayoutInflater().inflate(R.layout.photo_choose_dialog, null);
        dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        Point p = new Point();
        getWindowManager().getDefaultDisplay().getSize(p);
        wl.y = p.y;
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        //
        ButterKnife.findById(view,R.id.galleryBtn).setOnClickListener(this);
        ButterKnife.findById(view,R.id.cameraBtn).setOnClickListener(this);
        ButterKnife.findById(view,R.id.canelBtn).setOnClickListener(this);

        dialog.show();
    }

    @Override
    public void onClick(View v) {
        //https://github.com/ryanhoo/PhotoCropper
        switch (v.getId()){
            case R.id.galleryBtn:{
//                mCropParams.refreshUri();
//                mCropParams.enable = false;
//                mCropParams.compress = true;
//                Intent intent = CropHelper.buildGalleryIntent(mCropParams);
//                startActivityForResult(intent, CropHelper.REQUEST_PICK);
                mCropParams.enable = true;
                mCropParams.compress = true;
                Intent intent = CropHelper.buildGalleryIntent(mCropParams);
                startActivityForResult(intent, CropHelper.REQUEST_CROP);
                dismissDialog();
            }
            break;
            case R.id.cameraBtn:{
                mCropParams.refreshUri();
//                mCropParams.enable = false;
//                mCropParams.compress = true;
//                Intent intent = CropHelper.buildCameraIntent(mCropParams);
//                startActivityForResult(intent, CropHelper.REQUEST_CAMERA);
                mCropParams.enable = true;
                mCropParams.compress = true;
                Intent intent = CropHelper.buildCameraIntent(mCropParams);
                startActivityForResult(intent, CropHelper.REQUEST_CAMERA);
                dismissDialog();
            }
                break;
            case R.id.canelBtn:
                dismissDialog();
                break;
            default:
                break;
        }
    }

    private void dismissDialog(){
        if(dialog!=null){
            dialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CropHelper.handleResult(this, requestCode, resultCode, data);
        if (requestCode == 1) {
            LogUtil.e(TAG, "onActivityResult");
        }
    }

    @Override
    protected void onDestroy() {
        CropHelper.clearCacheDir();
        super.onDestroy();
    }

    @Override
    public CropParams getCropParams() {
        return mCropParams;
    }

    @Override
    public void onPhotoCropped(Uri uri) {
        // Original or Cropped uri
        LogUtil.d(TAG, "onPhotoCropped:Crop Uri in path: " + uri.getPath());
        if (!mCropParams.compress){
            bitmap = BitmapUtil.decodeUriAsBitmap(this, uri);
            portrait.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onCompressed(Uri uri) {
        // Compressed uri
        LogUtil.d(TAG, "onCompressed:Crop Uri in path: " + uri.getPath());
        if(mCropParams.compress){
            bitmap = BitmapUtil.decodeUriAsBitmap(this, uri);
            portrait.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onCancel() {
        //Toast.makeText(this, "Crop canceled!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailed(String message) {
        Toast.makeText(this, "Crop failed: " + message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void handleIntent(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }
}

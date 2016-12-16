package com.cmax.bodysheild.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.cmax.bodysheild.R;
import com.cmax.bodysheild.activity.adapter.UserAdapter;
import com.cmax.bodysheild.base.BaseActivity;
import com.cmax.bodysheild.bean.ble.BLEDevice;
import com.cmax.bodysheild.bean.cache.DeviceUser;
import com.cmax.bodysheild.bean.cache.User;
import com.cmax.bodysheild.util.Constant;
import com.cmax.bodysheild.util.IntentUtils;
import com.cmax.bodysheild.util.SharedPreferencesUtil;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class UserListActivity extends BaseActivity {
	private static String TAG = UserListActivity.class.getSimpleName();
    public final static String CURRENT_USER = "currentUser";
	private String msg = null;

	@Bind(R.id.userListView)
	SwipeMenuListView userListView;

	@Bind(R.id.common_back_btn)
	ImageView backView;

	private UserAdapter userAdapter;

	private BLEDevice device;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_user_list;
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		super.initData(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		device = extras.getParcelable(TemperatureInfoActivity.EXTRA_DEVICE);
		msg = extras.getString(Constant.NEW_DEVICE_FLAG);

		userAdapter = new UserAdapter(UserListActivity.this.getLayoutInflater(),userListView,device);
		userListView.setAdapter(userAdapter);
	}

	@Override
	protected void initEvent(Bundle savedInstanceState) {
		super.initEvent(savedInstanceState);

		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				if(menu.getViewType()>0){
					// create "delete" item
					SwipeMenuItem deleteItem = new SwipeMenuItem(
							getApplicationContext());
					// set item background
					deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
							0x3F, 0x25)));
					// set item width
					deleteItem.setWidth(dp2px(90));
					// set a icon
					deleteItem.setIcon(R.mipmap.ic_delete);
					// add to menu
					menu.addMenuItem(deleteItem);
				}

			}
		};
		// set creator
		userListView.setMenuCreator(creator);

		// step 2. listener item click event
		userListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
					case 0:
						delete(position);
						break;
				}

				return true;
			}
		});

	}

	public void delete(int position){
		//TODO delete user
		UserAdapter.UserSelected userForDel = (UserAdapter.UserSelected)userAdapter.getItem(position);
		if (!userForDel.isNew()){
			List<DeviceUser> deviceUsers = SharedPreferencesUtil.getList(Constant.DEVICE_USER_LIST,	DeviceUser.class);

			List<User> users = SharedPreferencesUtil.getList(Constant.USER_LIST,	User.class);

			for (DeviceUser deviceUser : deviceUsers){
				if(userForDel.getUser().getId().equalsIgnoreCase(deviceUser.getUserId())){
					deviceUser.setUserId("");
				}
			}

			for (int i=0; i<users.size(); i++) {
				User user = users.get(i);
				if(userForDel.getUser().getId().equalsIgnoreCase(user.getId())){
					Log.i("删除用户："+TAG,user.getId());
					users.remove(i);
					userAdapter.getUsersList().remove(position);
					break;
				}
			}

			SharedPreferencesUtil.setList(Constant.USER_LIST, users);
			SharedPreferencesUtil.setList(Constant.DEVICE_USER_LIST,deviceUsers);
		}

		userAdapter.notifyDataSetChanged();
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

	@OnItemClick(R.id.userListView)
	void itemClick(int position){
		UserAdapter.UserSelected user = (UserAdapter.UserSelected)userAdapter.getItem(position);

		if (!user.isNew()){
			userAdapter.setSelected(position);
			userAdapter.notifyDataSetChanged();

			List<DeviceUser> deviceUsers = SharedPreferencesUtil.getList(Constant.DEVICE_USER_LIST,
					DeviceUser.class);

			boolean flag = true;
			for (DeviceUser deviceuser:deviceUsers) {
				if (deviceuser.getAddress().equalsIgnoreCase(device.getAddress())){
					deviceuser.setUserId(user.getUser().getId());
					flag = false;
					break;
				}
			}

			if (flag){
				DeviceUser temp = new DeviceUser();
				temp.setDeviceType(device.getDeviceType());
				temp.setAddress(device.getAddress());
				temp.setUserId(user.getUser().getId());

				deviceUsers.add(temp);
			}

			SharedPreferencesUtil.setList(Constant.DEVICE_USER_LIST, deviceUsers);
			SharedPreferencesUtil.setStringValue(device.getAddress(), user.getUser().getId());

			SharedPreferencesUtil.setLongValue(Constant.KEY_TIME_FLAG, new Date().getTime());
			if(msg != null && Constant.NEW_DEVICE_FLAG.equals(msg)){
				final Intent intent = new Intent(this, TemperatureInfoActivity.class);
				intent.putExtra(TemperatureInfoActivity.EXTRA_DEVICE, device);
				startActivity(intent);
			}
			finish();
		}else{
			IntentUtils.toLoginActivity(this,null,device);
		}
	}

    @OnClick(R.id.editBtn)    void editUser(View v){
        List<UserAdapter.UserSelected> users = userAdapter.getUsersList();
		User user=null;
		for(UserAdapter.UserSelected u : users){
            if(u.isSelected()){
				user = u.getUser();
                break;
            }
        }
        if(user!=null){
			IntentUtils.toEditProfile(this,user);
        } else{
            Toast.makeText(this, R.string.not_selected_user, Toast.LENGTH_SHORT).show();
        }

    }

	@OnClick(R.id.common_back_btn)
	void back(){
		finish();
	}


}

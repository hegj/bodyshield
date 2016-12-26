package com.cmax.bodysheild.activity.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.bean.ble.BLEDevice;
import com.cmax.bodysheild.bean.cache.DeviceUser;
import com.cmax.bodysheild.bean.cache.User;
import com.cmax.bodysheild.util.Constant;
import com.cmax.bodysheild.util.PortraitUtil;
import com.cmax.bodysheild.util.SharedPreferencesUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 * Created by allen on 15/11/11.
 */
public class UserAdapter  extends BaseAdapter {

	private ArrayList<UserSelected> usersList;
	private LayoutInflater          inflator;
	private ListView                userListView;
	private BLEDevice               device;
	private Bitmap camera;
	//private Bitmap people;
	public UserAdapter(LayoutInflater inflator, ListView userListView,BLEDevice device) {
		super();

		this.device = device;
		usersList = new ArrayList<UserSelected>();

		camera = readBitMap(userListView.getContext(), R.mipmap.camera_btn);

		//people = readBitMap(userListView.getContext(),R.mipmap.people);

		List<User> users = SharedPreferencesUtil.getList(Constant.USER_LIST,User.class);
		DeviceUser currentUser = getCurrentDeviceUser();
		for (User user:users) {
			if (user==null)
				continue;
			UserSelected us = new UserSelected();
			us.user = user;
			us.selected = false;
			if(currentUser != null){
				us.selected = user.getId().equals(currentUser.getUserId());
			}

			us.isNew = false;

			usersList.add(us);
		}

		UserSelected us = new UserSelected();
		us.user = new User();

		us.user.setUserName(userListView.getResources().getString(R.string.add_user));
		us.selected = false;
		us.isNew = true;
		usersList.add(us);

		this.inflator = inflator;
		this.userListView = userListView;
	}

	private   Bitmap readBitMap(Context context, int resId){

		BitmapFactory.Options opt = new  BitmapFactory.Options();

		opt.inPreferredConfig =  Bitmap.Config.RGB_565;

		opt.inPurgeable = true;

		opt.inInputShareable = true;

		//  获取资源图片

		InputStream is =  context.getResources().openRawResource(resId);

		Bitmap temp =  BitmapFactory.decodeStream(is, null, opt);
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return  temp;

	}


	private DeviceUser getCurrentDeviceUser(){
		List<DeviceUser> deviceUsers = SharedPreferencesUtil.getList(Constant.DEVICE_USER_LIST,
				DeviceUser.class);
		for (DeviceUser deviceUser:deviceUsers) {
			if (deviceUser.getAddress().equalsIgnoreCase(device.getAddress())){
				return deviceUser;
			}
		}

		return null;
	}

	public ArrayList<UserSelected> getUsersList() {
		return usersList;
	}

	public void setUsersList(ArrayList<UserSelected> usersList) {
		this.usersList = usersList;
	}

	public void setSelected(int position){
		for(int index=0;index<usersList.size();index++){
			usersList.get(index).selected = (position == index);
		}
	}

	@Override
	public int getCount() {
		return usersList.size();
	}

	@Override
	public Object getItem(int position) {
		return usersList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;

		UserSelected us = usersList.get(position);

		if (convertView != null) {
			viewHolder = (ViewHolder) convertView.getTag();
		} else {
			convertView = inflator.inflate(R.layout.user_item, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		}

		viewHolder.checkedView.setVisibility(View.GONE);
		if(us != null){
			if (us.user!=null) {
				viewHolder.checkedView.setVisibility(us.selected?View.VISIBLE:View.GONE);
				viewHolder.userName.setText(us.user.getUserName());
				if (us.isNew) {
					viewHolder.user.setImageBitmap(camera);
				} else {
					Bitmap bm = PortraitUtil.getBitmap(convertView.getContext(), us.getUser().getImage());
					viewHolder.user.setImageBitmap(bm);
				}
			}
		}

		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		// menu type count
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		// current menu type
		UserSelected item = (UserSelected)getItem(position);
		if(item.selected||item.isNew){
            return 0;
        }else{
            return 1;
        }
	}

	static class ViewHolder {

		@Bind(R.id.userImageBtn)
		ImageView user;

		@Bind(R.id.checkedView)
		ImageView checkedView;

		@Bind(R.id.deviceUser)
		TextView userName;

		public ViewHolder(View view) {
			ButterKnife.bind(this,view);
		}
	}

	public static class UserSelected{
		User user;
		boolean selected;
		boolean isNew;

		public User getUser() {
			return user;
		}

		public boolean isSelected() {
			return selected;
		}

		public boolean isNew() {
			return isNew;
		}
	}
}

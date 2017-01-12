package com.cmax.bodysheild.activity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cmax.bodysheild.AppContext;
import com.cmax.bodysheild.R;
import com.cmax.bodysheild.bean.ble.BLEDevice;
import com.cmax.bodysheild.bean.cache.DeviceUser;
import com.cmax.bodysheild.bean.cache.User;
import com.cmax.bodysheild.bluetooth.DeviceType;
import com.cmax.bodysheild.enums.AppModel;
import com.cmax.bodysheild.util.CommonUtil;
import com.cmax.bodysheild.util.Constant;
import com.cmax.bodysheild.util.SharedPreferencesUtil;
import com.orhanobut.logger.Logger;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by allen on 15-11-1.
 */
public class DeviceAdapter extends BaseAdapter {

	private ArrayList<BLEDevice> leDevices;
	private LayoutInflater       inflator;
	private ListView             deviceListView;


	public DeviceAdapter(LayoutInflater inflator,ListView deviceListView) {
		super();
		leDevices = new ArrayList<BLEDevice>();
		this.inflator = inflator;
		this.deviceListView = deviceListView;


		if (AppContext.appModel == AppModel.Debug){
			BLEDevice testDevice =  new BLEDevice();
			testDevice.setDeviceType(DeviceType.Tempreature);
			testDevice.setAddress(Constant.TEST_DEVICE_NAME_TEMPERATURE);
			leDevices.add(testDevice);//模拟数据
		}

	}

	public boolean containsDevice(BLEDevice device) {
		return leDevices.contains(device);
	}

	public void addDevice(BLEDevice device) {
		if (!leDevices.contains(device)) {
			leDevices.add(device);
		}
	}

	public int getBLEDevice(String address){
		for (int index=leDevices.size()-1;index>=0;index--){
			if (leDevices.get(index).getAddress().equalsIgnoreCase(address)){
				return index;
			}
		}
		return  -1;
	}

	public void clear() {
		leDevices.clear();
	}

	@Override
	public int getCount() {
		return leDevices.size();
	}

	@Override
	public Object getItem(int i) {
		return leDevices.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        BLEDevice bleDevice = (BLEDevice) getItem(position);
        if(bleDevice.isConnectionState()){
            return 1;
        }else {
            return 0;
        }
    }

    public ArrayList<BLEDevice> getLeDevices() {
		return leDevices;
	}

	public void setLeDevices(ArrayList<BLEDevice> leDevices) {
		this.leDevices = leDevices;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		// General ListView optimization code.
		if (convertView != null) {
			viewHolder = (ViewHolder) convertView.getTag();
		} else {
			convertView = inflator.inflate(R.layout.device_item, null);
			viewHolder = new ViewHolder(convertView);

			convertView.setTag(viewHolder);
		}

		BLEDevice device = leDevices.get(position);

		configItem(position,viewHolder,device);

		return convertView;
	}

	public void updateView(int itemIndex) {
		//得到第一个可显示控件的位置，
		int visiblePosition = deviceListView.getFirstVisiblePosition();
		//只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
		if (itemIndex - visiblePosition >= 0) {
			//得到要更新的item的view
			View view = deviceListView.getChildAt(itemIndex - visiblePosition);
			if (view!=null){
				//从view中取得holder
				ViewHolder holder = (ViewHolder) view.getTag();
				BLEDevice item = leDevices.get(itemIndex);

				configItem(itemIndex, holder, item);
			}
		}
	}

	private void configItem(int position, ViewHolder holder,BLEDevice device){

		DeviceUser temp = getCurrentDeviceUser(device);
		if (temp!=null){
			setUser(temp.getUserId(),device);
		}
		String name = device.getName();
		Logger.d(name);
		holder.deviceUser.setText(name);

		if (device.isConnectionState()){
			holder.deviceState.setColorFilter(holder.deviceState.getResources().getColor(R.color.blue));
			holder.valueText.setText(CommonUtil.getTemperature(device.getDeviceValue()));
		}else {
			holder.deviceState.setColorFilter(holder.deviceState.getResources().getColor(R.color.white));
			holder.valueText.setText("--.--°C");
		}
	}


	private DeviceUser getCurrentDeviceUser(BLEDevice device){
		List<DeviceUser> deviceUsers = SharedPreferencesUtil.getList(Constant.DEVICE_USER_LIST,
				DeviceUser.class);
		for (DeviceUser deviceUser:deviceUsers) {
			if (deviceUser.getAddress().equalsIgnoreCase(device.getAddress())){
				return deviceUser;
			}
		}

		return null;
	}

	private void setUser(String id,BLEDevice device){
		List<User> users = SharedPreferencesUtil.getList(Constant.USER_LIST,
				User.class);
		for (User user:users) {
			if (user.getId().equals(id)){
				device.setUserName(user.getUserName());
				break;
			}
		}
	}


	static class ViewHolder {

		@Bind(R.id.deviceState)
		ImageView deviceState;
		@Bind(R.id.deviceUser)
		TextView  deviceUser;
		@Bind(R.id.deviceType)
		ImageView deviceType;
		@Bind(R.id.valueText)
		TextView  valueText;

		public ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}
	}
}

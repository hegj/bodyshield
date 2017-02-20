package com.cmax.bodysheild.activity;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.cmax.bodysheild.R;
import com.cmax.bodysheild.activity.adapter.DeviceAdapter;
import com.cmax.bodysheild.base.BaseActivity;
import com.cmax.bodysheild.bean.ble.BLEDevice;
import com.cmax.bodysheild.bean.ble.Temperature;
import com.cmax.bodysheild.bean.cache.DeviceUser;
import com.cmax.bodysheild.bean.cache.User;
import com.cmax.bodysheild.bluetooth.BLEService;
import com.cmax.bodysheild.bluetooth.BluetoothService;
import com.cmax.bodysheild.bluetooth.BluetoothService;
import com.cmax.bodysheild.bluetooth.DeviceType;
import com.cmax.bodysheild.bluetooth.command.temperature.ContinuousDataCommand;
import com.cmax.bodysheild.bluetooth.command.temperature.TimeDataCommand;
import com.cmax.bodysheild.bluetooth.response.temperature.PresentDataResponse;
import com.cmax.bodysheild.listeners.SimpleDialogListeners;
import com.cmax.bodysheild.util.Constant;
import com.cmax.bodysheild.util.DialogUtils;
import com.cmax.bodysheild.util.IntentUtils;
import com.cmax.bodysheild.util.LogUtil;
import com.cmax.bodysheild.util.PermissionUtils;
import com.cmax.bodysheild.util.SharedPreferencesUtil;
import com.cmax.bodysheild.util.UIUtils;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class DeviceActivity extends BaseActivity {

	private final static String TAG = DeviceActivity.class.getSimpleName();

	@Bind(R.id.deviceList)
	SwipeMenuListView deviceListView;

	@Bind(R.id.scanTextBtn)
	TextView scanTextView;

	private static final int REQUEST_ENABLE_BT = 1;
	private BLEService                   bleService;
	private ServiceConnection            serviceConnection;
	private BluetoothService.LocalBinder bleBinder;
	private boolean isBind = false;

	private DeviceAdapter deviceAdapter;
	private Handler       handler;
	/**
	 * 设备扫描间隔
	 */
	private static final long    SCAN_PERIOD = 10000;
	private              long    exitTime    = 0;
	private static final int     EXIT_TIME   = 2000;
	private              boolean scanning    = false;
	private Dialog permissionDialog;
		private boolean autoScan = false;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_device;
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		super.initData(savedInstanceState);
		deviceAdapter = new DeviceAdapter(DeviceActivity.this.getLayoutInflater(), deviceListView);
		deviceListView.setAdapter(deviceAdapter);

		handler = new Handler();
		serviceConnection = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {

			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				bleBinder = (BluetoothService.LocalBinder) service;
				bleService = bleBinder.getBLEService();
				loadHistoryList();
		    	if(autoScan){
					autoScan = false;
					scanLeDevice(!scanning);

				}
			}
		};
		bindService(new Intent(DeviceActivity.this, BluetoothService.class), serviceConnection, Context.BIND_AUTO_CREATE);
		isBind = true;
		initListViewMenu();
	}
	private void delete(int position){
		//Todo delete device
		BLEDevice device = (BLEDevice)deviceAdapter.getItem(position);
		List<DeviceUser> deviceUsers = SharedPreferencesUtil.getList(Constant.DEVICE_USER_LIST,
				DeviceUser.class);
		for(int i=0; i<deviceUsers.size(); i++){
			DeviceUser deviceUser = deviceUsers.get(i);
			if(device.getAddress().equalsIgnoreCase(deviceUser.getAddress())){
				Log.i(TAG, "删除设备：" + device.getAddress());
				deviceUsers.remove(i);
			}
		}

		//断开那个连接
		bleService.disconnect(device.getAddress());
		deviceAdapter.getLeDevices().remove(position);
		SharedPreferencesUtil.setList(Constant.DEVICE_USER_LIST, deviceUsers);
		deviceAdapter.notifyDataSetChanged();
	}

	Runnable scanTask = new Runnable() {
		@Override
		public void run() {
			Logger.d("----- "+scanning);
			if (!scanning) {
				Logger.d("-----scanTask)");
				scanLeDevice(true);
			}
		}
	};

	private void loadHistoryList(){

		List<DeviceUser> deviceUsers = SharedPreferencesUtil.getList(Constant.DEVICE_USER_LIST,
				DeviceUser.class);

		List<User> users = SharedPreferencesUtil.getList(Constant.USER_LIST,
				User.class);

		Map<String,Float> connectedDevices = bleService.getConnectedDevicesValue();

		for(DeviceUser deviceUser:deviceUsers){
			BLEDevice device = new BLEDevice();
			device.setAddress(deviceUser.getAddress());
			device.setDeviceType(DeviceType.Tempreature);
			device.setConnectionState(false);
			device.setName(deviceUser.getName());
			for (User user:users) {
				if (deviceUser.getUserId().equals(user.getId())){
					device.setUserName(user.getUserName());
					break;
				}
			}

			if (connectedDevices.containsKey(device.getAddress())){
				device.setConnectionState(true);

				Float value = connectedDevices.get(device.getAddress());
				if (value != null){
					device.setDeviceValue(value);
				}
			}

			deviceAdapter.addDevice(device);
		}
		deviceAdapter.notifyDataSetChanged();
	}
	private void initListViewMenu() {
		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				int vt = menu.getViewType();
				if(vt==0) {
					// create "delete" item
					SwipeMenuItem deleteItem = new SwipeMenuItem(
							getApplicationContext());
					// set item background
					deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
							0x3F, 0x25)));
					// set item width
					deleteItem.setWidth(UIUtils.dp2px(90));
					// set a icon
					deleteItem.setIcon(R.mipmap.ic_delete);
					// add to menu
					menu.addMenuItem(deleteItem);
				}
			}
		};
		// set creator
		deviceListView.setMenuCreator(creator);

		// step 2. listener item click event
		deviceListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
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


	/**
	 * 点击扫描
	 * @param scanTextView 刷新按钮
	 */
	@OnClick(R.id.scanTextBtn)
	void clickScanText(TextView scanTextView) {
/*		Intent intent = new Intent(this,SettingActivity.class);
		BLEDevice device = new BLEDevice();
		intent.putExtra(TemperatureInfoActivity.EXTRA_DEVICE, device);
		startActivity(intent);*/
		scanLeDevice(!scanning);
		// IntentUtils.toEditProfile(this,null);
		//IntentUtils.toLoginActivity(this,null,null);
	}

	private void scanLeDevice(final boolean enable) {
		if (enable) {
			PermissionUtils.askLocationInfo(this,new PermissionUtils.PermissionListener() {
				@Override
				public void onGranted() {
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							scanning = false;
							// 停止扫描设备
							bleService.scanLeDevice(!enable);

						}
					}, SCAN_PERIOD);

					scanning = true;
					scanTextView.setText(R.string.menu_stop);
					// 开始扫描设备
					bleService.scanLeDevice(enable);
				}

				@Override
				public void onDenied(String[] permissions, String permissionDesc, boolean isCancle, int requestCode) {
					if (permissionDialog == null) {
						permissionDialog = DialogUtils.showRequestPermissionDialog(DeviceActivity.this, permissionDesc, requestCode, isCancle, permissions);
					}else {
						permissionDialog.show();
					}
				}
			});

		} else {
			scanning = false;
			// 停止扫描设备
			bleService.scanLeDevice(!enable);
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					Map<String, Float> deviceMap = bleService.getConnectedDevicesValue();
					if (deviceMap == null || deviceMap.size() <= 0) {
						scanLeDevice(true);
					}
				}
			}, 1000);
		}
	}

	@OnItemClick(R.id.deviceList)
	void  clickDeviceItem(int position){
		BLEDevice device = (BLEDevice)deviceAdapter.getItem(position);

		if (device != null){

			List<DeviceUser> deviceUsers = SharedPreferencesUtil.getList(Constant.DEVICE_USER_LIST,
					DeviceUser.class);
			List<User> users = SharedPreferencesUtil.getList(Constant.USER_LIST,User.class);

			boolean flag = false;
			for (DeviceUser deviceuser:deviceUsers) {
				if (deviceuser.getAddress().equalsIgnoreCase(device.getAddress())){
					flag = true;
					break;
				}
			}

			Class clazz;
			String msg = null;
			if(!flag){
				if(users != null && !users.isEmpty()){
					clazz = UserListActivity.class;
					msg = Constant.NEW_DEVICE_FLAG;
				}else {
					clazz = RegisterActivity.class;
				}

			}else{
				clazz = TemperatureInfoActivity.class;
			}
			final Intent intent = new Intent(this, clazz);
			intent.putExtra(TemperatureInfoActivity.EXTRA_DEVICE, device);
			if(msg != null){
				intent.putExtra(Constant.NEW_DEVICE_FLAG,msg);
			}

			scanning = false;
			handler.removeCallbacks(scanTask);
			startActivity(intent);
		}
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		PermissionUtils.onRequestPermissionsResult(requestCode,permissions,grantResults);
	}
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(notificationReceiver, makeIntentFilter());

		if (!isBind){
			autoScan = false;
			bindService(new Intent(DeviceActivity.this, BluetoothService.class), serviceConnection, Context.BIND_AUTO_CREATE);
			isBind = true;
		}


		//若非第一次加载，尝试发送持续读数命令
		List<BLEDevice> devices = deviceAdapter.getLeDevices();

		if (bleService != null) {

			Map<String,Float> connectedDevices = bleService.getConnectedDevicesValue();

			for (BLEDevice device : devices) {
				if (connectedDevices.containsKey(device.getAddress())){
					device.setConnectionState(true);
					Float value = connectedDevices.get(device.getAddress());
					if (value != null){
						device.setDeviceValue(value);
					}
				}
			}
			deviceAdapter.notifyDataSetChanged();

			//由于发蓝牙命令很可能造成时间等待，所以循环多一次
			for(BLEDevice device : devices){

				if (device.getDeviceType() == DeviceType.Tempreature) {
					ContinuousDataCommand command = new ContinuousDataCommand(device.getAddress(),
							ContinuousDataCommand.ReqeustType.StartModel);
					bleService.executeCommand(command);
				}
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(isBind) {
			unbindService(serviceConnection);
			isBind = false;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(isBind) {
			unbindService(serviceConnection);
			isBind = false;
		}
		unregisterReceiver(notificationReceiver);
		if (permissionDialog!=null &&permissionDialog.isShowing() )permissionDialog.dismiss();
		permissionDialog=null;
		Log.i(TAG,"destory");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			// 判断是否在两秒之内连续点击返回键，是则退出，否则不退出
			if (System.currentTimeMillis() - exitTime > EXIT_TIME) {
				Toast.makeText(getApplicationContext(), getString(R.string.press_for_exit),
						Toast.LENGTH_SHORT).show();
				// 将系统当前的时间赋值给exitTime
				exitTime = System.currentTimeMillis();
			} else {
				exitApp();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 退出应用程序的方法，发送退出程序的广播
	 */
	private void exitApp() {
		Intent intent = new Intent();
		intent.setAction(Constant.EXIT);
		this.sendBroadcast(intent);
	}

	private final BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (BluetoothService.ACTION_BLE_UNSUPPORT.equals(action)) {
				//设备不支持BLE
				Toast.makeText(DeviceActivity.this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
			} else if (BluetoothService.ACTION_BLUETOOTH_UNSUPPORT.equals(action)) {
				//设备没有蓝牙
				Toast.makeText(DeviceActivity.this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
			} else if (BluetoothService.ACTION_BLUETOOTH_UNABLED.equals(action)) {
				//蓝牙没开启
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

			} else if (BluetoothService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				//发现服务
			} else if (BluetoothService.ACTION_GATT_CONNECTED.equals(action)) {
				//连接上设备
				BluetoothDevice device = intent.getParcelableExtra(BluetoothService.EXTRA_DEVICE);

				int index = deviceAdapter.getBLEDevice(device.getAddress());
				if (index > -1) {
					final BLEDevice bleDevice =
							((BLEDevice) deviceAdapter.getItem(index));

					bleDevice.setConnectionState(true);
					deviceAdapter.updateView(index);

					final String address = device.getAddress();

					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							bleService.executeCommand(new TimeDataCommand(address));
						}
					}, 1200);

				}

			} else if (BluetoothService.ACTION_GATT_DISCONNECTED.equals(action)) {
				//设备断开
				BluetoothDevice device = intent.getParcelableExtra(BluetoothService.EXTRA_DEVICE);

				int index = deviceAdapter.getBLEDevice(device.getAddress());
				if (index > -1) {
					((BLEDevice) deviceAdapter.getItem(index)).setConnectionState(false);
					deviceAdapter.updateView(index);
				}
				//尝试扫描一次
//				handler.post(scanTask);
			} else if (BluetoothService.ACTION_BLE_NEW_DEVICE.equals(action)) {
				//发现新设备
				BluetoothDevice device = intent.getParcelableExtra(BluetoothService.EXTRA_DEVICE);
				LogUtil.i(TAG, "find new device:" + device.getName());
				final BLEDevice bleDevice = new BLEDevice();
				bleDevice.setAddress(device.getAddress());
				bleDevice.setName(device.getName());
				bleDevice.setUserName(getString(R.string.device_default_user));
				bleDevice.setDeviceType(DeviceType.Tempreature);

				if (!deviceAdapter.containsDevice(bleDevice)) {
					deviceAdapter.addDevice(bleDevice);

				} else {
					int i = deviceAdapter.getBLEDevice(bleDevice.getAddress());
					if (i > -1) {
						BLEDevice temp = (BLEDevice) deviceAdapter.getItem(i);
						temp.setName(device.getName());
					}
				}
				deviceAdapter.notifyDataSetChanged();
				bleService.connect(bleDevice.getAddress(), bleDevice.getDeviceType());
			} else if (BluetoothService.ACTION_BLE_FINISH_SCANNING.equals(action)) {
				//扫描结束
				Map<String,Float> m = bleService.getConnectedDevicesValue();
				if(m == null || m.isEmpty()){
					Logger.d("m.isEmpty()");
					 handler.post(scanTask);
				}else {
					scanTextView.setText(R.string.menu_scan);
					Logger.d("-----R.string.menu_scan)");
				}
			} else if (PresentDataResponse.ACTION_REALTIME_TEMPRETURE.equals(action)) {
				//发现读数
				Parcelable temp = intent.getParcelableExtra(BluetoothService.EXTRA_DEVICE);
				if (temp instanceof BluetoothDevice) {
					BluetoothDevice device = intent.getParcelableExtra(BluetoothService.EXTRA_DEVICE);

					Temperature temperature = PresentDataResponse.getResponseResult(intent);

					int index = deviceAdapter.getBLEDevice(device.getAddress());
					if (index > -1 && temperature != null) {
						BLEDevice bleDevice = ((BLEDevice) deviceAdapter.getItem(index));
						bleDevice.setDeviceValue(temperature.getValue());
						bleDevice.setConnectionState(true);
						deviceAdapter.updateView(index);
					}
				}
			}
		}
	};

	private static IntentFilter makeIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothService.ACTION_BLE_UNSUPPORT);
		intentFilter.addAction(BluetoothService.ACTION_BLUETOOTH_UNSUPPORT);
		intentFilter.addAction(BluetoothService.ACTION_BLUETOOTH_UNABLED);
		intentFilter.addAction(BluetoothService.ACTION_BLE_NEW_DEVICE);
		intentFilter.addAction(BluetoothService.ACTION_BLE_FINISH_SCANNING);
		intentFilter.addAction(PresentDataResponse.ACTION_REALTIME_TEMPRETURE);

		return intentFilter;
	}

}

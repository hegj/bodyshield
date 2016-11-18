package com.cmax.bodysheild.alert;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.bean.ble.BLEDevice;
import com.cmax.bodysheild.bean.ble.Temperature;
import com.cmax.bodysheild.bluetooth.BluetoothService;
import com.cmax.bodysheild.bluetooth.response.temperature.PresentDataResponse;
import com.cmax.bodysheild.util.Constant;
import com.cmax.bodysheild.util.SharedPreferencesUtil;

/**
 * BLE服务
 * Created by allen on 15-10-26.
 */
public class AlertService extends Service {

	private final static String  TAG     = AlertService.class.getSimpleName();
	private final IBinder mBinder = new LocalBinder();
	private BLEDevice device;
	private float hightFever;
	private float lowFever;	//
	private int alertInterval;	//告警间隔
	private int vibration;	//震动
	private static final long[] pattern = {1000, 2000, 1000, 2000, 1000, 2000, 1000, 2000, 1000, 2000}; // OFF/ON/OFF/ON......
	private static int alertTimes = 3; //震动次数
	private int alertTimesIndex = 0;

	private Handler handler;
	private Vibrator vibrator;
	private boolean isAlerting = false;	//是否在告警
	private boolean isRegister = false; //是否已注册广播接收器

	private AudioManager audioMgr = null; // Audio管理器，用了控制音量
	private AssetManager assetMgr = null; // 资源管理器
	private SoundPool sp;
	int load;		//声音资源
	int maxVolume;	//最大音量
	int settingVolume;		//设置的音量

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		hightFever = SharedPreferencesUtil.getFloatValue(Constant.KEY_HIGHT_FEVER,39);
		lowFever = SharedPreferencesUtil.getFloatValue(Constant.KEY_LOW_FEVER, 38);
		alertInterval = SharedPreferencesUtil.getIntValue(Constant.KEY_ALERT_INTERVAL, 10);
		settingVolume = SharedPreferencesUtil.getIntValue(Constant.KEY_VOLUME, 50);
		vibration = SharedPreferencesUtil.getIntValue(Constant.KEY_VIBRATION,0);
		registerReceiver(notificationReceiver, makeIntentFilter());
		isRegister = true;
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		handler = new Handler();
//		initPlayWork();
		Log.i(TAG, "ALERTservice创建成功！！！");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (isRegister) {
			unregisterReceiver(notificationReceiver);
			isRegister = false;
		}
	}

	public void setHightFever(float value){
		hightFever = value;
	}

	public void setLowFever(float value){
		lowFever = value;
	}

	public void refresh(){
		hightFever = SharedPreferencesUtil.getFloatValue(Constant.KEY_HIGHT_FEVER,39);
		lowFever = SharedPreferencesUtil.getFloatValue(Constant.KEY_LOW_FEVER,36);
		alertInterval = SharedPreferencesUtil.getIntValue(Constant.KEY_ALERT_INTERVAL, 10);
		settingVolume = SharedPreferencesUtil.getIntValue(Constant.KEY_VOLUME, 50);
		vibration = SharedPreferencesUtil.getIntValue(Constant.KEY_VIBRATION,0);
		adjustVolume();
		Log.i(TAG,"刷新各项告警参数！");
	}

	public void setDevice(BLEDevice device){
		this.device = device;
		Log.i(TAG,"设置device成功！");
	}
	public void alertByVibrate(){
		if(1 == vibration){
			vibrator.vibrate(pattern, -1);
			Log.i(TAG, "正在震动");
		}
	}

	/**
	 * 初始化播放器、音量数据等相关工作
	 */
	private void initPlayWork() {
        if(sp != null){
            sp.release();
        }
		if(audioMgr == null){
			audioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		}

		// 获取最大音乐音量
		maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, AudioManager.FLAG_PLAY_SOUND);
		assetMgr = this.getAssets();
		adjustVolume();
		sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		load = sp.load(this, R.raw.sounds,1);
	}

	public void playSound(){
        initPlayWork();
        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                float volumnCurrent = audioMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
                soundPool.play(load, volumnCurrent, volumnCurrent, 1, 5, 1f);
            }
        });


	}
	/**
	 * 调整音量
	 */
	private void adjustVolume() {
		if(audioMgr == null){
			audioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		}
		int v =  (int)((float)settingVolume/100f*(float)maxVolume);
		audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, v,	AudioManager.FLAG_PLAY_SOUND);
	}

	private Runnable alertRunnable = new Runnable() {
		@Override
		public void run() {
			if(alertTimesIndex < alertTimes){
				playSound();
				alertByVibrate();
				handler.postDelayed(this, alertInterval*60*1000);
				alertTimesIndex++;

			}else {
				alertTimesIndex = 0;
				isAlerting = false;
			}
		}
	};

	private final BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (PresentDataResponse.ACTION_REALTIME_TEMPRETURE.equals(action)) {
				Temperature receiverTemperature = intent.getParcelableExtra(PresentDataResponse.EXTRA_PRESENT_DATA);
				String address = intent.getStringExtra(BluetoothService.EXTRA_ADDRESS);
				if (device != null && device.getAddress().equals(address)) {
					float y = receiverTemperature.getValue();
					if(y > lowFever && !isAlerting){
						handler.postDelayed(alertRunnable, 1000);
						isAlerting = true;
						Log.i(TAG,"发烧告警！！！");
					}
				}
			}else if (Constant.EXIT.equals(action)) {
				handler.removeCallbacks(alertRunnable);
				stopSelf();
			}
		}
	};


	private static IntentFilter makeIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(PresentDataResponse.ACTION_REALTIME_TEMPRETURE);
		intentFilter.addAction(Constant.EXIT);
		return intentFilter;
	}

	public class LocalBinder extends Binder {
		/**
		 * 获取AlertService服务
		 * @return
		 */
		public AlertService getService() {
			return AlertService.this;
		}
	}


}

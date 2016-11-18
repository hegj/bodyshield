/**
 * 
 */
package com.cmax.bodysheild.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.cmax.bodysheild.AppContext;

import java.util.ArrayList;

public class SharedPreferencesUtil {
	
	private static final String	FILE_PATH = "data";
	private static final SharedPreferences sharedPreferences = getSharedPreferences();

	private static SharedPreferences getSharedPreferences() {
		return AppContext.getContext().getSharedPreferences(FILE_PATH, Activity.MODE_PRIVATE);
	}
	
	public static void setStringValue(String key, String value) {
		synchronized (sharedPreferences) {
			Editor editor = sharedPreferences.edit();
			editor.putString(key, value);
			editor.apply();
		}
	}

	public static String getStringValue(String key, String defaultValue) {
		synchronized (sharedPreferences) {
			return sharedPreferences.getString(key, defaultValue);
		}
	}

	public static void setIntValue(String key, int value) {
		synchronized (sharedPreferences) {
			Editor editor = sharedPreferences.edit();
			editor.putInt(key, value);
			editor.apply();
		}
	}

	public static int getIntValue(String key, int defaultValue) {
		synchronized (sharedPreferences) {
			return sharedPreferences.getInt(key, defaultValue);
		}
	}

	public static void setLongValue(String key, long value) {
		synchronized (sharedPreferences) {
			Editor editor = sharedPreferences.edit();
			editor.putLong(key, value);
			editor.apply();
		}
	}

	public static long getLongValue(String key, long defaultValue) {
		synchronized (sharedPreferences) {
			return sharedPreferences.getLong(key, defaultValue);
		}
	}

	public static void setFloatValue(String key, float value) {
		synchronized (sharedPreferences) {
			Editor editor = sharedPreferences.edit();
			editor.putFloat(key, value);
			editor.apply();
		}
	}

	public static float getFloatValue(String key, float defaultValue) {
		synchronized (sharedPreferences) {
			return sharedPreferences.getFloat(key, defaultValue);
		}
	}

	public static <T> ArrayList<T> getList(String key,Class<T> clazz) {

		String temp = getStringValue(key, "[]");
		return JsonUtil.fromJsonList(temp,clazz);
	}

	public static void setList(String key,Object obj) {
		setStringValue(key,JsonUtil.toJsonString(obj));
	}
}

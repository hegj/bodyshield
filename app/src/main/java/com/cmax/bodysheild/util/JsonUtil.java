/**
 * 
 */
package com.cmax.bodysheild.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * @author Allen
 * @date 2015年7月1日 下午7:59:43 
 */
public class JsonUtil {
	private static final Gson	GSON	= new Gson();
	
	public static Gson getGsion() {
		return GSON;
	}

	public static <T> T toBeanFromJson(Class<T> clazz, String jsonStr) {
		return GSON.fromJson(jsonStr, clazz);
	}

	public static <T> List<T> toBeanListFromJson(Class<T> clazz, String jsonStr) {
		return GSON.fromJson(jsonStr, new TypeToken<List<T>>() {
		}.getType());
	}

	public static <T> ArrayList<T> fromJsonList(String json, Class<T> cls) {
		ArrayList<T> mList = new ArrayList<T>();
		JsonArray array = new JsonParser().parse(json).getAsJsonArray();
		for (final JsonElement elem : array) {
			mList.add(GSON.fromJson(elem, cls));
		}
		return mList;
	}

	public static String toJsonString(Object bean) {
		return GSON.toJson(bean);
	}
}

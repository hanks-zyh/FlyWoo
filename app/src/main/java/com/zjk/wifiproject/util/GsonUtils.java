package com.zjk.wifiproject.util;

import com.google.gson.Gson;

/**
 * json与bean的相互转化
 */
public class GsonUtils {

	public static  <T> T jsonToBean(String jsonResult,Class<T> clz){
		Gson gson = new Gson();
		T t = gson.fromJson(jsonResult, clz);
		return t;
	}

	public static  <T> String beanToJson(T clz){
		Gson gson = new Gson();
		return gson.toJson(clz);
	}
}

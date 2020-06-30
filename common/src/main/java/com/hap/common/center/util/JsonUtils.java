package com.hap.common.center.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class JsonUtils {

    public JsonUtils() {
    }

    public static String toJson(Object obj) {
        return JSON.toJSONString(obj);
    }

    public static <T> T fromJson(String json, Class<T> c) {
        return JSON.parseObject(json, c);
    }

    public static List fromJsonToList(String json, Class c) {
        return JSONObject.parseArray(json, c);
    }

}

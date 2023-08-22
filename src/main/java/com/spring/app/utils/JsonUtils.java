package com.spring.app.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class JsonUtils {
	private static final long serialVersionUID = 1L;
	private static ObjectMapper jsonMapper;

	protected static final ObjectMapper getMapper() {
		if (jsonMapper == null) {
			jsonMapper = new ObjectMapper();
		}
		synchronized (jsonMapper) {
			return jsonMapper;
		}
	}

	public static <T> String toJson(T object) {
		try {
			return getMapper().writeValueAsString(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static <T> T convertObject(Object data, Class<T> className) {
		if (data instanceof Map) {
			Map<Object, Object> temp = new LinkedHashMap((Map) data);
			Object id = temp.get("_id");
			if (id != null) {
				if (id instanceof ObjectId) {
					temp.put("id", ((ObjectId) id).toHexString());
				} else {
					temp.put("id", id);
				}
				temp.remove("_id");
			}
			return getMapper().convertValue(temp, className);
		} else {
			return getMapper().convertValue(data, className);
		}
	}

	public static <T> Object convertJsonObject(String json, Class<T> clazz) {
		Gson gson = new Gson();
		return gson.fromJson(json, clazz.getClass());
	}
}

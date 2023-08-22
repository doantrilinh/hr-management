package com.spring.app.utils;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
public class ResponseUtils {

	@Data
	public class ResultEntity {
		private String message;
		private boolean success;
		private int code;
		private Object data;
	}
	
	public ResponseEntity Success(Object any) {
		ResultEntity result = new ResultEntity();
		result.code = 200;
		result.success = true;
		result.message = "Success";
		result.data = any;
		
		return ResponseEntity.ok(result);
	}
	
	public ResponseEntity Error(String message, int code) {
		ResultEntity result = new ResultEntity();
		result.code = code;
		result.message = message;
		result.success = false;
		
		return ResponseEntity.ok(result);
	}
	
}

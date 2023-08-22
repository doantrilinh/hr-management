package com.spring.app.utils;

public enum HttpStatusUtils {

    NO_PARAM("No param", 2001),
    NO_CONTENT("No content", 2004),
    EMAIL_EXISTS("Email đã tồn tại !", 2002),
    INVALID_EMAIL("Email không hợp lệ !", 2003),
    PHONE_EXISTS("SĐT đã tồn tại !", 2004),
    INVALID_SALARY("Lương không hợp lệ !", 2005),
    ;
    
	private final int code;
    private final String message;
    
    private HttpStatusUtils(String message, int code) {
        this.message = message;
        this.code = code;
    }
    

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}

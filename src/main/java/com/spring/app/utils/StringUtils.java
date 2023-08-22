package com.spring.app.utils;

import java.util.Random;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class StringUtils {
	
	private static final String PATTERN_EMAIL = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" 
	        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$" ;

	public static String generateEmployeeID() {
		return "NV." + randomDigit();
	}

	private static String randomDigit() {
		Random random = new Random();
		int number = random.nextInt(999999);
		return String.format("%06d", number);
	}
	
	public static boolean isValidMail(String email) {
		return Pattern.compile(PATTERN_EMAIL)
				.matcher(email)
				.matches();
	}
}

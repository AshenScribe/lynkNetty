package org.example.utils;

import java.util.Base64;

public class Base64Utils {
    public static class Base64Exception extends RuntimeException {
        public Base64Exception(String message) { super(message); }
    }

    public static String decodeBase64(String input) {
        try {
            return new String(Base64.getDecoder().decode(input));
        } catch (IllegalArgumentException e) {
            throw new Base64Exception("Invalid Base64 encoding: " + input);
        }
    }

	public static String encodeBase64(String input) {
		try {
			return new String(Base64.getEncoder().encode(input.getBytes()));
		} catch (IllegalArgumentException e) {
			throw new Base64Exception("Invalid Base64 encoding: " + input);
		}
	}
}

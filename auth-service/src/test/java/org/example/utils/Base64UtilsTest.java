package org.example.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Base64UtilsTest {

	@Test
	void testEncodeAndDecodeBase64() {
		String original = "Hello World!";
		String encoded = Base64Utils.encodeBase64(original);
		String decoded = Base64Utils.decodeBase64(encoded);

		assertEquals(original, decoded);
	}

	@Test
	void testEncodeBase64() {
		String result = Base64Utils.encodeBase64("test");
		assertEquals("dGVzdA==", result);
	}

	@Test
	void testDecodeBase64() {
		String result = Base64Utils.decodeBase64("dGVzdA==");
		assertEquals("test", result);
	}

	@Test
	void testDecodeBase64WithSpecialCharacters() {
		String original = "user@example.com:pass123!@#";
		String encoded = Base64Utils.encodeBase64(original);
		String decoded = Base64Utils.decodeBase64(encoded);

		assertEquals(original, decoded);
	}

	@Test
	void testDecodeBase64WithUnicode() {
		String original = "用户:密码";
		String encoded = Base64Utils.encodeBase64(original);
		String decoded = Base64Utils.decodeBase64(encoded);

		assertEquals(original, decoded);
	}

	@Test
	void testDecodeBase64WithEmptyString() {
		String result = Base64Utils.decodeBase64("");
		assertEquals("", result);
	}

	@Test
	void testEncodeBase64WithEmptyString() {
		String result = Base64Utils.encodeBase64("");
		assertEquals("", result);
	}

	@Test
	void testDecodeBase64WithInvalidInputThrowsException() {
		assertThrows(Base64Utils.Base64Exception.class, () -> {
			Base64Utils.decodeBase64("!!!invalid!!!");
		});
	}

	@Test
	void testDecodeBase64WithMalformedInput() {
		assertThrows(Base64Utils.Base64Exception.class, () -> {
			Base64Utils.decodeBase64("not-base64!");
		});
	}

	@Test
	void testEncodeAndDecodeLongString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 1000; i++) {
			sb.append("a");
		}
		String original = sb.toString();
		String encoded = Base64Utils.encodeBase64(original);
		String decoded = Base64Utils.decodeBase64(encoded);

		assertEquals(original, decoded);
	}

	@ParameterizedTest
	@CsvSource({
			"hello, aGVsbG8=",
			"world, d29ybGQ=",
			"base64, YmFzZTY0",
			"123456, MTIzNDU2"
	})
	void testEncodeBase64Parameterized(String input, String expected) {
		assertEquals(expected, Base64Utils.encodeBase64(input));
	}

	@ParameterizedTest
	@CsvSource({
			"aGVsbG8=, hello",
			"d29ybGQ=, world",
			"YmFzZTY0, base64",
			"MTIzNDU2, 123456"
	})
	void testDecodeBase64Parameterized(String input, String expected) {
		assertEquals(expected, Base64Utils.decodeBase64(input));
	}
}
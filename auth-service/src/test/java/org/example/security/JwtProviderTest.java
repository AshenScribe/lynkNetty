package org.example.security;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {

	@BeforeAll
	static void setup() {
		JwtTestUtils.ensureInitialized();
	}

	@Test
	void testCreateToken_Success() {
		String token = JwtProvider.getInstance().createToken("testuser");
		assertNotNull(token);
		assertFalse(token.isEmpty());
		assertEquals(3, token.split("\\.").length);
	}

	@Test
	void testDecodeToken_Success() {
		String username = "alice@example.com";
		String token = JwtProvider.getInstance().createToken(username);
		String decoded = JwtProvider.getInstance().decodeToken(token);
		assertEquals(username, decoded);
	}

	@Test
	void testDecodeToken_WithSpecialCharacters() {
		String username = "user+123@test-domain.co.in";
		String token = JwtProvider.getInstance().createToken(username);
		String decoded = JwtProvider.getInstance().decodeToken(token);
		assertEquals(username, decoded);
	}

	@Test
	void testInvalidToken_ThrowsException() {
		assertThrows(Exception.class, () -> {
			JwtProvider.getInstance().decodeToken("invalid.token.here");
		});
	}

	@Test
	void testTamperedToken_ThrowsException() {
		String originalToken = JwtProvider.getInstance().createToken("user");
		String[] parts = originalToken.split("\\.");
		String tamperedToken = parts[0] + ".tampered." + parts[2];

		assertThrows(Exception.class, () -> {
			JwtProvider.getInstance().decodeToken(tamperedToken);
		});
	}

	@Test
	void testEmptyToken_ThrowsException() {
		assertThrows(Exception.class, () -> {
			JwtProvider.getInstance().decodeToken("");
		});
	}

	@Test
	void testNullUsername_ThrowsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			JwtProvider.getInstance().createToken(null);
		});
	}

	@Test
	void testMultipleTokens_Sequential() {
		JwtProvider provider = JwtProvider.getInstance();

		for (int i = 0; i < 10; i++) {
			String username = "user" + i;
			String token = provider.createToken(username);
			String decoded = provider.decodeToken(token);
			assertEquals(username, decoded);
		}
	}

	@Test
	void testConcurrentTokenCreation() throws InterruptedException {
		JwtProvider provider = JwtProvider.getInstance();
		Thread[] threads = new Thread[20];
		String[] tokens = new String[20];

		for (int i = 0; i < 20; i++) {
			final int index = i;
			threads[index] = new Thread(() -> {
				tokens[index] = provider.createToken("concurrent_user_" + index);
			});
			threads[index].start();
		}

		for (Thread thread : threads) {
			thread.join();
		}

		for (int i = 0; i < 20; i++) {
			assertNotNull(tokens[i]);
			assertEquals("concurrent_user_" + i, provider.decodeToken(tokens[i]));
		}
	}

	@Test
	void testTokenExpirationValue() {
		assertEquals(300, JwtProvider.TOKEN_EXPIRATION_TIME_IN_SECOND);
		assertEquals(5 * 60, JwtProvider.TOKEN_EXPIRATION_TIME_IN_SECOND);
	}

	@AfterAll
	static void tearDown() {
		JwtTestUtils.cleanup();
	}
}
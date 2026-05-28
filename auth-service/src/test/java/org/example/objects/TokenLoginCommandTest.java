package org.example.objects;

import org.example.db.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class TokenLoginCommandTest {

	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		userRepository = Mockito.mock(UserRepository.class);
	}

	@Test
	void testTokenLoginCommandCreation() {
		TokenLoginCommand cmd = new TokenLoginCommand("token-abc-123");

		assertEquals("token-abc-123", cmd.token());
	}

	@Test
	void testExecuteAuthentication() {
		TokenLoginCommand cmd = new TokenLoginCommand("token123");
		StepVerifier.create(cmd.executeAuthentication(userRepository))
				.expectNext("Success");
	}

	@Test
	void testTokenLoginCommandWithEmptyToken() {
		TokenLoginCommand cmd = new TokenLoginCommand("");

		assertEquals("", cmd.token());
		assertNotNull(cmd.executeAuthentication(userRepository));
	}

	@Test
	void testTokenLoginCommandWithNull() {
		TokenLoginCommand cmd = new TokenLoginCommand(null);

		assertNull(cmd.token());
	}

	@Test
	void testTokenLoginCommandEquality() {
		TokenLoginCommand cmd1 = new TokenLoginCommand("token123");
		TokenLoginCommand cmd2 = new TokenLoginCommand("token123");
		TokenLoginCommand cmd3 = new TokenLoginCommand("token456");

		assertEquals(cmd1, cmd2);
		assertNotEquals(cmd1, cmd3);
	}

	@Test
	void testTokenLoginCommandHashCode() {
		TokenLoginCommand cmd1 = new TokenLoginCommand("token123");
		TokenLoginCommand cmd2 = new TokenLoginCommand("token123");

		assertEquals(cmd1.hashCode(), cmd2.hashCode());
	}

	@Test
	void testTokenLoginCommandToString() {
		TokenLoginCommand cmd = new TokenLoginCommand("token123");
		String str = cmd.toString();

		assertTrue(str.contains("token123"));
	}
}
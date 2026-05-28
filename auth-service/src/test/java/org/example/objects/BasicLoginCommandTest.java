package org.example.objects;

import org.example.db.entity.User;
import org.example.db.repository.UserRepository;
import org.example.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class BasicLoginCommandTest {

	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		userRepository = Mockito.mock(UserRepository.class);
	}

	@Test
	void testExecuteAuthentication() {
		String hashedPassword = BCrypt.hashpw("pass", BCrypt.gensalt());
		User mockUser = new User("user", hashedPassword);

		Mockito.when(userRepository.findUserByUsername("user"))
				.thenReturn(Mono.just(mockUser));

		BasicLoginCommand cmd = new BasicLoginCommand("user", "pass");
		Mono<String> result = cmd.executeAuthentication(userRepository);

		StepVerifier.create(result)
				.expectNextMatches(token -> token.startsWith("eyJ"));
	}

	@Test
	void testBasicLoginCommandWithEmptyCredentials() {
		BasicLoginCommand cmd = new BasicLoginCommand("", "");
		Mockito.when(userRepository.findUserByUsername("")).thenReturn(Mono.empty());
		StepVerifier.create(cmd.executeAuthentication(userRepository))
				.expectError(UserNotFoundException.class);
	}
}
package org.example.authenticator;

import org.example.authenticator.requests.AuthCredentials;
import org.example.authenticator.requests.BasicAuthRequest;
import org.example.db.repository.UserRepository;
import org.example.exception.UserNotFoundException;
import org.example.security.JwtProvider;
import org.mindrot.jbcrypt.BCrypt;
import reactor.core.publisher.Mono;

public class BasicAuthStrategy implements AuthStrategy {
	@Override
	public Mono<String> authenticate(AuthCredentials credentials, UserRepository userRepository) {
		BasicAuthRequest authRequest = (BasicAuthRequest) credentials;
		return userRepository.findUserByUsername(authRequest.getUsername())
				.flatMap(user -> {
					if (BCrypt.checkpw(authRequest.getPassword(), user.password())) {
						return Mono.just(JwtProvider.getInstance().createToken(authRequest.getUsername()));
					} else {
						return Mono.error(new RuntimeException("Invalid Password"));
					}
				}).switchIfEmpty(Mono.error(new UserNotFoundException("User not found")));
	}
}

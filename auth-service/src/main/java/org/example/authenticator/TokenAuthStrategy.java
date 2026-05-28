package org.example.authenticator;

import org.example.authenticator.requests.AuthCredentials;
import org.example.authenticator.requests.TokenAuthRequest;
import org.example.db.repository.UserRepository;
import org.example.security.JwtProvider;
import reactor.core.publisher.Mono;

public class TokenAuthStrategy implements AuthStrategy {
	@Override
	public Mono<String> authenticate(AuthCredentials credentials, UserRepository userRepository) {
		TokenAuthRequest authRequest = (TokenAuthRequest) credentials;
		try {
			String username = JwtProvider.decodeToken(authRequest.getToken());
			return userRepository.findUserByUsername(username)
					.map(user -> JwtProvider.createToken(user.username()))
					.switchIfEmpty(Mono.error(new RuntimeException("User not found for token")));
		} catch (Exception e) {
			return Mono.error(new RuntimeException("Invalid token signature"));
		}
	}
}

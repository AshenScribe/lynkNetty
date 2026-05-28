package org.example.objects;

import org.example.authenticator.TokenAuthStrategy;
import org.example.authenticator.requests.TokenAuthRequest;
import org.example.db.repository.UserRepository;
import reactor.core.publisher.Mono;

public record TokenLoginCommand(String token) implements LoginCommand {
	@Override
	public Mono<String> executeAuthentication(UserRepository userRepository) {
		return new TokenAuthStrategy().authenticate(new TokenAuthRequest(token), userRepository);
	}
}

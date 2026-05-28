package org.example.objects;

import org.example.authenticator.BasicAuthStrategy;
import org.example.authenticator.requests.BasicAuthRequest;
import org.example.db.repository.UserRepository;
import reactor.core.publisher.Mono;

public record BasicLoginCommand(String username, String password) implements LoginCommand {
	@Override
	public Mono<String> executeAuthentication(UserRepository userRepository) {
		BasicAuthRequest request = new BasicAuthRequest(username, password);
		return new BasicAuthStrategy().authenticate(request, userRepository);
	}
}

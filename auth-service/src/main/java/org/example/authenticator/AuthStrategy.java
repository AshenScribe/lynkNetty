package org.example.authenticator;

import org.example.authenticator.requests.AuthCredentials;
import org.example.db.repository.UserRepository;
import reactor.core.publisher.Mono;

public interface AuthStrategy {
	Mono<String> authenticate(AuthCredentials credentials, UserRepository userRepository);
}

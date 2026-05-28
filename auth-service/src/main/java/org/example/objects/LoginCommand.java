package org.example.objects;

import org.example.db.repository.UserRepository;
import reactor.core.publisher.Mono;

public sealed interface LoginCommand permits BasicLoginCommand, TokenLoginCommand {
	Mono<String> executeAuthentication(UserRepository userRepository);
}
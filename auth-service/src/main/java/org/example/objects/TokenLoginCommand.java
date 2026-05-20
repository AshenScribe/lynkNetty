package org.example.objects;

import org.example.authenticator.TokenAuthStrategy;
import org.example.authenticator.requests.TokenAuthRequest;

public record TokenLoginCommand(String token) implements LoginCommand {
	@Override
	public String executeAuthentication() {
		return new TokenAuthStrategy().authenticate(new TokenAuthRequest(token));
	}
}

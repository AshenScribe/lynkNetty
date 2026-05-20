package org.example.objects;

import org.example.authenticator.BasicAuthStrategy;
import org.example.authenticator.requests.BasicAuthRequest;

public record BasicLoginCommand(String username, String password) implements LoginCommand {
	@Override
	public String executeAuthentication() {
		BasicAuthRequest request = new BasicAuthRequest(username, password);
		return new BasicAuthStrategy().authenticate(request);
	}
}

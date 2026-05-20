package org.example.authenticator;

import org.example.authenticator.requests.AuthCredentials;

public class TokenAuthStrategy implements AuthStrategy {
	@Override
	public String authenticate(AuthCredentials credentials) {
		return "";
	}
}

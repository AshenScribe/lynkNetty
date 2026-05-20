package org.example.authenticator;

import org.example.authenticator.requests.AuthCredentials;

public class BasicAuthStrategy implements AuthStrategy {
	@Override
	public String authenticate(AuthCredentials credentials) {
		return "";
	}
}

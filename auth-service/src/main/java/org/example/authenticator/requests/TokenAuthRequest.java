package org.example.authenticator.requests;

public class TokenAuthRequest implements AuthCredentials {
	private String token;

	public String getToken() {
		return token;
	}

	public TokenAuthRequest(String token) {
		this.token = token;
	}
}

package org.example.authenticator.requests;

public class TokenAuthRequest implements AuthCredentials {
	String token;
	public TokenAuthRequest(String token) {
		this.token = token;
	}
}

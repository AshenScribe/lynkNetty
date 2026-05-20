package org.example.authenticator.requests;

public class BasicAuthRequest implements AuthCredentials {
	String username;
	String password;
	public BasicAuthRequest(String username, String password){
		this.username = username;
		this.password = password;
	}
}

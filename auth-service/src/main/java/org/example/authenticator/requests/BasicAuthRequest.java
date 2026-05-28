package org.example.authenticator.requests;

public class BasicAuthRequest implements AuthCredentials {
	String username;
	String password;

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public BasicAuthRequest(String username, String password){
		this.username = username;
		this.password = password;
	}
}

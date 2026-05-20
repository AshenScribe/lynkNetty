package org.example.authenticator;

import org.example.authenticator.requests.AuthCredentials;

public interface AuthStrategy {
	String authenticate(AuthCredentials credentials);
}

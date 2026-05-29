package org.example.security.keyProvider;

public interface KeyProvider {
	String getPublicKey() throws Exception;
	String getPrivateKey() throws Exception;
}

package org.example.authenticator.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthCredentialsTest {

    @Test
    void testBasicAuthRequestCreation() {
        BasicAuthRequest request = new BasicAuthRequest("alice", "secret");

        assertEquals("alice", request.username);
        assertEquals("secret", request.password);
    }

    @Test
    void testTokenAuthRequestCreation() {
        TokenAuthRequest request = new TokenAuthRequest("token-xyz");

        assertEquals("token-xyz", request.token);
    }

    @Test
    void testBasicAuthRequestWithNullValues() {
        BasicAuthRequest request = new BasicAuthRequest(null, null);

        assertNull(request.username);
        assertNull(request.password);
    }

    @Test
    void testTokenAuthRequestWithNull() {
        TokenAuthRequest request = new TokenAuthRequest(null);

        assertNull(request.token);
    }

    @Test
    void testBasicAuthRequestWithEmptyStrings() {
        BasicAuthRequest request = new BasicAuthRequest("", "");

        assertEquals("", request.username);
        assertEquals("", request.password);
    }

    @Test
    void testTokenAuthRequestWithEmptyString() {
        TokenAuthRequest request = new TokenAuthRequest("");

        assertEquals("", request.token);
    }

    @Test
    void testBasicAuthRequestWithSpecialCharacters() {
        BasicAuthRequest request = new BasicAuthRequest("user!@#", "pass$%^");

        assertEquals("user!@#", request.username);
        assertEquals("pass$%^", request.password);
    }
}
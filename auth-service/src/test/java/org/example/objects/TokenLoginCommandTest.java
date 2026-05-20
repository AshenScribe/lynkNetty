package org.example.objects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenLoginCommandTest {

    @Test
    void testTokenLoginCommandCreation() {
        TokenLoginCommand cmd = new TokenLoginCommand("token-abc-123");

        assertEquals("token-abc-123", cmd.token());
    }

    @Test
    void testExecuteAuthentication() {
        TokenLoginCommand cmd = new TokenLoginCommand("token123");
        String result = cmd.executeAuthentication();

        assertNotNull(result);
        assertEquals("", result);
    }

    @Test
    void testTokenLoginCommandWithEmptyToken() {
        TokenLoginCommand cmd = new TokenLoginCommand("");

        assertEquals("", cmd.token());
        assertNotNull(cmd.executeAuthentication());
    }

    @Test
    void testTokenLoginCommandWithNull() {
        TokenLoginCommand cmd = new TokenLoginCommand(null);

        assertNull(cmd.token());
    }

    @Test
    void testTokenLoginCommandEquality() {
        TokenLoginCommand cmd1 = new TokenLoginCommand("token123");
        TokenLoginCommand cmd2 = new TokenLoginCommand("token123");
        TokenLoginCommand cmd3 = new TokenLoginCommand("token456");

        assertEquals(cmd1, cmd2);
        assertNotEquals(cmd1, cmd3);
    }

    @Test
    void testTokenLoginCommandHashCode() {
        TokenLoginCommand cmd1 = new TokenLoginCommand("token123");
        TokenLoginCommand cmd2 = new TokenLoginCommand("token123");

        assertEquals(cmd1.hashCode(), cmd2.hashCode());
    }

    @Test
    void testTokenLoginCommandToString() {
        TokenLoginCommand cmd = new TokenLoginCommand("token123");
        String str = cmd.toString();

        assertTrue(str.contains("token123"));
    }
}
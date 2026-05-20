package org.example.objects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BasicLoginCommandTest {

	@Test
	void testBasicLoginCommandCreation() {
		BasicLoginCommand cmd = new BasicLoginCommand("alice", "secret123");

		assertEquals("alice", cmd.username());
		assertEquals("secret123", cmd.password());
	}

	@Test
	void testExecuteAuthentication() {
		BasicLoginCommand cmd = new BasicLoginCommand("user", "pass");
		String result = cmd.executeAuthentication();

		assertNotNull(result);
		assertEquals("", result);
	}

	@Test
	void testBasicLoginCommandWithEmptyCredentials() {
		BasicLoginCommand cmd = new BasicLoginCommand("", "");

		assertEquals("", cmd.username());
		assertEquals("", cmd.password());
		assertNotNull(cmd.executeAuthentication());
	}

	@Test
	void testBasicLoginCommandWithNull() {
		BasicLoginCommand cmd = new BasicLoginCommand(null, null);

		assertNull(cmd.username());
		assertNull(cmd.password());
	}

	@Test
	void testBasicLoginCommandEquality() {
		BasicLoginCommand cmd1 = new BasicLoginCommand("user", "pass");
		BasicLoginCommand cmd2 = new BasicLoginCommand("user", "pass");
		BasicLoginCommand cmd3 = new BasicLoginCommand("user", "different");

		assertEquals(cmd1, cmd2);
		assertNotEquals(cmd1, cmd3);
	}

	@Test
	void testBasicLoginCommandHashCode() {
		BasicLoginCommand cmd1 = new BasicLoginCommand("user", "pass");
		BasicLoginCommand cmd2 = new BasicLoginCommand("user", "pass");

		assertEquals(cmd1.hashCode(), cmd2.hashCode());
	}

	@Test
	void testBasicLoginCommandToString() {
		BasicLoginCommand cmd = new BasicLoginCommand("user", "pass");
		String str = cmd.toString();

		assertTrue(str.contains("user"));
		assertTrue(str.contains("pass"));
	}
}
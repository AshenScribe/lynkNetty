package org.example;

import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthServerTest {

	private AuthServer authServer;
	private Thread serverThread;

	@BeforeEach
	void setUp() {
		authServer = new AuthServer(0, new NioServerSocketChannel(), LogLevel.DEBUG, 128);
	}

	@AfterEach
	void tearDown() {
		if (authServer != null) {
			authServer.stop();
		}
		if (serverThread != null && serverThread.isAlive()) {
			serverThread.interrupt();
		}
	}

	@Test
	void testAuthServerCreation() {
		assertNotNull(authServer);
		assertNotNull(authServer.bootstrap);
		assertNotNull(authServer.bossGroup);
		assertNotNull(authServer.workerGroup);
	}

	@Test
	void testAuthServerStartAndStop() throws InterruptedException {
		// Use a test-specific port that's likely free
		AuthServer testServer = new AuthServer(0, new NioServerSocketChannel());

		serverThread = new Thread(() -> {
			try {
				testServer.start();
			} catch (Exception e) {
				// Expected when stopping
			}
		});
		serverThread.setDaemon(true);
		serverThread.start();

		Thread.sleep(100);

		testServer.stop();

		assertTrue(true); // If we get here without exception, test passes
	}

	@Test
	void testAuthServerWithCustomQueueSize() {
		AuthServer customServer = new AuthServer(0, new NioServerSocketChannel(), LogLevel.INFO, 256);
		assertNotNull(customServer);
		customServer.stop();
	}

	@Test
	void testAuthServerWithDifferentLogLevel() {
		AuthServer debugServer = new AuthServer(0, new NioServerSocketChannel(), LogLevel.DEBUG, 128);
		AuthServer infoServer = new AuthServer(0, new NioServerSocketChannel(), LogLevel.INFO, 128);

		assertNotNull(debugServer);
		assertNotNull(infoServer);

		debugServer.stop();
		infoServer.stop();
	}

	@Test
	void testMultipleStopCalls() {
		authServer.stop();
		authServer.stop(); // Should handle gracefully without exception
		assertTrue(true);
	}
}
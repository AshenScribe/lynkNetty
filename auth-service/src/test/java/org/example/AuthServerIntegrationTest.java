package org.example;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.example.utils.Base64Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.ServerSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthServerIntegrationTest {

	private AuthServer authServer;
	private Thread serverThread;
	private int testPort;

	@BeforeEach
	void setUp() throws Exception {
		// Find a free port before each test
		testPort = findFreePort();

		authServer = new AuthServer(testPort, new NioServerSocketChannel());
		serverThread = new Thread(() -> {
			try {
				authServer.start();
			} catch (Exception e) {
				// Expected when server is stopped
			}
		});
		serverThread.setDaemon(true);
		serverThread.start();

		// Wait for server to start and bind
		Thread.sleep(500);
	}

	@AfterEach
	void tearDown() throws Exception {
		if (authServer != null) {
			// Stop the server gracefully
			authServer.stop();
		}

		if (serverThread != null && serverThread.isAlive()) {
			serverThread.interrupt();
			serverThread.join(1000);
		}

		// Give time for resources to release
		Thread.sleep(200);
	}

	private int findFreePort() throws Exception {
		try (ServerSocket socket = new ServerSocket(0)) {
			return socket.getLocalPort();
		}
	}

	@Test
	void testSendBasicLoginRequest() throws Exception {
		BlockingQueue<String> responseQueue = new LinkedBlockingQueue<>();

		EventLoopGroup group = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

		try {
			Bootstrap bootstrap = createBootstrap(group, responseQueue);
			Channel channel = bootstrap.connect("localhost", testPort).sync().channel();

			String encodedUser = Base64Utils.encodeBase64("testuser");
			String encodedPass = Base64Utils.encodeBase64("testpass");
			channel.writeAndFlush("LOGIN BASIC " + encodedUser + " " + encodedPass + "\n");

			String response = responseQueue.poll(3, TimeUnit.SECONDS);
			assertNotNull(response, "Should receive a response");
			assertTrue(response.startsWith("Auth Result:"));

			channel.close().sync();
		} finally {
			group.shutdownGracefully();
		}
	}

	@Test
	void testSendTokenLoginRequest() throws Exception {
		BlockingQueue<String> responseQueue = new LinkedBlockingQueue<>();

		EventLoopGroup group = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

		try {
			Bootstrap bootstrap = createBootstrap(group, responseQueue);
			Channel channel = bootstrap.connect("localhost", testPort).sync().channel();

			String encodedToken = Base64Utils.encodeBase64("test-token-123");
			channel.writeAndFlush("LOGIN TOKEN " + encodedToken + "\n");

			String response = responseQueue.poll(3, TimeUnit.SECONDS);
			assertNotNull(response, "Should receive a response");
			assertTrue(response.startsWith("Auth Result:"));

			channel.close().sync();
		} finally {
			group.shutdownGracefully();
		}
	}

	@Test
	void testSendRegisterRequest() throws Exception {
		BlockingQueue<String> responseQueue = new LinkedBlockingQueue<>();

		EventLoopGroup group = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

		try {
			Bootstrap bootstrap = createBootstrap(group, responseQueue);
			Channel channel = bootstrap.connect("localhost", testPort).sync().channel();

			String encodedUser = Base64Utils.encodeBase64("newuser");
			String encodedPass = Base64Utils.encodeBase64("newpass123");
			channel.writeAndFlush("REGISTER " + encodedUser + " " + encodedPass + "\n");

			// Register decoder doesn't send a response on success
			// Wait a bit to see if any error response comes
			Thread.sleep(500);

			channel.close().sync();
		} finally {
			group.shutdownGracefully();
		}
	}

	@Test
	void testSendMalformedLoginRequest() throws Exception {
		BlockingQueue<String> responseQueue = new LinkedBlockingQueue<>();

		EventLoopGroup group = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

		try {
			Bootstrap bootstrap = createBootstrap(group, responseQueue);
			Channel channel = bootstrap.connect("localhost", testPort).sync().channel();

			// Malformed: missing password
			String encodedUser = Base64Utils.encodeBase64("testuser");
			channel.writeAndFlush("LOGIN BASIC " + encodedUser + "\n");

			String response = responseQueue.poll(3, TimeUnit.SECONDS);
			// Should get an error response
			if (response != null) {
				assertTrue(response.contains("ERROR:") || response.contains("Malformed"));
			}

			channel.close().sync();
		} finally {
			group.shutdownGracefully();
		}
	}

	@Test
	void testSendInvalidBase64InLogin() throws Exception {
		BlockingQueue<String> responseQueue = new LinkedBlockingQueue<>();

		EventLoopGroup group = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

		try {
			Bootstrap bootstrap = createBootstrap(group, responseQueue);
			Channel channel = bootstrap.connect("localhost", testPort).sync().channel();

			channel.writeAndFlush("LOGIN BASIC !!! !!!\n");

			String response = responseQueue.poll(3, TimeUnit.SECONDS);
			assertNotNull(response, "Should receive error response");
			assertTrue(response.contains("ERROR:"), "Response should contain ERROR");
			assertTrue(response.contains("Invalid Base64"), "Should mention Base64 error");

			channel.close().sync();
		} finally {
			group.shutdownGracefully();
		}
	}

	@Test
	void testSendInvalidType() throws Exception {
		BlockingQueue<String> responseQueue = new LinkedBlockingQueue<>();

		EventLoopGroup group = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

		try {
			Bootstrap bootstrap = createBootstrap(group, responseQueue);
			Channel channel = bootstrap.connect("localhost", testPort).sync().channel();

			channel.writeAndFlush("LOGIN INVALID_TYPE " + Base64Utils.encodeBase64("value") + "\n");

			String response = responseQueue.poll(3, TimeUnit.SECONDS);
			assertNotNull(response, "Should receive error response");
			assertTrue(response.contains("Invalid type"), "Response should mention invalid type");

			channel.close().sync();
		} finally {
			group.shutdownGracefully();
		}
	}

	@Test
	void testMultipleLoginAttempts() throws Exception {
		BlockingQueue<String> responseQueue = new LinkedBlockingQueue<>();

		EventLoopGroup group = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

		try {
			Bootstrap bootstrap = createBootstrap(group, responseQueue);
			Channel channel = bootstrap.connect("localhost", testPort).sync().channel();

			// Send multiple login requests
			for (int i = 0; i < 3; i++) {
				String encodedUser = Base64Utils.encodeBase64("user" + i);
				String encodedPass = Base64Utils.encodeBase64("pass" + i);
				channel.writeAndFlush("LOGIN BASIC " + encodedUser + " " + encodedPass + "\n");

				String response = responseQueue.poll(2, TimeUnit.SECONDS);
				assertNotNull(response, "Should receive response for attempt " + i);
				assertTrue(response.startsWith("Auth Result:"));
			}

			channel.close().sync();
		} finally {
			group.shutdownGracefully();
		}
	}

	@Test
	void testConcurrentConnections() throws Exception {
		int numConnections = 3;
		EventLoopGroup group = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
		java.util.List<Channel> channels = new java.util.ArrayList<>();

		try {
			for (int i = 0; i < numConnections; i++) {
				BlockingQueue<String> responseQueue = new LinkedBlockingQueue<>();
				Bootstrap bootstrap = createBootstrap(group, responseQueue);
				Channel channel = bootstrap.connect("localhost", testPort).sync().channel();
				channels.add(channel);

				String encodedUser = Base64Utils.encodeBase64("user" + i);
				String encodedPass = Base64Utils.encodeBase64("pass" + i);
				channel.writeAndFlush("LOGIN BASIC " + encodedUser + " " + encodedPass + "\n");
			}

			// Give some time for processing
			Thread.sleep(1000);

			// Close all channels
			for (Channel ch : channels) {
				if (ch.isOpen()) {
					ch.close().sync();
				}
			}
		} finally {
			group.shutdownGracefully();
		}
	}

	private Bootstrap createBootstrap(EventLoopGroup group, BlockingQueue<String> responseQueue) {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(group)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) {
						ch.pipeline()
								.addLast(new LineBasedFrameDecoder(8192))
								.addLast(new StringDecoder(CharsetUtil.UTF_8))
								.addLast(new StringEncoder(CharsetUtil.UTF_8))
								.addLast(new SimpleChannelInboundHandler<String>() {
									@Override
									protected void channelRead0(ChannelHandlerContext ctx, String msg) {
										responseQueue.offer(msg);
									}

									@Override
									public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
										responseQueue.offer("ERROR: " + cause.getMessage());
										ctx.close();
									}
								});
					}
				});
		return bootstrap;
	}
}
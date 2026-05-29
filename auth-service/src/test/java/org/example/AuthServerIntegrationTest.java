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
import org.example.config.TestDatabaseConfig;
import org.example.db.DatabaseClient;
import org.example.db.entity.User;
import org.example.db.repository.BaseDatabaseTest;
import org.example.db.repository.UserRepository;
import org.example.security.JwtProvider;
import org.example.security.JwtTestUtils;
import org.example.utils.Base64Utils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.net.ServerSocket;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthServerIntegrationTest extends BaseDatabaseTest {

	private AuthServer authServer;
	private Thread serverThread;
	private int testPort;
	private UserRepository userRepository;

	@BeforeEach
	void setUp() throws Exception {
		testPort = findFreePort();
		JwtTestUtils.ensureInitialized();
		DatabaseClient databaseClient = new DatabaseClient(new TestDatabaseConfig(postgres, SSL_DIR));
		databaseClient.clearSchema();
		databaseClient.initializeSchema();
		userRepository = new UserRepository(databaseClient.getConnectionFactory());

		String hashedPw = BCrypt.hashpw("testpass", BCrypt.gensalt());
		User testUser = new User("testuser", hashedPw);
		userRepository.saveUser(testUser).block();

		authServer = new AuthServer(testPort, new NioServerSocketChannel(), databaseClient);

		serverThread = new Thread(() -> {
			try {
				authServer.start();
			} catch (Exception e) {
			}
		});
		serverThread.setDaemon(true);
		serverThread.start();

		Thread.sleep(500);
	}

	@AfterEach
	void tearDown() throws Exception {
		JwtTestUtils.cleanup();
		if (authServer != null) {
			authServer.stop();
		}

		if (serverThread != null && serverThread.isAlive()) {
			serverThread.interrupt();
			serverThread.join(1000);
		}

		Thread.sleep(200);
	}

	@AfterAll
	static void tearDownAll() {
		stop();
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

			String encodedToken = Base64Utils.encodeBase64(JwtProvider.getInstance().createToken("testuser"));
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

			String encodedUser = Base64Utils.encodeBase64("testuser");
			channel.writeAndFlush("LOGIN BASIC " + encodedUser + "\n");

			String response = responseQueue.poll(3, TimeUnit.SECONDS);
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

			for (int i = 0; i < 3; i++) {
				String plainPassword = "password" + i;

				User user = new User("username" + i, BCrypt.hashpw(plainPassword, BCrypt.gensalt()));
				userRepository.saveUser(user).block();

				channel.writeAndFlush("LOGIN BASIC " + Base64Utils.encodeBase64(user.username()) + " " + Base64Utils.encodeBase64(plainPassword) + "\n");

				String response = responseQueue.poll(2, TimeUnit.SECONDS);
				assertNotNull(response, "Should receive response for attempt " + i);
				assertTrue(response.startsWith("Auth Result:"),
						"Response should start with 'Auth Result:' for attempt " + i + " but was: " + response);
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

			Thread.sleep(1000);

			for (Iterator<Channel> it = channels.iterator(); it.hasNext(); ) {
				Channel ch = it.next();
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
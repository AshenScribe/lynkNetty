package org.example;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;
import org.example.db.DatabaseClient;
import org.example.db.repository.UserRepository;
import org.example.encoder.LoginDecoder;
import org.example.encoder.RegisterDecoder;
import org.example.handler.LoginServerHandler;
import org.example.handler.RegisterServerHandler;

public class AuthServer {

	private int port;
	ServerBootstrap bootstrap;
	EventLoopGroup bossGroup;
	EventLoopGroup workerGroup;
	DatabaseClient databaseClient;

	public AuthServer(int port, ServerChannel channel, DatabaseClient databaseClient) {
		this.databaseClient = databaseClient;
		initServer(port, channel, LogLevel.DEBUG, 128);
	}

	public AuthServer(int port, ServerChannel channel, LogLevel level, int acceptedQueueSize, DatabaseClient databaseClient) {
		this.databaseClient = databaseClient;
		initServer(port, channel, level, acceptedQueueSize);
	}


	private void initServer(int port, ServerChannel channel, LogLevel level, int acceptedQueueSize) {
		this.port = port;
		bootstrap = new ServerBootstrap();
		bossGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
		workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
		bootstrap
				.group(bossGroup, workerGroup)
				.channel(channel.getClass())
				.handler(new LoggingHandler(level))
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) {
						ch.pipeline()
								.addLast(new LineBasedFrameDecoder(1024),
										new StringDecoder(CharsetUtil.UTF_8),
										new StringEncoder(CharsetUtil.UTF_8),
										new LoginDecoder(),
										new RegisterDecoder(),
										new LoginServerHandler(new UserRepository(databaseClient.getConnectionFactory())),
										new RegisterServerHandler(new UserRepository(databaseClient.getConnectionFactory())));
					}
				})
				.option(ChannelOption.SO_BACKLOG, acceptedQueueSize)
				.childOption(ChannelOption.SO_KEEPALIVE, true);
	}

	public void start() {
		try {
			ChannelFuture channelFuture = bootstrap.bind(port).sync();
			channelFuture.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	public void stop() {
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}
}

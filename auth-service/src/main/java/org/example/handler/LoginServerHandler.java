package org.example.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.example.db.repository.UserRepository;
import org.example.objects.LoginCommand;

public class LoginServerHandler extends SimpleChannelInboundHandler<LoginCommand> {

	private final UserRepository userRepository;

	public LoginServerHandler(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, LoginCommand msg) {
		msg.executeAuthentication(userRepository).subscribe(
				token -> ctx.writeAndFlush("Auth Result: " + token + "\n"),
				error -> {
					ctx.writeAndFlush("ERROR: " + error.getMessage() + "\n");
					ctx.close();
				}
		);
	}
}

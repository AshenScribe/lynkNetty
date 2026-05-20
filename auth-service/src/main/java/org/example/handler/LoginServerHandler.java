package org.example.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.example.objects.LoginCommand;

public class LoginServerHandler extends SimpleChannelInboundHandler<LoginCommand> {
	@Override
	public void channelRead0(ChannelHandlerContext ctx, LoginCommand msg) {
		String result = msg.executeAuthentication();
		ctx.writeAndFlush("Auth Result: " + result + "\n");
	}
}

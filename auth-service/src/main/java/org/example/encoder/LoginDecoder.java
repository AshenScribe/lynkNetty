package org.example.encoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.example.objects.BasicLoginCommand;
import org.example.objects.LoginCommand;
import org.example.objects.TokenLoginCommand;
import org.example.objects.Type;

import java.util.Arrays;
import java.util.List;

public class LoginDecoder extends MessageToMessageDecoder<String> {

	private static final String CMD_LOGIN = "LOGIN";

	@Override
	protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) {
		String[] parts = msg.trim().split("\\s+");

		if (!CMD_LOGIN.equals(parts[0])) {
			ctx.fireChannelRead(msg);
			return;
		}

		LoginCommand loginCommand = null;
		try {
			Type type = Type.valueOf(parts[1]);
			switch (type) {
				case BASIC -> {
					if (parts.length != 4) {
						ctx.fireChannelRead("Malformed Login Request:" + msg);
						// close connection if invalid request format
						ctx.close();
						return;
					}
					loginCommand = new BasicLoginCommand(parts[2], parts[3]);
				}
				case TOKEN -> {
					if (parts.length != 3) {
						ctx.fireChannelRead("Malformed Login Request:" + msg);
						// close connection if invalid request format
						ctx.close();
						return;
					}
					loginCommand = new TokenLoginCommand(parts[2]);
				}
				default -> {
					ctx.fireChannelRead("Malformed Login Request:" + msg);
					// close connection if invalid request format
					ctx.close();
					return;
				}
			}
		} catch (IllegalArgumentException e) {
			ctx.writeAndFlush("Invalid type: " + parts[1] + ",Expected type: " + Arrays.toString(Type.values()));
			return;
		}
			out.add(loginCommand);
	}

	private void sendError(ChannelHandlerContext ctx, String message) {
		ctx.writeAndFlush("ERROR: " + message + "\n");
		ctx.close();
	}
}

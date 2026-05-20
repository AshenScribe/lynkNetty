package org.example.encoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.example.objects.BasicLoginCommand;
import org.example.objects.LoginCommand;
import org.example.objects.TokenLoginCommand;
import org.example.objects.Type;
import org.example.utils.Base64Utils;

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

		try {
			Type type = Type.valueOf(parts[1]);
			LoginCommand loginCommand;

			switch (type) {
				case BASIC -> {
					if (parts.length != 4) {
						sendErrorAndClose(ctx, "Malformed Login Request");
						return;
					}
					loginCommand = new BasicLoginCommand(Base64Utils.decodeBase64(parts[2]), Base64Utils.decodeBase64(parts[3]));
				}
				case TOKEN -> {
					if (parts.length != 3) {
						sendErrorAndClose(ctx, "Malformed Login Request");
						return;
					}
					loginCommand = new TokenLoginCommand(Base64Utils.decodeBase64(parts[2]));
				}
				default -> {
					sendErrorAndClose(ctx, "Malformed Login Request");
					return;
				}
			}

			out.add(loginCommand);

		} catch (IllegalArgumentException e) {
			sendErrorAndClose(ctx, "Invalid type: " + parts[1]);
		} catch (Base64Utils.Base64Exception e) {
			sendErrorAndClose(ctx, e.getMessage());
		}
	}

	private void sendErrorAndClose(ChannelHandlerContext ctx, String msg) {
		ctx.writeAndFlush("ERROR: " + msg + "\n");
		ctx.close();
	}
}

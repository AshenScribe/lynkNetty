package org.example.encoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.example.objects.RegisterObject;
import org.example.utils.Base64Utils;

import java.util.List;

public class RegisterDecoder extends MessageToMessageDecoder<String> {

    @Override
    protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) {
        String trimmed = msg.trim();
        String[] parts = trimmed.split("\\s+");

        if (!"REGISTER".equalsIgnoreCase(parts[0]) || parts.length != 3) {
            sendError(ctx, "Malformed Register Request. Expected: REGISTER <base64_user> <base64_pass>");
            return;
        }

        try {
            String user = Base64Utils.decodeBase64(parts[1]);
            String pass = Base64Utils.decodeBase64(parts[2]);
            out.add(new RegisterObject(user, pass));
        } catch (Base64Utils.Base64Exception e) {
            sendError(ctx, e.getMessage());
        }
    }

    private void sendError(ChannelHandlerContext ctx, String message) {
        ctx.writeAndFlush("ERROR: " + message + "\n");
        ctx.close();
    }
}
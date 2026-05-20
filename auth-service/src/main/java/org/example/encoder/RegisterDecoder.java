package org.example.encoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.example.objects.RegisterObject;

import java.util.List;

public class RegisterDecoder extends MessageToMessageDecoder<String> {

    @Override
    protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) {
        String trimmed = msg.trim();
        // REGISTER username password
        String[] parts = trimmed.split("\\s", 3);

        if (!parts[0].equals("REGISTER") || parts.length != 3) {
            ctx.writeAndFlush("Malformed Register Request\nExpected: REGISTER username password\n");
            ctx.close();
            return;
        }

        out.add(new RegisterObject(parts[1], parts[2]));
    }
}
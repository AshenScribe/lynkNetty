package org.example.encoder;

import io.netty.channel.ChannelHandlerContext;
import org.example.objects.RegisterObject;
import org.example.utils.Base64Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RegisterDecoderTest {

	private RegisterDecoder decoder;

	@BeforeEach
	void setUp() {
		decoder = new RegisterDecoder();
	}

	@Test
	void testDecodeValidRegister() {
		String user = Base64Utils.encodeBase64("testuser");
		String pass = Base64Utils.encodeBase64("testpass123");
		String message = "REGISTER " + user + " " + pass;

		ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);

		List<Object> out = new ArrayList<>();
		decoder.decode(ctx, message, out);

		assertEquals(1, out.size());
		RegisterObject obj = (RegisterObject) out.get(0);
		assertEquals("testuser", obj.username());
		assertEquals("testpass123", obj.password());
	}

	@Test
	void testDecodeMalformedRegisterMissingPassword() {
		String message = "REGISTER testuser";
		ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);

		List<Object> out = new ArrayList<>();
		decoder.decode(ctx, message, out);

		assertEquals(0, out.size());
		verify(ctx).writeAndFlush(contains("ERROR:"));
		verify(ctx).close();
	}

	@Test
	void testDecodeMalformedRegisterWrongCommand() {
		String message = "LOGIN testuser testpass";
		ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);

		List<Object> out = new ArrayList<>();
		decoder.decode(ctx, message, out);

		assertEquals(0, out.size());
		verify(ctx).writeAndFlush(contains("Malformed Register Request"));
		verify(ctx).close();
	}

	@Test
	void testDecodeRegisterWithExtraParameters() {
		String message = "REGISTER testuser testpass extraparam";
		ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);

		List<Object> out = new ArrayList<>();
		decoder.decode(ctx, message, out);

		assertEquals(0, out.size());
		verify(ctx).writeAndFlush(contains("Malformed Register Request"));
		verify(ctx).close();
	}
}
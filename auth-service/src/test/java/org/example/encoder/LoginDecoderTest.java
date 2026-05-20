package org.example.encoder;

import io.netty.channel.ChannelHandlerContext;
import org.example.objects.BasicLoginCommand;
import org.example.objects.TokenLoginCommand;
import org.example.utils.Base64Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginDecoderTest {

    private LoginDecoder decoder;

    @BeforeEach
    void setUp() {
        decoder = new LoginDecoder();
    }

    @Test
    void testDecodeBasicLogin() {
        String user = Base64Utils.encodeBase64("testuser");
        String pass = Base64Utils.encodeBase64("testpass");
        String message = "LOGIN BASIC " + user + " " + pass;

        List<Object> out = new ArrayList<>();
        decoder.decode(mock(ChannelHandlerContext.class), message, out);

        assertEquals(1, out.size());
        assertInstanceOf(BasicLoginCommand.class, out.get(0));
        BasicLoginCommand cmd = (BasicLoginCommand) out.get(0);
        assertEquals("testuser", cmd.username());
        assertEquals("testpass", cmd.password());
    }

    @Test
    void testDecodeTokenLogin() {
        String token = Base64Utils.encodeBase64("test-token-123");
        String message = "LOGIN TOKEN " + token;

        List<Object> out = new ArrayList<>();
        decoder.decode(mock(ChannelHandlerContext.class), message, out);

        assertEquals(1, out.size());
        assertInstanceOf(TokenLoginCommand.class, out.get(0));
        TokenLoginCommand cmd = (TokenLoginCommand) out.get(0);
        assertEquals("test-token-123", cmd.token());
    }

    @Test
    void testDecodeNonLoginCommand() {
        String message = "OTHER COMMAND";
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);

        List<Object> out = new ArrayList<>();
        decoder.decode(ctx, message, out);

        assertEquals(0, out.size());
        verify(ctx).fireChannelRead(message);
    }

    @Test
    void testDecodeMalformedBasicLogin() {
        // Still invalid length even if base64
        String message = "LOGIN BASIC " + Base64Utils.encodeBase64("onlyonepart");
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);

        List<Object> out = new ArrayList<>();
        decoder.decode(ctx, message, out);

        assertEquals(0, out.size());
        verify(ctx).close();
    }

    @Test
    void testDecodeInvalidBase64() {
        String message = "LOGIN BASIC !!! !!!";
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);

        List<Object> out = new ArrayList<>();
        decoder.decode(ctx, message, out);

        assertEquals(0, out.size());
        verify(ctx).writeAndFlush(contains("ERROR:"));
        verify(ctx).close();
    }

    @Test
    void testDecodeInvalidType() {
        String message = "LOGIN INVALID_TYPE somevalue";
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);

        List<Object> out = new ArrayList<>();
        decoder.decode(ctx, message, out);

        assertEquals(0, out.size());
        verify(ctx).writeAndFlush(contains("Invalid type"));
    }
}
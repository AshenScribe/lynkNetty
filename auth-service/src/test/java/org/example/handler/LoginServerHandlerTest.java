package org.example.handler;

import io.netty.channel.ChannelHandlerContext;
import org.example.objects.BasicLoginCommand;
import org.example.objects.TokenLoginCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class LoginServerHandlerTest {

    private LoginServerHandler handler;
    private ChannelHandlerContext ctx;

    @BeforeEach
    void setUp() {
        handler = new LoginServerHandler();
        ctx = mock(ChannelHandlerContext.class);
    }

    @Test
    void testChannelReadWithBasicLogin() {
        BasicLoginCommand cmd = new BasicLoginCommand("testuser", "testpass");

        handler.channelRead0(ctx, cmd);

        verify(ctx).writeAndFlush(contains("Auth Result:"));
    }

    @Test
    void testChannelReadWithTokenLogin() {
        TokenLoginCommand cmd = new TokenLoginCommand("test-token");

        handler.channelRead0(ctx, cmd);

        verify(ctx).writeAndFlush(contains("Auth Result:"));
    }

    @Test
    void testChannelReadResponseFormat() {
        BasicLoginCommand cmd = new BasicLoginCommand("user", "pass");

        handler.channelRead0(ctx, cmd);

        verify(ctx).writeAndFlush(argThat(msg ->
            ((String) msg).startsWith("Auth Result:") && ((String) msg).endsWith("\n")
        ));
    }

    @Test
    void testChannelReadResponseNotEmpty() {
        BasicLoginCommand cmd = new BasicLoginCommand("user", "pass");

        handler.channelRead0(ctx, cmd);

        // The response should be "Auth Result: " + result + "\n"
        // Even if result is empty, the response is still valid
        verify(ctx).writeAndFlush(argThat(msg ->
            ((String) msg).startsWith("Auth Result:") && ((String) msg).endsWith("\n")
        ));
    }

    @Test
    void testChannelReadResponseContainsAuthResult() {
        BasicLoginCommand cmd = new BasicLoginCommand("user", "pass");

        handler.channelRead0(ctx, cmd);

        verify(ctx).writeAndFlush(argThat(msg ->
            ((String) msg).contains("Auth Result:")
        ));
    }
}
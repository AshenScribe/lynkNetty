package org.example.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.example.db.entity.User;
import org.example.db.repository.UserRepository;
import org.example.objects.RegisterObject;
import org.mindrot.jbcrypt.BCrypt;

public class RegisterServerHandler extends SimpleChannelInboundHandler<RegisterObject> {

    private final UserRepository userRepository;

    public RegisterServerHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterObject msg) {
        String hashedPassword = BCrypt.hashpw(msg.password(), BCrypt.gensalt());
        User user = new User(msg.username(), hashedPassword);
        
        userRepository.saveUser(user)
                .subscribe(
                        done -> ctx.writeAndFlush("REGISTER_SUCCESS: User " + msg.username() + " created\n"),
                        error -> {
                            ctx.writeAndFlush("ERROR: Registration failed - " + error.getMessage() + "\n");
                            ctx.close();
                        }
                );
    }
}
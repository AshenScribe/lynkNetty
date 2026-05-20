package org.example;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.socket.nio.NioServerSocketChannel;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
	static void main() {
		AuthServer authServer = new AuthServer(8080, new NioServerSocketChannel());
		authServer.start();
	}
}

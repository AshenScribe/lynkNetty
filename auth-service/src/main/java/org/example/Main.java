package org.example;

import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.aeonbits.owner.ConfigFactory;
import org.example.config.DatabaseConfig;
import org.example.db.DatabaseClient;

public class Main {
	public static void main(String[] args) {
		DatabaseConfig config = ConfigFactory.create(DatabaseConfig.class);
		DatabaseClient databaseClient = new DatabaseClient(config);

		try {
			databaseClient.initializeSchema();
			System.out.println("Database initialized.");
		} catch (Exception e) {
			System.err.println("CRITICAL: Failed to initialize database: " + e.getMessage());
			System.exit(1);
		}

		AuthServer authServer = new AuthServer(8080, new NioServerSocketChannel(), databaseClient);
		authServer.start();
	}
}

package org.example.db;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.client.SSLMode;
import io.r2dbc.postgresql.client.SSLNegotiation;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import org.example.config.DatabaseConfig;
import reactor.core.publisher.Mono;

public class DatabaseClient {
	private static ConnectionFactory connectionFactory;
	private volatile static DatabaseClient databaseClient = null;

	public DatabaseClient(DatabaseConfig config) {
		connectionFactory = new PostgresqlConnectionFactory(PostgresqlConnectionConfiguration.builder()
				.host(config.host())
				.port(config.port())
				.database(config.database())
				.username(config.username())
				.password(config.password())
				.sslMode(SSLMode.VERIFY_FULL)
				.sslNegotiation(SSLNegotiation.POSTGRES)
				.sslRootCert(config.sslRootCert())
				.sslCert(config.sslCert())
				.sslKey(config.sslKey())
				.sslPassword(config.sslPassword())
				.build());
	}

	public static DatabaseClient getDatabaseClient(DatabaseConfig config) {
		if (databaseClient == null) {
			databaseClient = new DatabaseClient(config);
		}
		return databaseClient;
	}

	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	public boolean initializeSchema() {
		try {
			Mono.from(connectionFactory.create())
					.flatMapMany(conn -> conn.createStatement(
							"CREATE TABLE IF NOT EXISTS " + TableNameUtils.USER_TABLE_NAME + " (username VARCHAR(255) PRIMARY KEY, password VARCHAR(255) NOT NULL)"
					).execute())
					.flatMap(Result::getRowsUpdated)
					.collectList()
					.block();      // Blocking at startup only

			System.out.println("Database schema initialized successfully.");
			return true;
		} catch (Exception e) {
			System.err.println("Database initialization failed: " + e.getMessage());
			return false;
		}
	}

	public void clearSchema() {
		try {
			Mono.from(connectionFactory.create())
					.flatMapMany(conn -> conn.createStatement(
							"TRUNCATE TABLE " + TableNameUtils.USER_TABLE_NAME + " CASCADE"
					).execute())
					.flatMap(Result::getRowsUpdated)
					.collectList()
					.block();
		} catch (Exception e) {
			System.err.println("Database clear failed: " + e.getMessage());
		}
	}
}

package org.example.config;

import org.testcontainers.postgresql.PostgreSQLContainer;

public class TestDatabaseConfig implements DatabaseConfig {
    private final PostgreSQLContainer container;
	private final String SSL_DIR;
    public TestDatabaseConfig(PostgreSQLContainer container, String sslDir) {
        this.container = container;
		this.SSL_DIR = sslDir;
    }

    @Override public String host() { return container.getHost(); }
    @Override public int port() { return container.getMappedPort(5432); }
    @Override public String username() { return container.getUsername(); }
    @Override public String password() { return container.getPassword(); }
    @Override public String database() { return container.getDatabaseName(); }

    @Override public String sslRootCert() { return SSL_DIR + "rootCA.crt"; }
    @Override public String sslCert() { return SSL_DIR + "client.crt"; }
    @Override public String sslKey() { return SSL_DIR + "client.key"; }
    @Override public String sslPassword() { return null; }
}

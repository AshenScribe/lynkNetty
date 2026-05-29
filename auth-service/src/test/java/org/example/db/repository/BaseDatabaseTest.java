package org.example.db.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@Testcontainers
public abstract class BaseDatabaseTest {
	protected static final String SSL_DIR = "src/test/resources/ssl/";
	@Container
	protected static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:15-alpine");
	private static final Logger log = LoggerFactory.getLogger(BaseDatabaseTest.class);

	static {
		setupCertificates();
		postgres.start();
	}

	private synchronized static void setupCertificates() {
		try {
			File scriptFile = new File(SSL_DIR + "generate_ssl.sh");
			if (!scriptFile.setExecutable(true)) log.atError().log("Cannot make generate_ssl.sh executable");
			try {
				new ProcessBuilder("bash", scriptFile.getPath()).inheritIO().start().waitFor();
			} catch (Exception e) {
				throw new RuntimeException("Certificate generation failed.", e);
			}
			if (!new File(SSL_DIR + "entrypoint.sh").setExecutable(true))
				log.atError().log("Cannot make entrypoint.sh executable");
			postgres.withUsername("postgres")
					.withCopyFileToContainer(MountableFile.forHostPath(SSL_DIR + "setup-ssl.sh"), "/docker-entrypoint-initdb.d/ssl-config.sh")
					.withCopyFileToContainer(MountableFile.forHostPath(SSL_DIR + "entrypoint.sh"), "/usr/local/bin/entrypoint.sh")
					.withCreateContainerCmdModifier(cmd -> cmd.withEntrypoint("/usr/local/bin/entrypoint.sh"))
					.withEnv("SERVER_CRT", Files.readString(Paths.get(SSL_DIR + "server.crt")))
					.withEnv("SERVER_KEY", Files.readString(Paths.get(SSL_DIR + "server.key")))
					.withEnv("ROOT_CA_CRT", Files.readString(Paths.get(SSL_DIR + "rootCA.crt")));
		} catch (Exception e) {
			throw new RuntimeException("Failed to setup SSL for Testcontainers", e);
		}
	}

	protected static void stop() {
		for (File f : new File(SSL_DIR).listFiles()) {
			if (f.isFile() && !f.getName().endsWith(".sh")) f.delete();
		}
	}
}
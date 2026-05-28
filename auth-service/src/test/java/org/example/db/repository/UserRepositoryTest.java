package org.example.db.repository;

import org.example.config.DatabaseConfig;
import org.example.config.TestDatabaseConfig;
import org.example.db.DatabaseClient;
import org.example.db.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class UserRepositoryTest extends BaseDatabaseTest {

	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		DatabaseConfig config = new TestDatabaseConfig(postgres, SSL_DIR);
		DatabaseClient databaseClient = new DatabaseClient(config);
		databaseClient.clearSchema();
		databaseClient.initializeSchema();
		userRepository = new UserRepository(databaseClient.getConnectionFactory());
	}

	@AfterEach
	void tearDown() {

		stop();
	}

	@Test
	void testSaveAndFindUser() {
		User user = new User("alice", "hashed_password");
		StepVerifier.create(userRepository.saveUser(user)).verifyComplete();
		StepVerifier.create(userRepository.findUserByUsername("alice"))
				.expectNextMatches(u -> u.username().equals("alice"))
				.verifyComplete();
	}
}
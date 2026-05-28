package org.example.db.repository;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import org.example.db.DatabaseClient;
import org.example.db.TableNameUtils;
import org.example.db.entity.User;
import reactor.core.publisher.Mono;

public class UserRepository {
	private final ConnectionFactory factory;

	public UserRepository(ConnectionFactory factory) {
		this.factory = factory;
	}

	public Mono<Void> saveUser(User user) {
    return Mono.from(factory.create())
            .flatMapMany(conn -> conn.createStatement(
                            "INSERT INTO " + TableNameUtils.USER_TABLE_NAME + " (username, password) VALUES ($1, $2)")
                    .bind("$1", user.username())
                    .bind("$2", user.password())
                    .execute())
            .flatMap(Result::getRowsUpdated)
            .then();
}

	public Mono<User> findUserByUsername(String username) {
		return Mono.from(factory.create())
				.flatMapMany(connection -> connection
						.createStatement("SELECT username, password FROM " + TableNameUtils.USER_TABLE_NAME + " WHERE username = $1")
						.bind("$1", username)
						.execute())
				.flatMap(result -> result.map((row, metadata) ->
						new User(
								row.get("username", String.class),
								row.get("password", String.class)
						)
				))
				.next();
	}
}
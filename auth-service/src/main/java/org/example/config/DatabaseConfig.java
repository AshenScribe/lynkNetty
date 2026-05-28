package org.example.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources({"classpath:db.properties", "system:env"})
public interface DatabaseConfig extends Config {

	@Key("db.host")
	@DefaultValue("localhost")
	String host();

	@Key("db.port")
	@DefaultValue("5432")
	int port();

	@Key("db.username")
	String username();

	@Key("db.password")
	String password();

	@Key("db.database")
	String database();

	@Key("ssl.root.cert")
	String sslRootCert();

	@Key("ssl.cert")
	String sslCert();

	@Key("ssl.key")
	String sslKey();

	@Key("ssl.password")
	String sslPassword();
}
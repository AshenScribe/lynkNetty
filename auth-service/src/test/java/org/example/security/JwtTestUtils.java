package org.example.security;

import org.example.security.keyProvider.FileKeyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class JwtTestUtils {

    private static boolean initialized = false;
    private static final String SCRIPT_DIR = "src/test/resources/jwt/";
    private static final Logger log = LoggerFactory.getLogger(JwtTestUtils.class);
    private static File publicKeyFile;
    private static File privateKeyFile;

    public static synchronized void ensureInitialized() {
        if (initialized) {
            return;
        }
        try {
		File scriptDir = new File(SCRIPT_DIR);
		if (!scriptDir.exists()) {
			scriptDir.mkdirs();
		}

		publicKeyFile = new File(SCRIPT_DIR + "public.pem");
		privateKeyFile = new File(SCRIPT_DIR + "private.pem");

		if (!generateKeysWithScript()) {
			log.error("Failed to generate keys with script");
			System.exit(-1);
		}

		JwtProvider.initialize(new FileKeyProvider(
				publicKeyFile.getAbsolutePath(),
				privateKeyFile.getAbsolutePath()
		));

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize JwtProvider in test context", e);
        }
    }

    private static boolean generateKeysWithScript() {
        File script = new File(SCRIPT_DIR + "jwt.sh");
        if (!script.exists()) {
            log.error("Script not found: {}", script.getAbsolutePath());
            return false;
        }

        try {
            if (!script.setExecutable(true)) {
                log.error("Could not make script executable");
                return false;
            }

            Process process = new ProcessBuilder("bash", script.getPath())
                    .inheritIO()
                    .start();

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("Script exited with code: {}", exitCode);
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("Script execution failed: {}", e.getMessage());
            return false;
        }
    }

    public static void cleanup() {
        if (publicKeyFile.exists()) publicKeyFile.delete();
        if (privateKeyFile.exists()) privateKeyFile.delete();
    }
}
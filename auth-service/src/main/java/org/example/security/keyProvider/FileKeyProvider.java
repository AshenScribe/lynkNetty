package org.example.security.keyProvider;

import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.Files.*;
import static java.nio.file.Paths.*;

public class FileKeyProvider implements KeyProvider {
    private final String publicKeyPath;
    private final String privateKeyPath;

    public FileKeyProvider(String publicKeyPath, String privateKeyPath) {
        this.publicKeyPath = publicKeyPath;
        this.privateKeyPath = privateKeyPath;
    }

    @Override
    public String getPublicKey() throws Exception {
        return new String(readAllBytes(get(publicKeyPath)));
    }

    @Override
    public String getPrivateKey() throws Exception {
        return new String(readAllBytes(get(privateKeyPath)));
    }
}
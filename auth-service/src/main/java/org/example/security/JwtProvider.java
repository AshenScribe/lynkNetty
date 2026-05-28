package org.example.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.example.security.keyProvider.KeyProvider;

import java.io.StringReader;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Date;

public class JwtProvider {
    private final Algorithm algorithm;
    public static final int TOKEN_EXPIRATION_TIME_IN_SECOND = 5 * 60;
    private static JwtProvider instance;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private JwtProvider(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public static void initialize(KeyProvider keyProvider) throws Exception {
        String publicKeyPem = keyProvider.getPublicKey();
        String privateKeyPem = keyProvider.getPrivateKey();

        ECPublicKey publicKey = (ECPublicKey) loadPublicKeyFromPem(publicKeyPem);
        ECPrivateKey privateKey = (ECPrivateKey) loadPrivateKeyFromPem(privateKeyPem);

        Algorithm algorithm = Algorithm.ECDSA256(publicKey, privateKey);
        instance = new JwtProvider(algorithm);
    }

    private static PublicKey loadPublicKeyFromPem(String pem) throws Exception {
        try (PEMParser pemParser = new PEMParser(new StringReader(pem))) {
            Object object = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");

            if (object instanceof SubjectPublicKeyInfo) {
                return converter.getPublicKey((SubjectPublicKeyInfo) object);
            }
            throw new IllegalArgumentException("Invalid public key format. Expected PUBLIC KEY or EC PUBLIC KEY");
        }
    }

    private static PrivateKey loadPrivateKeyFromPem(String pem) throws Exception {
        try (PEMParser pemParser = new PEMParser(new StringReader(pem))) {
            Object object = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");

            if (object instanceof PrivateKeyInfo) {
                return converter.getPrivateKey((PrivateKeyInfo) object);
            }
            throw new IllegalArgumentException("Invalid private key format. Expected PRIVATE KEY or EC PRIVATE KEY");
        }
    }

    public static JwtProvider getInstance() {
        if (instance == null) {
            throw new IllegalStateException("JwtProvider not initialized. Call initialize() first.");
        }
        return instance;
    }

    public String createToken(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }

        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME_IN_SECOND * 1000L))
                .sign(algorithm);
    }

    public String decodeToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        return JWT.require(algorithm)
                .build()
                .verify(token)
                .getSubject();
    }
}
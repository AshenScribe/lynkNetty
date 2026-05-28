package org.example.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;

public class JwtProvider {
    private static final String SECRET = System.getenv("JWT_SECRET");
    private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET != null ? SECRET : "super-secret-default-key");

    public static String createToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600_000))
                .sign(ALGORITHM);
    }

    public static String decodeToken(String token) {
        return JWT.decode(token).getSubject();
    }
}
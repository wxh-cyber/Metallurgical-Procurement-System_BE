package com.metallurgy.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    private final Key key;
    private final long expirationMs;

    public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration-ms}") long expirationMs) {
        if (secret.length() < 32) throw new IllegalArgumentException("jwt.secret 至少需要 32 个字符");
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String issue(Long id, String username, String role) {
        Date now = new Date();
        return Jwts.builder().setSubject(String.valueOf(id)).claim("username", username).claim("role", role)
                .setIssuedAt(now).setExpiration(new Date(now.getTime() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256).compact();
    }

    public Claims parse(String token) { return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody(); }
    public long getExpirationMs() { return expirationMs; }
}

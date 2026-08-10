package com.biblioteca_idat_api.biblioteca_idat_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    private static final String TOKEN_TYPE_CLAIM = "token_type";

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(UserPrincipal userPrincipal) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        claims.put("roles", roles);
        claims.put(TOKEN_TYPE_CLAIM, TokenType.ACCESS.name());
        return buildToken(claims, userPrincipal.getUsername(), accessTokenExpirationMs);
    }

    public String generateRefreshToken(UserPrincipal userPrincipal) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE_CLAIM, TokenType.REFRESH.name());
        return buildToken(claims, userPrincipal.getUsername(), refreshTokenExpirationMs);
    }

    private String buildToken(Map<String, Object> claims, String subject, long expirationMs) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isAccessTokenValid(String token, String username) {
        return isTokenValid(token, username, TokenType.ACCESS);
    }

    public boolean isRefreshTokenValid(String token, String username) {
        return isTokenValid(token, username, TokenType.REFRESH);
    }

    private boolean isTokenValid(String token, String username, TokenType expectedType) {
        Claims claims = extractClaims(token);
        String tokenType = claims.get(TOKEN_TYPE_CLAIM, String.class);
        return username.equals(claims.getSubject())
                && expectedType.name().equals(tokenType)
                && claims.getExpiration().after(new Date());
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public LocalDateTime extractExpirationDateTime(String token) {
        return LocalDateTime.ofInstant(
                extractClaims(token).getExpiration().toInstant(),
                ZoneId.systemDefault());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

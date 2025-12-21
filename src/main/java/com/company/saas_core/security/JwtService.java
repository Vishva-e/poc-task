package com.company.saas_core.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class JwtService {

	@Value("${app.security.jwt.secret}")
	private String jwtSecret;

	@Value("${app.security.jwt.expiration}")
	private long jwtExpirationMs;

	private Key key;

	@PostConstruct
	public void init() {
		key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}

	public String generateToken(String username, Long tenantId, List<String> roles) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + jwtExpirationMs);

		return Jwts.builder().setSubject(username).claim("tenantId", tenantId).claim("roles", roles).setIssuedAt(now)
				.setExpiration(expiry).signWith(key, SignatureAlgorithm.HS256).compact();
	}

	public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        
    }
}

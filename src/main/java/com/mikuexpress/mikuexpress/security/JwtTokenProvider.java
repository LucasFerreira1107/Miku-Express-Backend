package com.mikuexpress.mikuexpress.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

	@Value("${jwt.secret-key}")
	private String secretKeyString;
	
	@Value("${jwt.expiration-ms}")
	private long jwtExpirationInMs;
	
	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(secretKeyString.getBytes());
	}
	
	public String generateToken(Authentication authentication) {
		UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
		Instant now = Instant.now();
		Instant expiryDate = now.plus(jwtExpirationInMs, ChronoUnit.MILLIS);
		
		List<String> roles = userPrincipal.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.toList();
		
		return Jwts.builder().subject(userPrincipal.getUsername())
				.claim("roles", roles)
				.issuedAt(Date.from(now))
				.expiration(Date.from(expiryDate))
				.signWith(getSigningKey())
				.compact();
	}
	
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	private <T> T getClaimFromToken(String token,Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = getClaimFromToken(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
	
	
}

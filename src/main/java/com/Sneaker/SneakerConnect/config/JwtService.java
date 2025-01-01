package com.Sneaker.SneakerConnect.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${SECRET_KEY}")
    private String SECRET_KEY;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractPayload(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractClaims(String access_token) {
        return extractPayload(access_token);
    }

    public List<GrantedAuthority> extractRoles(Claims claims) {
        List<String> roles = (List<String>) claims.get("Roles");

        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    // Indirectly validates the token signature via extractUsername()
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public String generateAccessToken(UserDetails userDetails) {
        return generateAccessToken(userDetails, new HashMap<>());
    }

    /*
    Token generation
    1: data = base64url(header) + "." + base64url(payload)
    2: signature = HMACsha256(data, secretKey)
    3: token = header.payload.signature
     */
    public String generateAccessToken(UserDetails userDetails, Map<String, List<String>> extraClaims) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 15); // token expires in 15 mins

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(calendar.getTime())
                .claims(extraClaims)
                .signWith(getSigningKey())
                .compact();
    }

    /*
    Purpose: Ultimately extracts the claims from a token but first validates the signature so token validation happens here.
                An exception is thrown if token validation fails. DefaultJwtParser is the implementing class that does token validation
    Simplified Flow:
    Step 1: Jwts.parser() → returns JwtParserBuilder.
    Step 2: .build() → returns JwtParser (implemented by DefaultJwtParser).
    Step 3: .parseSignedClaims(token) → returns Jws<Claims> (likely implemented by DefaultJws).
    Step 4: .getPayload() → extracts the Claims (payload) from the Jws<Claims>.
    */
    private Claims extractPayload(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token) // token validation happens here by DefaultJwtParser class
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

package com.yuriykoziy.issueTracker.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.yuriykoziy.issueTracker.models.UserProfile;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
// implements various JWT token operations
public class JwtService {
   private static final String SECRET_KEY = "472D4B6150645367556B58703273357638792F423F4528482B4D625165546857";

   public String extractUsername(String token) {
      return extractClaim(token, Claims::getSubject);
   }

   public Date extractExpiration(String token) {
      return extractClaim(token, Claims::getExpiration);
   }

   public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
      final Claims claims = extractAllClaims(token);
      return claimsResolver.apply(claims);
   }

   public String generateToken(UserProfile userProfile) {
      return generateToken(new HashMap<>(), userProfile);
   }

   public String generateToken(Map<String, Object> extraClaims, UserProfile userProfile) {
      return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userProfile.getUsername())
            .claim("id", userProfile.getId())
            .claim("role", userProfile.getUserRole())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
   }

   public Boolean isTokenValid(String token, UserProfile userProfile) {
      final String username = extractUsername(token);
      return (username.equals(userProfile.getUsername()) && !isTokenExpired(token));
   }

   private Claims extractAllClaims(String token) {
      return Jwts
            .parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
   }

   private Key getSignInKey() {
      byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
      return Keys.hmacShaKeyFor(keyBytes);
   }

   private Boolean isTokenExpired(String token) {
      final Date expiration = extractExpiration(token);
      return expiration.before(new Date());
   }
}

package com.kevin.todo_app.security.jwt;

import com.kevin.todo_app.security.model.MainUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProvider {

   @Value("${jwt.secret}")
   private String secret;

   @Value("${jwt.expiration}")
   private Integer expiration;

   private SecretKey key;

   @PostConstruct
   public void initKey() {
      this.key = Keys.hmacShaKeyFor(secret.getBytes());
   }

   public String generateToken(MainUser mainUser) {
      Map<String, Object> claims = new HashMap<>();
      claims.put("sub", mainUser.getUsername());
      claims.put("roles", mainUser.getRoles());
      return Jwts.builder()
         .subject(mainUser.getUsername())
         .expiration(new Date(System.currentTimeMillis() + (expiration * 1000)))
         .claims(claims)
         .signWith(key)
         .compact();
   }

   public Claims getAllClaimsFromToken(String token) {
      return Jwts.parser().verifyWith(key)
         .build()
         .parseSignedClaims(token)
         .getPayload();
   }

   public Date getExpirationDateFromToken(String token) {
      return getAllClaimsFromToken(token).getExpiration();
   }

   private boolean isExpired(String token) {
      final Date expiration = getExpirationDateFromToken(token);
      return expiration.before(new Date());
   }

   public String getUserNameFromToken(String token) {
      return getAllClaimsFromToken(token).getSubject();
   }

   public boolean validateToken(String token) {
      return !isExpired(token);
   }
}

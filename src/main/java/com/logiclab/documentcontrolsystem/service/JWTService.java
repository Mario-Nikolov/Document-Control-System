package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.User;
import com.logiclab.documentcontrolsystem.exceptions.InvalidAuthorizationHeaderException;
import com.logiclab.documentcontrolsystem.exceptions.MissingAuthorizationHeaderException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JWTService {

    private final String SECRET = "mySecretKeyThatIsVeryLongAndSecure";

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("username", user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extractToken(String authHeader) {
        if (authHeader == null )
            throw new MissingAuthorizationHeaderException();

        if(!authHeader.startsWith("Bearer "))
            throw new InvalidAuthorizationHeaderException();

        return authHeader.substring(7);
    }
}

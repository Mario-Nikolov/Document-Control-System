package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.User;
import com.logiclab.documentcontrolsystem.exceptions.InvalidAuthorizationHeaderException;
import com.logiclab.documentcontrolsystem.exceptions.MissingAuthorizationHeaderException;
import com.logiclab.documentcontrolsystem.exceptions.NotFoundException;
import com.logiclab.documentcontrolsystem.exceptions.UnauthorizedException;
import com.logiclab.documentcontrolsystem.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class JWTService {

    private final String SECRET = "mySecretKeyThatIsVeryLongAndSecure";
    private final UserRepository userRepository;

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
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("JWT token has expired. Please login again.", e);

        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException("Invalid JWT token.", e);
        }
    }

    public String extractToken(String authHeader) {
        if (authHeader == null )
            throw new MissingAuthorizationHeaderException();

        if(!authHeader.startsWith("Bearer "))
            throw new InvalidAuthorizationHeaderException();

        return authHeader.substring(7);
    }

    public User extractUser(String authHeader){
        String token = extractToken(authHeader);

        String email = extractEmail(token);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with email: " + email +  " not found!"));
    }
}

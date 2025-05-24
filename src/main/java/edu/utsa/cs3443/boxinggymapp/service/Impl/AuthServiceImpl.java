package edu.utsa.cs3443.boxinggymapp.service.Impl;

import edu.utsa.cs3443.boxinggymapp.dto.AuthentificationTokenDTO;
import edu.utsa.cs3443.boxinggymapp.service.AuthService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${auth.secret.key}")
    private String SECRET_KEY;

    @Value("${auth.expiration_time}")
    private String EXPIRATION_TIME;

    @Override
    public AuthentificationTokenDTO login(String username, String userType) {
        Date expirationTime = new Date(System.currentTimeMillis() + Long.parseLong(EXPIRATION_TIME));
        String token = Jwts.builder()
                .subject(username)
                .claim("userType", userType)
                .issuedAt(new Date())
                .expiration(expirationTime)
                .signWith(new SecretKeySpec(Base64.getDecoder()
                        .decode(SECRET_KEY.getBytes(StandardCharsets.UTF_8)), "HmacSHA256"))
                .compact();
        AuthentificationTokenDTO authentificationTokenDTO = new AuthentificationTokenDTO();
        authentificationTokenDTO.setToken(token);
        authentificationTokenDTO.setExpirationTimeMillis(expirationTime.getTime());
        return authentificationTokenDTO;
    }

    @Override
    public String getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    @Override
    public String getUsernameFromJWT(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(new SecretKeySpec(Base64.getDecoder()
                            .decode(SECRET_KEY.getBytes(StandardCharsets.UTF_8)), "HmacSHA256"))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (MalformedJwtException e) {
            throw new RuntimeException("Invalid JWT token");
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("JWT token is expired");
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException("JWT token is unsupported");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("JWT claims string is empty");
        }
    }

    @Override
    public String getUserTypeFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(new SecretKeySpec(Base64.getDecoder()
                        .decode(SECRET_KEY.getBytes(StandardCharsets.UTF_8)), "HmacSHA256"))
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("userType", String.class);
    }
}

package com.realbeatz.security.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;

public class JwtUtils {
    private static final String AUTHORITIES_HEADER = "authorities";
    public static String generateAccessToken(String subject,
                                             Collection<? extends GrantedAuthority> authorities,
                                             Integer daysUntilExpiration,
                                             SecretKey secretKey) {

        return Jwts.builder()
                .setSubject(subject)
                .claim(AUTHORITIES_HEADER, authorities)
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(
                        LocalDate.now()
                                .plusDays(daysUntilExpiration)))
                .signWith(secretKey)
                .compact();
    }

    public static String generateRefreshToken(String subject,
                                             Collection<? extends GrantedAuthority> authorities,
                                             Integer daysUntilExpiration,
                                             SecretKey secretKey) {

        return Jwts.builder()
                .setSubject(subject)
                .claim(AUTHORITIES_HEADER, authorities)
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(
                        LocalDate.now()
                                .plusDays(daysUntilExpiration)))
                .signWith(secretKey)
                .compact();
    }
}

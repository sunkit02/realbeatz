package com.realbeatz.security.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;

public class JwtUtils {
    private static final String AUTHORITIES_HEADER = "authorities";
    public static String generateToken(@NonNull String subject,
                                       @NonNull Collection<? extends GrantedAuthority> authorities,
                                       @NonNull Integer daysUntilExpiration,
                                       @NonNull SecretKey secretKey) {

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

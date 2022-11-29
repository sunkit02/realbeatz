package com.realbeatz.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realbeatz.payloads.requests.UsernameAndPasswordAuthenticationRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;

    public JwtUsernameAndPasswordAuthenticationFilter(
            AuthenticationManager authenticationManager,
            JwtConfig jwtConfig,
            SecretKey secretKey) {
        this.authenticationManager = authenticationManager;
        this.jwtConfig = jwtConfig;
        this.secretKey = secretKey;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        try {
            UsernameAndPasswordAuthenticationRequest authenticationRequest =
                    new ObjectMapper().readValue(
                            request.getInputStream(),
                            UsernameAndPasswordAuthenticationRequest.class);
            logger.info("Attempting to authenticate: + " + authenticationRequest.getUsername());

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(),
                    authenticationRequest.getPassword()
            );

            return authenticationManager.authenticate(authentication);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws ServletException, IOException {
        String accessToken = JwtUtils.generateAccessToken(
                authResult.getName(),
                authResult.getAuthorities(),
                jwtConfig.getAccessTokenExpirationAfterDays(),
                secretKey
        );

        String refreshToken = JwtUtils.generateRefreshToken(
                authResult.getName(),
                authResult.getAuthorities(),
                jwtConfig.getRefreshTokenExpirationAfterDays(),
                secretKey
        );

        response.addHeader("access_token",
                jwtConfig.getTokenPrefix() + accessToken);
        response.addHeader("refresh_token",
                jwtConfig.getTokenPrefix() + refreshToken);
    }
}

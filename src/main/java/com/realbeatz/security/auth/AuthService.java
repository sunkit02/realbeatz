package com.realbeatz.security.auth;

import com.realbeatz.exceptions.AuthenticationException;
import com.realbeatz.exceptions.InvalidUsernameException;
import com.realbeatz.security.jwt.JwtConfig;
import com.realbeatz.security.jwt.JwtUtils;
import com.realbeatz.user.User;
import com.realbeatz.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collection;

@Slf4j
@Service
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;

    @Autowired
    public AuthService(UserService userService,
                       @Qualifier("bcrypt") PasswordEncoder passwordEncoder,
                       JwtConfig jwtConfig,
                       SecretKey secretKey) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtConfig = jwtConfig;
        this.secretKey = secretKey;
    }

    public Tokens authenticate(String username, String rawPassword) throws InvalidUsernameException, AuthenticationException {
        User subject = userService.getUserByUsername(username);
        String hashedPassword = subject.getPassword();
        if(passwordEncoder.matches(rawPassword, hashedPassword)) {
            log.info("User with username: {} authenticated successfully.", username);
            Collection<? extends GrantedAuthority> authorities =
                    subject.getAuthUserDetails().getAuthorities();
            String accessToken = JwtUtils.generateToken(
                    username,
                    authorities,
                    jwtConfig.getAccessTokenExpirationAfterDays(),
                    secretKey);

            String refreshToken = JwtUtils.generateToken(
                    username,
                    authorities,
                    jwtConfig.getRefreshTokenExpirationAfterDays(),
                    secretKey);

            return new Tokens(
                    jwtConfig.getTokenPrefix() + accessToken,
                    jwtConfig.getTokenPrefix() + refreshToken);
        } else {
            throw new AuthenticationException("Failed to authenticate subject with username: " + username);
        }
    }

    record Tokens(String accessToken, String refreshToken) {
    }
}

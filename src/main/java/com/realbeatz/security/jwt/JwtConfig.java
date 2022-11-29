package com.realbeatz.security.jwt;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Getter
@Setter
@ToString
@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "application.jwt")
public class JwtConfig {
    private String secretKey;
    private String tokenPrefix;
    private Integer accessTokenExpirationAfterDays;
    private Integer refreshTokenExpirationAfterDays;
    private String authoritiesHeader;
}

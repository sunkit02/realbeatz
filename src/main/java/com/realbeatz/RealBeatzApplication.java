package com.realbeatz;

import com.realbeatz.security.auth.AuthUserDetails;
import com.realbeatz.security.auth.roles.UserRole;
import com.realbeatz.user.User;
import com.realbeatz.user.UserRepository;
import com.realbeatz.user.profile.UserProfile;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@SpringBootApplication
@ConfigurationPropertiesScan("com.realbeatz.security.jwt")
public class RealBeatzApplication {
    public static void main(String[] args) {
        SpringApplication.run(RealBeatzApplication.class, args);
    }

    @Bean
    CommandLineRunner run(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        return args -> {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("password"))
                    .registrationDate(LocalDate.now())
                    .role(UserRole.ADMIN)
                    .build();

            UserProfile adminProfile = UserProfile.builder()
                    .user(admin)
                    .lastName("admin")
                    .firstName("admin")
                    .dob(LocalDate.now().minusDays(100))
                    .build();

            AuthUserDetails adminAuthUserDetails = AuthUserDetails.builder()
                    .user(admin)
                    .build();

            admin.setProfile(adminProfile);
            admin.setAuthUserDetails(adminAuthUserDetails);

            userRepository.save(admin);
            System.out.println(admin);
        };
    }
}

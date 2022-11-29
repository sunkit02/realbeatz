package com.realbeatz.security.auth;

import com.realbeatz.exceptions.InvalidUsernameException;
import com.realbeatz.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return userService.getUserByUsername(username)
                    .getAuthUserDetails();
        } catch (InvalidUsernameException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}

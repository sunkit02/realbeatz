package com.realbeatz.security.auth;

import com.realbeatz.user.User;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserDetails implements UserDetails {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long id;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            nullable = false)
    @MapsId
    @ToString.Exclude
    private User user;

    @Builder.Default
    private boolean isAccountNonExpired = true;
    @Builder.Default
    private boolean isAccountNonLocked = true;
    @Builder.Default
    private boolean isCredentialNonExpired = true;
    @Builder.Default
    private boolean isEnabled = true;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRole().getGrantedAuthorities();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
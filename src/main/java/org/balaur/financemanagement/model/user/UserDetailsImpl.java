package org.balaur.financemanagement.model.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class UserDetailsImpl implements UserDetails {
    private final String username;
    private final String email;
    private final String hashedPassword;
    private final boolean isLocked;
    private final Set<UserGroup> userGroup;

    public UserDetailsImpl(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.hashedPassword = user.getHashedPassword();
        this.isLocked = user.isLocked();
        this.userGroup = user.getUserGroups();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<UserGroup> userGroups = userGroup;
        Collection<GrantedAuthority> authorities = new ArrayList<>(userGroups.size());
        for (UserGroup userGroup : userGroups) {
            authorities.add(new SimpleGrantedAuthority(userGroup.getCode().toUpperCase()));
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return hashedPassword;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

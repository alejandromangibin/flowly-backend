package com.flowly.auth.service;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.flowly.config.multitenancy.TenantContextHolder;
import com.flowly.shared.model.User;
import com.flowly.shared.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository authUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Loading user by email: " + email + " (authentication)");
        
        User authUser = authUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        System.out.println("Found auth user: " + authUser.getEmail() + " for tenant: " + TenantContextHolder.getCurrentTenant());

        return new org.springframework.security.core.userdetails.User(
                authUser.getEmail(),
                authUser.getPassword(),
                true,   // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                AuthorityUtils.createAuthorityList(authUser.getRole())
        );
    }
}
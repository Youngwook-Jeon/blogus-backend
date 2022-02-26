package com.young.blogusbackend.service;

import com.young.blogusbackend.exception.SpringBlogusException;
import com.young.blogusbackend.model.Bloger;
import com.young.blogusbackend.repository.BlogerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final BlogerRepository blogerRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        Optional<Bloger> blogerOptional = blogerRepository.findByEmail(email);
        Bloger bloger = blogerOptional
                .orElseThrow(() -> new UsernameNotFoundException(email + " 계정을 찾을 수 없습니다."));

        return new User(
                bloger.getEmail(),
                bloger.getPassword(),
                bloger.isEnabled(),
                true,
                true,
                true,
                getAuthorities(bloger.getRole().name())
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
}

package com.young.blogusbackend.security;

import com.young.blogusbackend.exception.SpringBlogusException;
import com.young.blogusbackend.model.Bloger;
import com.young.blogusbackend.repository.BlogerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final BlogerRepository blogerRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String jwt = getJwtFromRequest(request);
        if (StringUtils.hasText(jwt) &&
                jwtProvider.validateTokenBySecret(jwt, JwtProvider.ACCESS_TOKEN_SECRET)) {
            Long id = jwtProvider.getDataFromJwt(jwt, JwtProvider.ACCESS_TOKEN_SECRET);
            UserDetails userDetails = getUserDetailsById(id);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private UserDetails getUserDetailsById(Long id) {
        Optional<Bloger> blogerOptional = blogerRepository.findById(id);
        Bloger bloger = blogerOptional
                .orElseThrow(() -> new SpringBlogusException("존재하지 않는 계정입니다."));

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

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

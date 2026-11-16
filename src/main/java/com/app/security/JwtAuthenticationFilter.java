
package com.app.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            try {
                String username =
                        jwtTokenUtil.getUsernameFromToken(token);

                if (username != null &&
                    SecurityContextHolder.getContext()
                        .getAuthentication() == null) {

                    UserDetails details =
                            userDetailsService.loadUserByUsername(
                                username);

                    if (jwtTokenUtil.validateToken(token, details)) {

                        UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                details,
                                null,
                                details.getAuthorities()
                            );

                        auth.setDetails(
                            new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                        );

                        SecurityContextHolder.getContext()
                            .setAuthentication(auth);
                    }
                }

            } catch (JwtException |
                     IllegalArgumentException |
                     org.springframework.security.core.AuthenticationException e) {

                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(request, response);
    }
}

package com.example.BasicSecuritySetup.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor

public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    // Built into Spring Framework
    private final UserDetailsService userDetailsService;



    @Override
    protected void doFilterInternal(
           @NonNull HttpServletRequest request,
           @NonNull HttpServletResponse response,
           @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        // Get authHeader
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        // Check if authHead is a valid auth token
        if(authHeader == null || !authHeader.startsWith("Bearer")){
            //send request/ response to next filter
            filterChain.doFilter(request, response);
            return;
        }
        // Extract JWT token with the removal of bearer
        jwt = authHeader.substring(7);
        // Extract username from the given JWT
        username = jwtService.extractUsername(jwt);
        // Check to see if auth is already provided or if username is null
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            // Get user details
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            // Check if token is valid
            if (jwtService.isTokenValid(jwt, userDetails)){
                // Creation of auth token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                // Set auth details based on current request
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // Set created auth token in security context
                SecurityContextHolder.getContext().setAuthentication((authToken));
            }
        }
        // Continue request process
        filterChain.doFilter(request, response);

    }
}

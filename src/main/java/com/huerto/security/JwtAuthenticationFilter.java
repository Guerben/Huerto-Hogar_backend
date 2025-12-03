package com.huerto.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            
            logger.debug("JWT Filter - Request URI: {}", request.getRequestURI());
            logger.debug("JWT Filter - JWT present: {}", StringUtils.hasText(jwt));

            if (StringUtils.hasText(jwt)) {
                logger.info("JWT Filter - Processing JWT token for request: {}", request.getRequestURI());
                logger.debug("JWT Filter - Token (first 50 chars): {}...", jwt.length() > 50 ? jwt.substring(0, 50) : jwt);
                
                boolean isValid = tokenProvider.validateToken(jwt);
                logger.info("JWT Filter - Token valid: {}", isValid);
                
                if (isValid) {
                    try {
                        String email = tokenProvider.getUserEmailFromToken(jwt);
                        logger.info("JWT Filter - Email from token: {}", email);

                        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                        logger.info("JWT Filter - User loaded successfully: {}", email);
                        logger.debug("JWT Filter - User authorities: {}", userDetails.getAuthorities());
                        
                        UsernamePasswordAuthenticationToken authentication = 
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.info("JWT Filter - Authentication set successfully for user: {}", email);
                    } catch (UsernameNotFoundException ex) {
                        logger.error("JWT Filter - User not found in database: {}", ex.getMessage());
                        // No establecer autenticación - Spring Security rechazará la petición
                    } catch (Exception ex) {
                        logger.error("JWT Filter - Error loading user details: {}", ex.getMessage(), ex);
                        logger.error("JWT Filter - Exception stack trace:", ex);
                        // No establecer autenticación - Spring Security rechazará la petición
                    }
                } else {
                    logger.warn("JWT Filter - Token validation failed for request: {}", request.getRequestURI());
                    logger.warn("JWT Filter - This means the token signature is invalid, expired, or malformed");
                }
            } else {
                logger.debug("JWT Filter - No JWT token found in request: {}", request.getRequestURI());
            }
        } catch (Exception ex) {
            logger.error("JWT Filter - Could not set user authentication in security context", ex);
            logger.error("Exception details - Message: {}, Class: {}", ex.getMessage(), ex.getClass().getName());
            if (ex.getCause() != null) {
                logger.error("Caused by: {}", ex.getCause().getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}


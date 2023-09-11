package com.sabeer.electronic.store.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtHelper jwtHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // get token from request
        // Authorization header
        String authorizationHeader = request.getHeader("Authorization");
        // Bearer 23491243b12fkaljjnfasfjh
        LOGGER.info("Header : {}", authorizationHeader);
        String username = null;
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
            // looking good
            token = authorizationHeader.substring(7);
            try {
                username = this.jwtHelper.getUsernameFromToken(token);
            } catch (IllegalArgumentException ex) {
                LOGGER.error("Illegal Argument while fetching the username !!");
                ex.printStackTrace();
            } catch (ExpiredJwtException ex) {
                LOGGER.error("Given jwt token is expired !!");
                ex.printStackTrace();
            } catch (MalformedJwtException ex) {
                LOGGER.error("Some changes has done in token !! Invalid Token");
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            LOGGER.info("Invalid Header Value !!");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // fetch user detail from username
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            Boolean isValidToken = jwtHelper.validateToken(token, userDetails);
            if (isValidToken) {
                // set the authentication
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                LOGGER.info("Validation fails !!");
            }
        }
        filterChain.doFilter(request, response);
    }
}

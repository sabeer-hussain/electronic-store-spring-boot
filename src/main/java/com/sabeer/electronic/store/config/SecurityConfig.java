package com.sabeer.electronic.store.config;

import com.sabeer.electronic.store.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    // User hardcoding : In-Memory Authentication
    /*
    @Bean
    public UserDetailsService userDetailsService() {
        // users create
        UserDetails normalUser = User.builder()
                .username("Ankit")
                .password(passwordEncoder().encode("ankit"))
                .roles("NORMAL")
                .build();

        UserDetails adminUser = User.builder()
                .username("Sabeer")
                .password(passwordEncoder().encode("sabeer"))
                .roles("ADMIN")
                .build();

//        InMemoryUserDetailsManager - is implementation class of UserDetailService
        return new InMemoryUserDetailsManager(normalUser, adminUser);
    }
     */

    // Database Authentication
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(this.userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

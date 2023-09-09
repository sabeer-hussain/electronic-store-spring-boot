package com.sabeer.electronic.store.config;

import com.sabeer.electronic.store.security.JwtAuthenticationEntryPoint;
import com.sabeer.electronic.store.security.JwtAuthenticationFilter;
import com.sabeer.electronic.store.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter authenticationFilter;

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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Form based login authentication
        /*
        http
           .authorizeRequests()
           .anyRequest()
           .authenticated()
           .and()
           .formLogin()
           .loginPage("login.html")
           .loginProcessingUrl("/process-url")
           .defaultSuccessUrl("/dashboard")
           .failureUrl("error")
           .and()
           .logout()
           .logoutUrl("/logout");
        return http.build();
         */

        // Basic Authentication
        /*
        http
            .csrf()
            .disable()
            .cors()
            .disable()
            .authorizeRequests()
            .anyRequest()
            .authenticated()
            .and()
            .httpBasic();
        return http.build();
         */

        // Configure JWT in spring security config, i.e. JWT Token Based Authentication
        http
            .csrf()
            .disable()
            .cors()
            .disable()
            .authorizeRequests()
            .antMatchers("/auth/login")
            .permitAll()
            .antMatchers(HttpMethod.POST, "/users")
            .permitAll()
            .antMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")
            .anyRequest()
            .authenticated()
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint)
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

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

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}

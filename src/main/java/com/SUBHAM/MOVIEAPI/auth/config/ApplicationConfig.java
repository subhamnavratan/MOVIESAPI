package com.SUBHAM.MOVIEAPI.auth.config;

import com.SUBHAM.MOVIEAPI.auth.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfig {

    private final UserRepository userRepository;

    public ApplicationConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ------------------------------------------------------------------
    // What is UserDetailsService?
    // Ans: A Spring Security interface used to load user information
    //      during authentication (login).
    //
    // Why is this REQUIRED?
    // Ans: Spring Security NEVER talks to the database directly.
    //      It always asks UserDetailsService:
    //      "Give me user details for this username"
    //
    // Without this bean:
    // - AuthenticationProvider cannot work
    // - Login will always fail
    // ------------------------------------------------------------------
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with email: " + username
                        )
                );
    }

    // ------------------------------------------------------------------
    // What is AuthenticationProvider?
    // Ans: It performs the ACTUAL authentication logic.
    //
    // It does:
    // 1. Calls UserDetailsService
    // 2. Loads user from DB
    // 3. Encodes raw password
    // 4. Compares with stored password
    // 5. Approves or rejects login
    //
    // IMPORTANT (Spring Security 6):
    // - DaoAuthenticationProvider MUST receive UserDetailsService
    //   via constructor (no default constructor exists)
    // ------------------------------------------------------------------
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider =
                new DaoAuthenticationProvider(userDetailsService());

        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    // ------------------------------------------------------------------
    // What is AuthenticationManager?
    // Ans: The CENTRAL coordinator of authentication.
    //
    // It:
    // - Receives authentication request
    // - Delegates to appropriate AuthenticationProvider
    //
    // Without this:
    // - You cannot manually authenticate users
    // - JWT login flow will not work
    // ------------------------------------------------------------------
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ------------------------------------------------------------------
    // PasswordEncoder
    //
    // BCrypt:
    // - Salted
    // - Secure
    // - Industry standard
    // - Recommended by Spring Security
    // ------------------------------------------------------------------
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     ------------------- AUTHENTICATION FLOW -------------------

     1. Client sends email + password
     2. AuthenticationManager receives request
     3. AuthenticationProvider is invoked
     4. UserDetailsService loads user from DB
     5. PasswordEncoder compares passwords
     6. Authentication succeeds or fails
     7. JWT is generated after success

     -----------------------------------------------------------
     */
}

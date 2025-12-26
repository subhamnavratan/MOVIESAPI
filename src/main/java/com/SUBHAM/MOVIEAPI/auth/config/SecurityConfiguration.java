package com.SUBHAM.MOVIEAPI.auth.config;

import com.SUBHAM.MOVIEAPI.auth.services.AuthFilterService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration

@EnableWebSecurity
@EnableMethodSecurity
// Enables method-level security
// Allows usage of:
// @PreAuthorize
// @PostAuthorize
// @Secured
public class SecurityConfiguration {

    private final AuthFilterService authFilterService;
    private final AuthenticationProvider authenticationProvider;
    public SecurityConfiguration(
            AuthFilterService authFilterService,
            AuthenticationProvider authenticationProvider
    ) {
        this.authFilterService = authFilterService;
        this.authenticationProvider = authenticationProvider;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // ---------------------------------------------------------
                // CSRF is disabled because:
                // - We are using JWT (stateless authentication)
                // - No session or cookies are used
                // ---------------------------------------------------------
                .csrf(AbstractHttpConfigurer::disable)

                // ---------------------------------------------------------
                // Authorization rules
                // ---------------------------------------------------------
                .authorizeHttpRequests(auth -> auth

                        // Public endpoints (NO authentication required)
                        .requestMatchers(
                                "/api/v1/auth/**",        // login / register
                                "/forgotPassword/**",     // password reset
                                "/file/**"                // serve poster images
                        ).permitAll()

                        // All other endpoints require authentication
                        .anyRequest()
                        .authenticated()
                )

                // ---------------------------------------------------------
                // Session management
                // ---------------------------------------------------------
                .sessionManagement(session -> session
                        // JWT is STATELESS:
                        // - No HTTP session
                        // - Every request must carry token
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // ---------------------------------------------------------
                // Authentication Provider
                //
                // Tells Spring Security:
                // "Use this provider to authenticate requests"
                // ---------------------------------------------------------
                .authenticationProvider(authenticationProvider)

                // ---------------------------------------------------------
                // Custom JWT filter
                //
                // This filter:
                // - Extracts JWT from Authorization header
                // - Validates token
                // - Sets Authentication in SecurityContext
                //
                // Must run BEFORE UsernamePasswordAuthenticationFilter
                // ---------------------------------------------------------
                .addFilterBefore(
                        authFilterService,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}

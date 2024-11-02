package com.spring.authentication.configuration;

import com.spring.authentication.services.AccessDeniedService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
* We have created our filters, validated the tokens and updated the security context but we still have not binded all these items so that they can be used.
* this class takes care of that.
* */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final AuthenticationProvider authenticationProvider;
    private final JWTAuthenticationFilter jwtAuthFilter;
    private final AccessDeniedService accessDeniedHandler;
    /*
* At the application startup, spring security will try to look up for security filter chain which is responsible for all the security
* Basically this will guide springboot about the process of validating and updating security context as this is the binding class of all the resources
* */
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    System.out.println("----------------------");
    http
            .csrf(csrf -> csrf.disable())  // Disable CSRF protection (only if necessary)
            .authorizeRequests(authorizeRequests -> authorizeRequests
                    .requestMatchers("/auth/v1/**").authenticated()  // Adjust this to your desired public endpoints. this will authorize all the methods that are created
                    // in AuthenticationController
                    .anyRequest().permitAll()        // Any other request requires authentication
            ) // this is basically to authorize any other request that does not match with our pattern in line 33. In this case we want to authorize all so
            // we are keeping it blank
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Set session management policy
            )
            .authenticationProvider(authenticationProvider)  // Set the authentication provider
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exceptionHandling -> exceptionHandling
                    .accessDeniedHandler(accessDeniedHandler)  // Set custom AccessDeniedHandler
            );; // Add custom JWT filter

    return http.build();  // Build and return the SecurityFilterChain

}
}

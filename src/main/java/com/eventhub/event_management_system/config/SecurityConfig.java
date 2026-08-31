package com.eventhub.event_management_system.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.eventhub.event_management_system.security.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(session ->
                session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                ))

                
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/register",
                        		"/auth/login"
                        ).permitAll()
                        
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/events/**"
                        ).authenticated()

                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/events/**"
                        ).hasRole("ORGANIZER")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.PUT,
                                "/events/**"
                        ).hasRole("ORGANIZER")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.DELETE,
                                "/events/**"
                        ).hasRole("ORGANIZER")
                        
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")
                        
                        .requestMatchers("/organizer/**")
                        .hasRole("ORGANIZER")
                        
                        .requestMatchers("/check-in/**")
                        .hasRole("ORGANIZER")
                        
                        .anyRequest().authenticated()
                )
                
                .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}
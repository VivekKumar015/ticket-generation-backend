package com.ticketsystem.config;

import com.ticketsystem.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/config/**").hasRole("SUPER_ADMIN")
                .requestMatchers("/api/reports/**").hasRole("SUPER_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/tickets/my").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/tickets/assigned").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/tickets/search").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/tickets/project/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/tickets/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/tickets").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/tickets/**").authenticated()
                .requestMatchers("/api/projects/**").authenticated()
                .requestMatchers("/api/tickets/{ticketId}/comments/**").authenticated()
                .requestMatchers("/api/users/me").authenticated()
                .requestMatchers("/api/users/employees").authenticated()
                .requestMatchers("/api/users").hasRole("SUPER_ADMIN")
                .requestMatchers("/api/dashboard/admin").hasRole("SUPER_ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
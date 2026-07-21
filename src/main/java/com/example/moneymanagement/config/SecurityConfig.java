package com.example.moneymanagement.config;

import com.example.moneymanagement.security.JWTRequestFilter;
import com.example.moneymanagement.service.AppUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.http.HttpMethod;


import org.springframework.security.config.Customizer;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AppUserDetailsService appUserDetailsService;
    private final JWTRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

         http
//                 .csrf(AbstractHttpConfigurer::disable)
//                 .authorizeHttpRequests(auth->auth.anyRequest().permitAll());
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                 .authorizeHttpRequests(auth -> auth
                         .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                         .requestMatchers(
                                 "/register",
                                 "/activate",
                                 "/login",
                                 "/status",
                                 "/health"
                         ).permitAll()
                         .anyRequest().authenticated()
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(
//                                "/register",
//                                "/activate",
//                                "/login",
//                                "/status",
//                                "/health"
//                        ).permitAll()
//                        .anyRequest().authenticated()
                ).sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                 .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource(){
//        CorsConfiguration corsConfiguration=new CorsConfiguration();
//        corsConfiguration.setAllowedOriginPatterns(List.of("*"));
//        corsConfiguration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
//        corsConfiguration.setAllowedHeaders(List.of("Authorization","Content-Type","Accept"));
//        corsConfiguration.setAllowCredentials(true);
//        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**",corsConfiguration);
//        return source;
//    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(
                List.of("http://localhost:5173")
        );

        config.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );

        config.setAllowedHeaders(
                List.of("*")
        );

        config.setExposedHeaders(
                List.of("Authorization")
        );

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                config
        );

        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(){
        DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(appUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authenticationProvider);
    }
}

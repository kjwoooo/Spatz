package com.elice.spatz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 인증과정 없이 요청 가능한 url
    String[] urlsToBePermittedAll = {"/hello", "/login", "/h2-console/**", "/**", "/files/**"};

    // 인증 과정이 필요하여
    // 인증 없이 요청한 경우 로그인 페이지로 리다이렉션 합니다.
    String[] urlsToBeAuthenticated = {"/logout"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/files/**").permitAll()
                        .requestMatchers(urlsToBeAuthenticated).authenticated()
                        .anyRequest().permitAll()
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .formLogin(withDefaults()) // 기본 로그인 페이지 사용
                .logout(withDefaults()); // 로그아웃도 허용

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOriginPattern("*"); // 모든 출처 허용
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 적용
        return source;
    }
}
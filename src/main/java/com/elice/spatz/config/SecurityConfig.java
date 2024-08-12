package com.elice.spatz.config;

import com.elice.spatz.domain.user.service.CustomOAuth2UserService;
import com.elice.spatz.domain.user.service.TokenProvider;
import com.elice.spatz.exception.handler.GlobalExceptionHandler;
import com.elice.spatz.filter.JWTTokenValidatorFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTTokenValidatorFilter jwtTokenValidatorFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final SocialLoginSuccessHandler socialLoginSuccessHandler;
    private final GlobalExceptionHandler globalExceptionHandler;


    // 관리자(admin)만 접근이 가능한 리소스 (여기에다만 추가하시면 됩니다)
    String[] adminUrls = {"/admin/**"};

    // 인증(로그인)한 사용자만 접근이 가능한 리소스
    String[] urlsToBeAuthenticated = {"/logout", "/users/password/**",
                                      "/users/**", "/blocks/**", "/reports/**",
                                      "/friend-requests/**", "/friendships/**", "/servers/**",
                                        "/openvidu/**", "/voiceChats"
    };

    // 인증 과정에 필요하여 반드시 모두에게 허용이 되어야 하는 리소스
    String[] urlsToBePermittedAll = {"/users", "/users/password", "/mails/**", "/apiLogin/**", "/users/email", "/h2-console/**", "/users/profile", "/afterSocialLogin/**"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 개발 단계에서는 오로지 HTTP 만을 이용해서 통신하도록 설정
                .requiresChannel(rcc -> rcc.anyRequest().requiresInsecure())
                // CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                // JWT 토큰 시스템을 사용하기 위해 jsessionid 발급을 중단.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(withDefaults())
                // CORS 설정
                .cors(corsConfig -> corsConfig.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
                        config.setAllowedMethods(Collections.singletonList("*"));
                        config.setAllowCredentials(true);
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setExposedHeaders(List.of("Authorization", "Set-Cookie"));
                        // 위의 setAllowCredentials 가 true 이라면 자동으로 Set-Cookie 헤더에 클라이언트에 접근이 가능해진다.
                        config.setMaxAge(3600L);
                        return config;
                    }
                }))
                // 인증이 필요한 url 과 그렇지 않은 url 설정
                .authorizeHttpRequests(authorize -> authorize
                        // 인증을 해야만 접근할 수 있는 url 입니다.
                        .requestMatchers(urlsToBePermittedAll).permitAll()
                        .requestMatchers(urlsToBeAuthenticated).authenticated()
                        // 관리자 권한에게만 허용되는 url 입니다.
                        .requestMatchers(adminUrls).hasRole("ADMIN")
                )
                // 인증 작업 전 JWT 토큰 검증용 필터 추가
                .addFilterBefore(jwtTokenValidatorFilter, BasicAuthenticationFilter.class)
                // X-Frame-Options 헤더설정 for h2-database console
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                // Basic Authentication 이용한 인증작업 실패 시 어떠한 루틴이 실행될 것인가 설정.
                .httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()))
                // 403 Forbidden Error 발생 시 어떠한 루틴이 실행될 것인가 설정.
                .formLogin(AbstractHttpConfigurer::disable)
                // OAuth2 로그인 시, 리소스 서버로부터 제공받은 사용자 정보를 어떻게 가져올 것인가를 설정.
                .oauth2Login((olc) -> olc
                        .userInfoEndpoint((uiec) -> uiec.userService(customOAuth2UserService))
                        .successHandler(socialLoginSuccessHandler))
                .exceptionHandling(ehc -> ehc
                        .accessDeniedHandler(new CustomAccessDeniedHandler()))
                .logout(withDefaults());
                // OAuth2 로그인 시 구글 로그인 페이지로 리다이렉션

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // apiLogin 이라는 사용자 정의 인증 로직을 위해서는, 요청이 들어올 때 authentication process 를 시작하도록 하여야 한다.
    // 그렇게 하기 위해서는 Authentication Manager 를 구현해야 함
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        CustomAuthenticationProvider authenticationProvider =
                new CustomAuthenticationProvider(userDetailsService, passwordEncoder);

        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false); // 인증과정에서 authentication 객체의 비밀번호를 지우지 않고 넘겨주어서 사용자 정의 인증로직이 제대로 동작하게 함.
        return  providerManager;
    }

}

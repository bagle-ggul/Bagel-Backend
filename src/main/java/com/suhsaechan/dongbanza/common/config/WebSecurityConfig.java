package com.suhsaechan.dongbanza.common.config;

import com.suhsaechan.dongbanza.common.jwt.filter.TokenAuthenticationFilter;
import com.suhsaechan.dongbanza.common.jwt.service.CustomUserDetailsService;
import com.suhsaechan.dongbanza.common.jwt.service.JwtUtil;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

  private final JwtUtil jwtUtil;

  private final static String MEMBER = "USER"; // Spring 기본 유저
  private final static String ADMIN = "ADMIN"; // Spring 기본 관리자

  @Bean
  public WebSecurityCustomizer configure() {
    return (web) -> web.ignoring()
        .requestMatchers(new AntPathRequestMatcher("/static/**"));
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return
        http.cors(cors -> cors
                .configurationSource(corsConfigurationSource()))

            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)

            .authorizeHttpRequests((authorize) -> authorize
                // 공개 경로는 SecurityWhitelist 한 곳에서만 관리한다 (필터와 공유)
                .requestMatchers(SecurityWhitelist.PATHS).permitAll()
                .requestMatchers(HttpMethod.GET, "/api/my-page").hasAuthority(MEMBER)
                .requestMatchers(HttpMethod.POST, "/api/game/over").hasAuthority(MEMBER)
                .requestMatchers(HttpMethod.GET, "/api/game/my-results").hasAuthority(MEMBER)
                .requestMatchers(HttpMethod.DELETE, "/api/game/my-results/{gameResultId}").hasAuthority(MEMBER)
                .anyRequest().authenticated()
            )

            // JWT 무상태 API라 세션 기반 로그아웃 설정은 의미가 없어 제거함
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(new TokenAuthenticationFilter(jwtUtil),
                UsernamePasswordAuthenticationFilter.class)
            .build();
  }

  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http,
      BCryptPasswordEncoder bCryptPasswordEncoder,
      CustomUserDetailsService customUserDetailsService) throws Exception {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(customUserDetailsService);
    authProvider.setPasswordEncoder(bCryptPasswordEncoder);
    return new ProviderManager(authProvider);
  }

  // allowCredentials(true)와 "*"를 함께 쓰면 Spring이 요청 Origin을 그대로 반사한다.
  // 즉 임의의 사이트가 사용자 브라우저로 이 API를 호출하고 응답까지 읽을 수 있어 출처를 명시한다.
  private static final List<String> ALLOWED_ORIGIN_PATTERNS = List.of(
      "https://bagel.suhsaechan.kr",  // 운영 프론트엔드
      "http://localhost:[*]",         // 로컬 개발 (포트 무관)
      "http://127.0.0.1:[*]"
  );

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(ALLOWED_ORIGIN_PATTERNS);
    configuration.setAllowedMethods(
        Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    configuration.setAllowCredentials(true);
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setExposedHeaders(Arrays.asList("Authorization")); // Authorization 헤더 노출
    configuration.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
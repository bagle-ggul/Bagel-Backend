package com.suhsaechan.dongbanza.common.config;

/**
 * 인증 없이 접근 가능한 경로의 단일 정의.
 *
 * <p>기존에는 WebSecurityConfig(인가 규칙)와 TokenAuthenticationFilter(JWT 검사 생략)가
 * 각각 목록을 따로 들고 있어 내용이 어긋났다. 그 결과 필터는 통과시키지만 인가 단계에서
 * 막히는 경로가 생겼고, /actuator/health가 403이 되어 서버 생존 확인 수단이 없었다.
 * 두 곳이 이 상수를 공유하도록 해 재발을 막는다.
 */
public final class SecurityWhitelist {

  private SecurityWhitelist() {
  }

  public static final String[] PATHS = {
      "/",                    // 기본 화면
      "/api/signup",          // 회원가입
      "/api/login",           // 로그인
      "/api/token",           // Access Token 재발급
      "/docs/**",             // Swagger UI
      "/v3/api-docs/**",      // Swagger 스펙
      "/actuator/health",     // 헬스체크 (모니터링·배포 검증용)
      "/actuator/prometheus", // Prometheus 스크랩
      "/favicon.ico"
  };
}

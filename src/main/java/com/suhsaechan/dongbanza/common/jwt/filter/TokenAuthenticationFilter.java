package com.suhsaechan.dongbanza.common.jwt.filter;

import com.suhsaechan.dongbanza.common.config.SecurityWhitelist;
import com.suhsaechan.dongbanza.common.jwt.service.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.util.AntPathMatcher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final AntPathMatcher antPathMatcher = new AntPathMatcher();

  // 인가 규칙(WebSecurityConfig)과 동일한 목록을 공유해 두 곳이 어긋나지 않게 한다
  private static final String[] WHITELIST = SecurityWhitelist.PATHS;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String URI = request.getRequestURI();
    String authorizationHeader = request.getHeader("Authorization");

    // WHITELIST URL 인 경우 -> JWT Token Validation 하지않는다.
    if (Arrays.stream(WHITELIST)
        .anyMatch(whiteListUri -> antPathMatcher.match(whiteListUri, URI))) {
      log.debug("Whitelisted URI: {}", URI);
      // Token 검사 생략
      filterChain.doFilter(request, response);
      return;
    }

    // 이외 주소 Token 검사
    String token = getAccessToken(authorizationHeader);

    // 토큰 검사 통과 로직
    if (jwtUtil.validateToken(token)) {
      log.debug("Token 검사로직 통과: {}", URI);
      Authentication authentication = jwtUtil.getAuthentication(token);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    filterChain.doFilter(request, response);
  }

  private String getAccessToken(String authorizationHeader) {
    if (authorizationHeader != null && authorizationHeader.startsWith(
        "Bearer ")) {
      return authorizationHeader.substring("Bearer ".length());
    }
    return null;
  }
}
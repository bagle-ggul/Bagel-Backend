package com.suhsaechan.dongbanza.common.jwt.service;

import com.suhsaechan.dongbanza.common.jwt.dto.CustomUserDetails;
import com.suhsaechan.dongbanza.member.domain.constants.MemberRole;
import com.suhsaechan.dongbanza.member.domain.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtUtil {
  @Value("${spring.jwt.secret-key}")
  private String secretKey;

  @Value("${spring.jwt.access-expiration-time}")
  private Long accessTokenExpTime;
  @Value("${spring.jwt.refresh-expiration-time}")
  private Long refreshTokenExpTime;

  private final String ROLE ="role";

  // Access Token 생성
  public String createAccessToken(CustomUserDetails customUserDetails) {
    return createToken(customUserDetails, this.accessTokenExpTime);
  }

  // Refresh Token 생성
  public String createRefreshToken(CustomUserDetails customUserDetails) {
    return createToken(customUserDetails, this.refreshTokenExpTime);
  }

  private String createToken(CustomUserDetails customUserDetails,
      long expiredAt) {
    Date now = new Date();

    // Header 설정
    Map<String, Object> headers = new HashMap<>();
    headers.put("typ", "JWT");

    return Jwts.builder()
        .header()
        .add(headers)
        .and()
        .issuer("Saechan")
        .issuedAt(now)
        .expiration(new Date(now.getTime() + expiredAt))
        .subject(customUserDetails.getUsername())
        .claim(ROLE, customUserDetails.getMember().getRole())
        .signWith(getSigningKey())
        .compact();
  }

  // JWT 검증
  public boolean validateToken(String token) {
    try {
      Jwts.parser()
          .verifyWith(getSigningKey())
          .build()
          .parseSignedClaims(token);
      return true;
    } catch (io.jsonwebtoken.security.SecurityException |
             MalformedJwtException e) {
      log.info("Invalid JWT Token", e);
    } catch (ExpiredJwtException e) {
      log.info("Expired JWT Token", e);
    } catch (UnsupportedJwtException e) {
      log.info("Unsupported JWT Token", e);
    } catch (IllegalArgumentException e) {
      log.info("JWT claims string is empty.", e);
    }
    return false;
  }

  // 토큰 기반 -> 인증 정보 가져옴 // 누구의 토큰인가
  public Authentication getAuthentication(String token) {
    // 클레임 전문을 로깅하면 매 요청마다 토큰 주체·권한이 그대로 남아 로그 노출·소음이 커진다
    Claims claims = getClaims(token);

    Set<SimpleGrantedAuthority> authorities
        = Collections.singleton(new SimpleGrantedAuthority(claims.get(ROLE, String.class)));

    Member member = Member.builder()
        .id(Long.valueOf(claims.getSubject()))
        .role(MemberRole.valueOf(claims.get(ROLE, String.class)))
        .build();

    CustomUserDetails userDetails = CustomUserDetails.builder()
        .member(member)
        .build();

    return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
  }

  public Claims getClaims(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

}
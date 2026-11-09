package com.suhsaechan.dongbanza.common.jwt.dto;

import com.suhsaechan.dongbanza.member.domain.entity.Member;
import java.util.Collection;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@AllArgsConstructor
@Builder
public class CustomUserDetails implements UserDetails {
  private final Member member;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(new SimpleGrantedAuthority(member.getRole().getKey()));
  }

  // 비밀번호 인증을 쓰지 않는 게스트 구조라 빈 값 반환 (UserDetails 규약상 메서드는 필요)
  @Override
  public String getPassword() {
    return "";
  }

  // JWT subject: 회원 id (닉네임은 중복 허용이라 식별자로 쓰지 않음)
  @Override
  public String getUsername() {
    return member.getId().toString();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  // 탈퇴·휴면 개념이 없는 게스트 구조라 항상 사용 가능
  @Override
  public boolean isEnabled() {
    return true;
  }
}

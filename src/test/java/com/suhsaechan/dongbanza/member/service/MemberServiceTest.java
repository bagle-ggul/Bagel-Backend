package com.suhsaechan.dongbanza.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.suhsaechan.dongbanza.common.exception.ErrorCode;
import com.suhsaechan.dongbanza.common.exception.api.MemberException;
import com.suhsaechan.dongbanza.common.jwt.service.JwtUtil;
import com.suhsaechan.dongbanza.common.jwt.service.TokenService;
import com.suhsaechan.dongbanza.member.domain.constants.GameProgress;
import com.suhsaechan.dongbanza.member.domain.entity.Member;
import com.suhsaechan.dongbanza.member.dto.request.MemberSignUpForm;
import com.suhsaechan.dongbanza.member.dto.response.MemberDto;
import com.suhsaechan.dongbanza.member.repository.MemberRepository;
import java.lang.reflect.Field;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** 회원가입 도메인 규칙 검증. DB 없이 동작하도록 리포지토리를 목으로 대체한다. */
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

  @Mock
  private MemberRepository memberRepository;
  @Mock
  private BCryptPasswordEncoder passwordEncoder;
  @Mock
  private JwtUtil jwtUtil;
  @Mock
  private TokenService tokenService;

  @InjectMocks
  private MemberService memberService;

  private static MemberSignUpForm form(String email, String password, String characterName)
      throws Exception {
    MemberSignUpForm form = new MemberSignUpForm();
    set(form, "email", email);
    set(form, "password", password);
    set(form, "characterName", characterName);
    return form;
  }

  private static void set(Object target, String field, Object value) throws Exception {
    Field f = target.getClass().getDeclaredField(field);
    f.setAccessible(true);
    f.set(target, value);
  }

  @Test
  @DisplayName("캐릭터명을 생략하면 기본값 '주인공'이 저장된다")
  void appliesDefaultCharacterNameWhenOmitted() throws Exception {
    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(anyString())).thenReturn("encoded");
    when(memberRepository.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

    MemberDto saved = memberService.save(form("user@example.com", "pw", null));

    assertThat(saved.getCharacterName()).isEqualTo(Member.DEFAULT_CHARACTER_NAME);
  }

  @Test
  @DisplayName("캐릭터명이 공백뿐이어도 기본값으로 대체된다")
  void appliesDefaultCharacterNameWhenBlank() throws Exception {
    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(anyString())).thenReturn("encoded");
    when(memberRepository.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

    MemberDto saved = memberService.save(form("user@example.com", "pw", "   "));

    assertThat(saved.getCharacterName()).isEqualTo(Member.DEFAULT_CHARACTER_NAME);
  }

  @Test
  @DisplayName("캐릭터명을 지정하면 그대로 저장된다")
  void keepsProvidedCharacterName() throws Exception {
    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(anyString())).thenReturn("encoded");
    when(memberRepository.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

    MemberDto saved = memberService.save(form("user@example.com", "pw", "베이글"));

    assertThat(saved.getCharacterName()).isEqualTo("베이글");
  }

  @Test
  @DisplayName("점수·회귀 횟수는 0으로 초기화된다")
  void initialisesCountersToZero() throws Exception {
    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(anyString())).thenReturn("encoded");
    when(memberRepository.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

    MemberDto saved = memberService.save(form("user@example.com", "pw", null));

    assertThat(saved.getTotalScore()).isZero();
    assertThat(saved.getTotalRegressionCount()).isZero();
  }

  @Test
  @DisplayName("게임 진행 상태는 NOT_STARTED로 초기화된다")
  void initialisesGameProgressToNotStarted() throws Exception {
    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(anyString())).thenReturn("encoded");
    when(memberRepository.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

    MemberDto saved = memberService.save(form("user@example.com", "pw", null));

    assertThat(saved.getGameProgress()).isEqualTo(GameProgress.NOT_STARTED);
  }

  @Test
  @DisplayName("이미 가입된 이메일이면 EMAIL_ALREADY_EXISTS로 거부한다")
  void rejectsDuplicatedEmail() throws Exception {
    when(memberRepository.findByEmail(anyString()))
        .thenReturn(Optional.of(Member.builder().email("user@example.com").build()));

    assertThatThrownBy(() -> memberService.save(form("user@example.com", "pw", null)))
        .isInstanceOf(MemberException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMAIL_ALREADY_EXISTS);
  }
}

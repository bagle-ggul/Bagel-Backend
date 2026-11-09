package com.suhsaechan.dongbanza.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.suhsaechan.dongbanza.common.jwt.dto.CustomUserDetails;
import com.suhsaechan.dongbanza.common.jwt.service.JwtUtil;
import com.suhsaechan.dongbanza.common.jwt.service.TokenService;
import com.suhsaechan.dongbanza.member.domain.constants.GameProgress;
import com.suhsaechan.dongbanza.member.domain.constants.MemberRole;
import com.suhsaechan.dongbanza.member.domain.entity.Member;
import com.suhsaechan.dongbanza.member.dto.request.GuestStartForm;
import com.suhsaechan.dongbanza.member.dto.response.GuestStartResponse;
import com.suhsaechan.dongbanza.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** 게스트 시작 도메인 규칙 검증. DB 없이 동작하도록 리포지토리를 목으로 대체한다. */
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

  private static final String GENERATED_NICKNAME = "고소한베이글482";

  @Mock
  private MemberRepository memberRepository;
  @Mock
  private JwtUtil jwtUtil;
  @Mock
  private TokenService tokenService;
  @Mock
  private NicknameGenerator nicknameGenerator;

  @InjectMocks
  private MemberService memberService;

  @BeforeEach
  void stubCollaborators() {
    // 저장 시 DB가 부여하는 id를 흉내 내지 않고 입력 엔티티를 그대로 돌려준다
    org.mockito.Mockito.lenient().when(memberRepository.save(any(Member.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    org.mockito.Mockito.lenient().when(jwtUtil.createAccessToken(any(CustomUserDetails.class)))
        .thenReturn("access-token");
    org.mockito.Mockito.lenient().when(tokenService.createRefreshToken(any(Member.class)))
        .thenReturn("refresh-token");
    org.mockito.Mockito.lenient().when(nicknameGenerator.generate())
        .thenReturn(GENERATED_NICKNAME);
  }

  @Test
  @DisplayName("닉네임을 생략하면 자동 생성한 닉네임이 저장된다")
  void generatesNicknameWhenOmitted() {
    GuestStartResponse response = memberService.startAsGuest(new GuestStartForm(null, "ENFP"));

    assertThat(response.getCharacterName()).isEqualTo(GENERATED_NICKNAME);
  }

  @Test
  @DisplayName("닉네임이 공백뿐이어도 자동 생성한 닉네임으로 대체된다")
  void generatesNicknameWhenBlank() {
    GuestStartResponse response = memberService.startAsGuest(new GuestStartForm("   ", "ENFP"));

    assertThat(response.getCharacterName()).isEqualTo(GENERATED_NICKNAME);
  }

  @Test
  @DisplayName("닉네임을 지정하면 그대로 저장된다")
  void keepsProvidedNickname() {
    GuestStartResponse response = memberService.startAsGuest(new GuestStartForm("베이글", "ENFP"));

    assertThat(response.getCharacterName()).isEqualTo("베이글");
  }

  @Test
  @DisplayName("MBTI와 초기 상태(점수·회귀 횟수 0, NOT_STARTED, USER, ACTIVE)가 저장된다")
  void savesInitialMemberState() {
    memberService.startAsGuest(new GuestStartForm("베이글", "INTJ"));

    ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
    verify(memberRepository).save(captor.capture());
    Member saved = captor.getValue();

    assertThat(saved.getMbti()).isEqualTo("INTJ");
    assertThat(saved.getTotalScore()).isZero();
    assertThat(saved.getTotalRegressionCount()).isZero();
    assertThat(saved.getGameProgress()).isEqualTo(GameProgress.NOT_STARTED);
    assertThat(saved.getRole()).isEqualTo(MemberRole.USER);
  }

  @Test
  @DisplayName("액세스·리프레시 토큰이 함께 발급된다")
  void issuesBothTokens() {
    GuestStartResponse response = memberService.startAsGuest(new GuestStartForm("베이글", "ENFP"));

    assertThat(response.getAccessToken()).isEqualTo("access-token");
    assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
  }

  @Test
  @DisplayName("응답 body용 변환에서는 액세스 토큰이 빠진다")
  void excludesAccessTokenFromBody() {
    GuestStartResponse response = memberService.startAsGuest(new GuestStartForm("베이글", "ENFP"));

    GuestStartResponse body = response.exceptAccessToken();

    assertThat(body.getAccessToken()).isNull();
    assertThat(body.getRefreshToken()).isEqualTo("refresh-token");
  }

  @Test
  @DisplayName("닉네임 추천은 생성기 결과를 그대로 돌려주고 저장하지 않는다")
  void suggestsNicknameWithoutSaving() {
    assertThat(memberService.suggestNickname()).isEqualTo(GENERATED_NICKNAME);
    org.mockito.Mockito.verifyNoInteractions(memberRepository);
  }
}

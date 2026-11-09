package com.suhsaechan.dongbanza.member.service;

import com.suhsaechan.dongbanza.common.exception.ErrorCode;
import com.suhsaechan.dongbanza.common.exception.api.MemberException;
import com.suhsaechan.dongbanza.common.jwt.dto.CustomUserDetails;
import com.suhsaechan.dongbanza.common.jwt.service.JwtUtil;
import com.suhsaechan.dongbanza.common.jwt.service.TokenService;
import com.suhsaechan.dongbanza.member.domain.constants.GameProgress;
import com.suhsaechan.dongbanza.member.domain.constants.MemberRole;
import com.suhsaechan.dongbanza.member.domain.entity.Member;
import com.suhsaechan.dongbanza.member.dto.request.GuestStartForm;
import com.suhsaechan.dongbanza.member.dto.response.GuestStartResponse;
import com.suhsaechan.dongbanza.member.dto.response.MemberDto;
import com.suhsaechan.dongbanza.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final JwtUtil jwtUtil;
  private final TokenService tokenService;
  private final NicknameGenerator nicknameGenerator;

  /**
   * 게스트 시작: 닉네임 확정 > 회원 생성 > 토큰 발급
   * 가입·로그인 없이 닉네임과 MBTI만으로 플레이 계정 생성 (닉네임 중복 허용)
   */
  @Transactional
  public GuestStartResponse startAsGuest(GuestStartForm form) {
    // 닉네임 확정 (생략 시 자동 생성)
    String characterName = StringUtils.hasText(form.getCharacterName())
        ? form.getCharacterName().trim()
        : nicknameGenerator.generate();

    // 회원 생성
    Member member = memberRepository.save(Member.builder()
        .characterName(characterName)
        .mbti(form.getMbti())
        .role(MemberRole.USER)
        .totalScore(0) // 초기 호감도는 0으로 설정
        .totalRegressionCount(0) // 초기 회귀 횟수는 0으로 설정
        .gameProgress(GameProgress.NOT_STARTED) // 초기 게임 진행 상태
        .build());

    // 토큰 발급 (리프레시 토큰 저장 포함이라 한 트랜잭션으로 묶음)
    String accessToken = jwtUtil.createAccessToken(
        CustomUserDetails.builder()
            .member(member)
            .build());
    String refreshToken = tokenService.createRefreshToken(member);

    return GuestStartResponse.from(member, accessToken, refreshToken);
  }

  // 닉네임 입력 화면의 초기값 제안 (저장하지 않음)
  public String suggestNickname() {
    return nicknameGenerator.generate();
  }

  @Transactional(readOnly = true)
  public MemberDto getMemberById(Long id) {
    Member member = memberRepository.findById(id)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    return MemberDto.from(member);
  }
}

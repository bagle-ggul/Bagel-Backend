package com.suhsaechan.dongbanza.member.dto.response;

import com.suhsaechan.dongbanza.member.domain.constants.GameProgress;
import com.suhsaechan.dongbanza.member.domain.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberDto {
  private Long id;

  private String characterName;

  private String role;

  private Integer totalScore;

  private String mbti;

  private Integer totalRegressionCount;

  // Jackson이 상수명을 그대로 직렬화하므로 응답 JSON 형태는 기존과 동일하다.
  // role처럼 String.valueOf()로 변환하면 null이 문자열 "null"로 새어나가 타입을 유지한다.
  private GameProgress gameProgress;

  // 리프레시 토큰은 refresh_token 테이블이 정본이며 회원 응답에 노출할 이유가 없어 제거함
  public static MemberDto from(Member member){
    return MemberDto.builder()
        .id(member.getId())
        .characterName(member.getCharacterName())
        .role(String.valueOf(member.getRole()))
        .totalScore(member.getTotalScore() != null ? member.getTotalScore() : 0)
        .mbti(member.getMbti())
        .totalRegressionCount(member.getTotalRegressionCount() != null ? member.getTotalRegressionCount() : 0)
        .gameProgress(member.getGameProgress())
        .build();
  }
}

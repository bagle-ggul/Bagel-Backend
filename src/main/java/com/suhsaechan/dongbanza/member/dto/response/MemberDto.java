package com.suhsaechan.dongbanza.member.dto.response;

import com.suhsaechan.dongbanza.member.domain.entity.Member;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberDto {
  private Long id;

  private String email;

  private String characterName;

  private String role;

  private String status;

  private String profileImageUrl;

  private Integer totalScore;

  private LocalDate birthDate;

  private String gender;

  private String mbti;

  private Integer totalRegressionCount;

  private String gameProgress;

  // 리프레시 토큰은 refresh_token 테이블이 정본이며 회원 응답에 노출할 이유가 없어 제거함
  public static MemberDto from(Member member){
    return MemberDto.builder()
        .id(member.getId())
        .email(member.getEmail())
        .characterName(member.getCharacterName())
        .role(String.valueOf(member.getRole()))
        .status(String.valueOf(member.getStatus()))
        .profileImageUrl(member.getProfileImageUrl())
        .totalScore(member.getTotalScore() != null ? member.getTotalScore() : 0)
        .birthDate(member.getBirthDate())
        .gender(member.getGender())
        .mbti(member.getMbti())
        .totalRegressionCount(member.getTotalRegressionCount() != null ? member.getTotalRegressionCount() : 0)
        .gameProgress(member.getGameProgress())
        .build();
  }
}

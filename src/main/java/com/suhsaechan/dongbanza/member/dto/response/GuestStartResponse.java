package com.suhsaechan.dongbanza.member.dto.response;

import com.suhsaechan.dongbanza.member.domain.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GuestStartResponse {
  private Long id;
  private String characterName;
  private String mbti;
  private String accessToken; // 헤더에 포함된다
  private String refreshToken; // body에 포함시킨다

  public static GuestStartResponse from(Member member, String accessToken, String refreshToken) {
    return GuestStartResponse.builder()
        .id(member.getId())
        .characterName(member.getCharacterName())
        .mbti(member.getMbti())
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  // 액세스 토큰은 Authorization 헤더로만 내려가므로 body에서는 제외
  public GuestStartResponse exceptAccessToken() {
    return GuestStartResponse.builder()
        .id(id)
        .characterName(characterName)
        .mbti(mbti)
        .refreshToken(refreshToken)
        .build();
  }
}

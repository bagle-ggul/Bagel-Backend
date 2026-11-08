package com.suhsaechan.dongbanza.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게스트 시작 요청: 닉네임(선택), MBTI(필수)
 * 닉네임 생략 시 서버가 자동 생성
 */
@Getter
@NoArgsConstructor
public class GuestStartForm {

  public static final int CHARACTER_NAME_MIN_LENGTH = 2;
  public static final int CHARACTER_NAME_MAX_LENGTH = 12;

  // 한글·영문·숫자·밑줄만 허용 (랭킹 화면 노출값이라 공백·특수문자 제외)
  @Size(min = CHARACTER_NAME_MIN_LENGTH, max = CHARACTER_NAME_MAX_LENGTH,
      message = "닉네임은 2~12자여야 합니다.")
  @Pattern(regexp = "^[가-힣a-zA-Z0-9_]+$",
      message = "닉네임은 한글, 영문, 숫자, 밑줄만 사용할 수 있습니다.")
  private String characterName;

  // 4글자 대문자 조합 (예: ENFP)
  @NotBlank(message = "MBTI는 필수입니다.")
  @Pattern(regexp = "^[EI][SN][TF][JP]$", message = "MBTI 형식이 올바르지 않습니다.")
  private String mbti;

  public GuestStartForm(String characterName, String mbti) {
    this.characterName = characterName;
    this.mbti = mbti;
  }
}

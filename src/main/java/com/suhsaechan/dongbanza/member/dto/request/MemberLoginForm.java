package com.suhsaechan.dongbanza.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberLoginForm {
  // 로그인도 동일하게 필수값 누락이 500으로 새지 않도록 방어한다
  @NotBlank(message = "이메일은 필수입니다.")
  private String email;

  @NotBlank(message = "비밀번호는 필수입니다.")
  private String password;
}
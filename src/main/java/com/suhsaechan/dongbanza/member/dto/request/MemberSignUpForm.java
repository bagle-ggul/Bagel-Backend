package com.suhsaechan.dongbanza.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class MemberSignUpForm {
  // email·password는 가입 처리에서 반드시 역참조하므로 누락 시 400으로 걸러낸다
  // (미검증 시 passwordEncoder.encode(null)이 터져 500으로 나감)
  @NotBlank(message = "이메일은 필수입니다.")
  @Email(message = "이메일 형식이 올바르지 않습니다.")
  private String email;

  @NotBlank(message = "비밀번호는 필수입니다.")
  private String password;

  private String characterName;

  private String mbti;

  private LocalDate birthDate;

  private String gender;
}

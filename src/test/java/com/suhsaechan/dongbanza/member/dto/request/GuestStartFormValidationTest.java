package com.suhsaechan.dongbanza.member.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** 게스트 시작 요청의 입력 검증이 500이 아닌 400으로 걸러지는지 검증한다. */
class GuestStartFormValidationTest {

  private static Validator validator;

  @BeforeAll
  static void setUp() {
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      validator = factory.getValidator();
    }
  }

  private static Set<String> violatedFields(GuestStartForm form) {
    Set<ConstraintViolation<GuestStartForm>> violations = validator.validate(form);
    return violations.stream()
        .map(violation -> violation.getPropertyPath().toString())
        .collect(Collectors.toSet());
  }

  @Test
  @DisplayName("닉네임을 생략해도 MBTI만 있으면 통과한다")
  void allowsOmittedNickname() {
    assertThat(violatedFields(new GuestStartForm(null, "ENFP"))).isEmpty();
  }

  @Test
  @DisplayName("MBTI가 없으면 검증 실패")
  void rejectsMissingMbti() {
    assertThat(violatedFields(new GuestStartForm("베이글", null))).containsExactly("mbti");
  }

  @ParameterizedTest
  @ValueSource(strings = {"enfp", "ABCD", "ENF", "ENFPX", "EEFP", " ENFP"})
  @DisplayName("MBTI 형식이 아니면 검증 실패")
  void rejectsMalformedMbti(String mbti) {
    assertThat(violatedFields(new GuestStartForm("베이글", mbti))).containsExactly("mbti");
  }

  @ParameterizedTest
  @ValueSource(strings = {"A", "열세글자를넘는아주긴닉네임입니다", "<script>", "공 백", "   "})
  @DisplayName("닉네임 길이·문자 규칙을 어기면 검증 실패")
  void rejectsInvalidNickname(String nickname) {
    assertThat(violatedFields(new GuestStartForm(nickname, "ENFP"))).contains("characterName");
  }

  @ParameterizedTest
  @ValueSource(strings = {"베이글", "bagel_01", "고소한베이글482"})
  @DisplayName("한글·영문·숫자·밑줄 닉네임은 통과한다")
  void allowsValidNickname(String nickname) {
    assertThat(violatedFields(new GuestStartForm(nickname, "ENFP"))).isEmpty();
  }
}

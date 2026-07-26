package com.suhsaechan.dongbanza.member.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.lang.reflect.Field;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 필수값 누락이 500이 아닌 400으로 걸러지는지 검증한다.
 *
 * <p>과거 email/password 검증이 없어 서비스 계층에서 예외가 터지며 서버 오류로 응답했다.
 */
class MemberFormValidationTest {

  private static Validator validator;

  @BeforeAll
  static void setUp() {
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      validator = factory.getValidator();
    }
  }

  /** DTO에 세터가 없어 리플렉션으로 값을 주입한다. */
  private static void set(Object target, String field, Object value) throws Exception {
    Field f = target.getClass().getDeclaredField(field);
    f.setAccessible(true);
    f.set(target, value);
  }

  private static Set<String> violatedFields(Object form) {
    Set<ConstraintViolation<Object>> violations = validator.validate(form);
    return violations.stream()
        .map(v -> v.getPropertyPath().toString())
        .collect(java.util.stream.Collectors.toSet());
  }

  @Test
  @DisplayName("회원가입: email·password 누락 시 두 필드 모두 검증 실패")
  void signUpRejectsMissingRequiredFields() {
    MemberSignUpForm form = new MemberSignUpForm();

    assertThat(violatedFields(form)).containsExactlyInAnyOrder("email", "password");
  }

  @Test
  @DisplayName("회원가입: 이메일 형식이 아니면 검증 실패")
  void signUpRejectsMalformedEmail() throws Exception {
    MemberSignUpForm form = new MemberSignUpForm();
    set(form, "email", "not-an-email");
    set(form, "password", "Test1234!");

    assertThat(violatedFields(form)).containsExactly("email");
  }

  @Test
  @DisplayName("회원가입: 선택 필드를 생략해도 검증을 통과한다 (기존 동작 회귀 방지)")
  void signUpAllowsOptionalFieldsToBeOmitted() throws Exception {
    MemberSignUpForm form = new MemberSignUpForm();
    set(form, "email", "user@example.com");
    set(form, "password", "Test1234!");

    assertThat(violatedFields(form)).isEmpty();
  }

  @Test
  @DisplayName("로그인: email·password 누락 시 검증 실패")
  void loginRejectsMissingRequiredFields() {
    MemberLoginForm form = new MemberLoginForm();

    assertThat(violatedFields(form)).containsExactlyInAnyOrder("email", "password");
  }

  @Test
  @DisplayName("로그인: 값이 공백만 있어도 검증 실패")
  void loginRejectsBlankValues() {
    MemberLoginForm form = new MemberLoginForm();
    form.setEmail("   ");
    form.setPassword("   ");

    assertThat(violatedFields(form)).containsExactlyInAnyOrder("email", "password");
  }
}

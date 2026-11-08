package com.suhsaechan.dongbanza.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.suhsaechan.dongbanza.member.dto.request.GuestStartForm;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NicknameGeneratorTest {

  private final NicknameGenerator generator = new NicknameGenerator();

  @Test
  @DisplayName("같은 시드면 같은 닉네임이 나온다")
  void isDeterministicForSameSeed() {
    assertThat(generator.generate(new Random(7))).isEqualTo(generator.generate(new Random(7)));
  }

  @Test
  @DisplayName("생성한 닉네임은 항상 입력 검증 규칙(2~12자, 한글·영문·숫자)을 통과한다")
  void alwaysSatisfiesNicknameRules() {
    Random random = new Random(1);
    for (int count = 0; count < 5000; count++) {
      String nickname = generator.generate(random);

      assertThat(nickname.length())
          .isBetween(GuestStartForm.CHARACTER_NAME_MIN_LENGTH, GuestStartForm.CHARACTER_NAME_MAX_LENGTH);
      assertThat(nickname).matches("^[가-힣a-zA-Z0-9_]+$");
    }
  }

  @Test
  @DisplayName("여러 번 생성하면 서로 다른 닉네임이 섞여 나온다")
  void producesVariety() {
    Random random = new Random(3);
    long distinctCount = java.util.stream.IntStream.range(0, 200)
        .mapToObj(index -> generator.generate(random))
        .distinct()
        .count();

    assertThat(distinctCount).isGreaterThan(150);
  }
}

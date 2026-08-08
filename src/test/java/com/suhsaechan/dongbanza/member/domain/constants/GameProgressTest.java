package com.suhsaechan.dongbanza.member.domain.constants;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * enum 전환 후에도 기존 DB 데이터와 호환되는지 고정한다.
 *
 * <p>@Enumerated(EnumType.STRING)은 상수명을 그대로 저장·조회하므로, 상수명이 바뀌면
 * 운영 DB의 기존 varchar 값을 읽는 순간 예외가 발생한다. 이름 변경을 실수로 하지 못하게 막는다.
 */
class GameProgressTest {

  @Test
  @DisplayName("운영 DB에 저장된 값이 그대로 매핑된다")
  void mapsExistingDatabaseValues() {
    assertThat(GameProgress.valueOf("NOT_STARTED")).isEqualTo(GameProgress.NOT_STARTED);
    assertThat(GameProgress.valueOf("GAME_END")).isEqualTo(GameProgress.GAME_END);
  }

  @Test
  @DisplayName("상수명이 곧 저장값이므로 name()이 기존 문자열과 일치한다")
  void nameMatchesStoredValue() {
    assertThat(GameProgress.NOT_STARTED.name()).isEqualTo("NOT_STARTED");
    assertThat(GameProgress.GAME_END.name()).isEqualTo("GAME_END");
  }

  @Test
  @DisplayName("정의된 상태는 두 가지뿐이다 (임의 추가 시 DB 호환성 재검토 필요)")
  void hasOnlyKnownStates() {
    assertThat(GameProgress.values())
        .containsExactly(GameProgress.NOT_STARTED, GameProgress.GAME_END);
  }
}

package com.suhsaechan.dongbanza.member.domain.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 회원의 게임 진행 상태.
 *
 * <p>기존에는 "NOT_STARTED" / "GAME_END" 매직 문자열을 그대로 저장해 오타를 컴파일 시점에
 * 잡을 수 없었다. 상수명이 곧 DB 저장값이므로 기존 데이터와 호환된다.
 */
@Getter
@RequiredArgsConstructor
public enum GameProgress {

  NOT_STARTED("게임 시작 전"),
  GAME_END("게임 종료");

  private final String description;
}

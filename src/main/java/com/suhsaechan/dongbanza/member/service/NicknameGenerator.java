package com.suhsaechan.dongbanza.member.service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Component;

/**
 * 게스트 닉네임 생성: 형용사 + 명사 + 숫자 3자리 (예: 고소한베이글482)
 * 최대 길이 11자 (GuestStartForm 닉네임 상한 12자 이내)
 */
@Component
public class NicknameGenerator {

  private static final List<String> ADJECTIVES = List.of(
      "졸린", "말랑한", "쫀득한", "고소한", "따끈한", "바삭한", "달콤한", "엉뚱한", "수줍은", "용감한",
      "느긋한", "반짝이는", "포근한", "씩씩한", "발랄한", "차분한"
  );

  private static final List<String> NOUNS = List.of(
      "베이글", "크림치즈", "곰돌이", "고양이", "토끼", "여우", "펭귄", "라떼", "쿠키", "구름",
      "별빛", "파도", "다람쥐", "참새", "마카롱", "푸딩"
  );

  private static final int NUMBER_MIN = 100;
  private static final int NUMBER_BOUND_EXCLUSIVE = 1000;

  public String generate() {
    return generate(ThreadLocalRandom.current());
  }

  // 테스트에서 시드를 고정할 수 있도록 Random 주입 지원
  String generate(Random random) {
    String adjective = ADJECTIVES.get(random.nextInt(ADJECTIVES.size()));
    String noun = NOUNS.get(random.nextInt(NOUNS.size()));
    int number = NUMBER_MIN + random.nextInt(NUMBER_BOUND_EXCLUSIVE - NUMBER_MIN);
    return adjective + noun + number;
  }
}

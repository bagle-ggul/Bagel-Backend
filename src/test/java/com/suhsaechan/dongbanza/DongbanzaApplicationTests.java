package com.suhsaechan.dongbanza;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// 운영 설정은 Actions 시크릿으로 주입되어 CI에 존재하지 않으므로 인메모리 DB 기반 test 프로파일로 구동한다
@SpringBootTest
@ActiveProfiles("test")
class DongbanzaApplicationTests {

  @Test
  void contextLoads() {
  }

}

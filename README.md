# Bagel-Backend

비주얼 노벨 웹 게임 **Re: WAVE**의 API 서버입니다.
가입·로그인 없이 **닉네임과 MBTI만으로 바로 시작**하고, 게임 결과와 전체 랭킹을 저장합니다.

<!-- 수정하지마세요 자동으로 동기화 됩니다 -->
## 최신 버전 : v1.0.18 (2026-11-09)

[전체 버전 기록](CHANGELOG.md)

- 게임: https://bagel.suhsaechan.kr
- API 문서 (Swagger): https://api.bagel.suhsaechan.kr/docs/swagger
- 프론트엔드 레포: [bagle-ggul/Bagel-Frontend](https://github.com/bagle-ggul/Bagel-Frontend)

## 이런 서버입니다

| 하는 일 | 방식 |
|---|---|
| 게임 시작 | `POST /api/guest/start`에 MBTI(필수)와 닉네임(선택)만 보내면 플레이 계정과 토큰 발급 |
| 닉네임 추천 | 닉네임을 비우면 `고소한베이글482` 형태로 자동 생성. 중복 허용 |
| 개인정보 | 이메일·비밀번호·생년월일·성별을 받지 않음. 저장하는 값은 닉네임과 MBTI뿐 |
| 랭킹 | 토큰 없이 누구나 조회 (점수 내림차순, 페이지 단위) |

## API

| 메서드 | 경로 | 인증 | 설명 |
|---|---|---|---|
| GET | `/api/guest/nickname` | 없음 | 닉네임 추천 |
| POST | `/api/guest/start` | 없음 | 게스트 시작. 액세스 토큰은 `Authorization` 헤더, 리프레시 토큰은 body |
| POST | `/api/token` | 없음 | 리프레시 토큰으로 액세스 토큰 재발급 |
| GET | `/api/game/ranking?page=0&size=10` | 없음 | 전체 랭킹 |
| GET | `/api/my-page` | 필요 | 내 정보 (닉네임, MBTI, 점수, 진행 상태) |
| POST | `/api/game/over` | 필요 | 게임 결과 저장 |
| GET | `/api/game/my-results` | 필요 | 내 게임 결과 목록 |
| DELETE | `/api/game/my-results/{gameResultId}` | 필요 | 내 게임 결과 삭제 |

```bash
# 게스트로 시작 (닉네임 생략 시 자동 생성)
curl -i -X POST https://api.bagel.suhsaechan.kr/api/guest/start \
  -H 'Content-Type: application/json' \
  -d '{"mbti":"ENFP"}'
```

## 로컬에서 실행

필요한 것: JDK 17, PostgreSQL

```bash
# 테스트 (H2 인메모리 DB 사용, 외부 의존 없음)
./gradlew test

# 서버 실행: src/main/resources/application-local.yml 작성 후
./gradlew bootRun --args='--spring.profiles.active=local'
```

`application-local.yml`에 필요한 값:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bagel
    username: {DB_USERNAME}
    password: {DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update
  jwt:
    secret-key: {BASE64_인코딩된_64바이트_이상_키}
    access-expiration-time: 3600000      # 1시간 (ms)
    refresh-expiration-time: 1209600000  # 14일 (ms)
```

운영 설정(`application-prod.yml`)은 GitHub Actions 시크릿으로 주입되며 레포에 없습니다.

## 기술 스택

Java 17 · Spring Boot 3.4 · Spring Security + JWT (jjwt 0.12) · Spring Data JPA · PostgreSQL · Springdoc OpenAPI · Prometheus

## 구조

```
src/main/java/com/suhsaechan/dongbanza/
├── member/   게스트 시작, 닉네임 생성, 내 정보
├── game/     게임 결과 저장, 랭킹
└── common/   보안 설정, JWT, 예외 처리, API 문서
docs/db/      운영 DB 수동 마이그레이션 SQL
```

## 배포

`main` 브랜치에 푸시되면 GitHub Actions가 테스트 > Docker 이미지 빌드 > 서버 배포까지 자동으로 진행합니다.

## 라이선스

[MIT](LICENSE)

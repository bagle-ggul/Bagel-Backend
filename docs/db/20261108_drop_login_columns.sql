-- 게스트 시작 전환: 가입·로그인·탈퇴·휴면 개념 제거에 따른 기존 DB 정리 (PostgreSQL)
-- 자동 적용되지 않음. 운영 DB에는 수동 실행 (재실행해도 안전)
--
-- 실행 순서
--   1. 새 백엔드 먼저 배포 (엔티티에서 빠진 컬럼은 모두 nullable이라 구 컬럼이 남아 있어도 동작)
--   2. pg_dump 백업
--   3. 이 스크립트 실행
--
-- 되돌리기: 백업 복원 (컬럼 제거는 되돌릴 수 없음)

BEGIN;

-- 1단계: 영향 범위 확인 (읽기 전용)
SELECT count(*) AS total_members FROM member;

-- 2단계: 가입만 하고 한 판도 안 한 구 계정 정리
-- 대상: 이메일 있음(구 가입 계정) + 플레이 기록 없음. 로그인 제거로 다시 접근할 방법이 없는 계정
-- 이메일 없는 계정은 게스트 시작으로 만든 진행 중 계정이라 제외
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns
             WHERE table_name = 'member' AND column_name = 'email') THEN
    DELETE FROM refresh_token
    WHERE member_id IN (
      SELECT m.id FROM member m
      WHERE m.email IS NOT NULL
        AND NOT EXISTS (SELECT 1 FROM game_result g WHERE g.member_id = m.id)
    );
    DELETE FROM member m
    WHERE m.email IS NOT NULL
      AND NOT EXISTS (SELECT 1 FROM game_result g WHERE g.member_id = m.id);
  END IF;
END $$;

-- 3단계: 가입·개인정보·회원 상태 컬럼 제거 (email 유니크 인덱스는 컬럼과 함께 제거됨)
-- refresh_token 컬럼: refresh_token 테이블로 옮긴 뒤 남은 구 컬럼
-- status 컬럼: 탈퇴·휴면 개념 제거 (MemberStatus 삭제)
ALTER TABLE member DROP COLUMN IF EXISTS email;
ALTER TABLE member DROP COLUMN IF EXISTS password;
ALTER TABLE member DROP COLUMN IF EXISTS birth_date;
ALTER TABLE member DROP COLUMN IF EXISTS gender;
ALTER TABLE member DROP COLUMN IF EXISTS profile_image_url;
ALTER TABLE member DROP COLUMN IF EXISTS refresh_token;
ALTER TABLE member DROP COLUMN IF EXISTS status;

-- 4단계: 정리 결과 확인
SELECT count(*) AS remaining_members FROM member;

COMMIT;

-- 게스트 시작 전환: 가입·로그인 제거에 따른 기존 DB 정리 (PostgreSQL)
-- 자동 적용되지 않음. 운영 DB에는 수동 실행
--
-- 실행 순서
--   1. 새 백엔드 먼저 배포 (엔티티에서 컬럼이 빠져도 기존 컬럼이 nullable이라 동작함)
--   2. pg_dump 백업
--   3. 이 스크립트 실행 (1단계 확인 쿼리 결과를 보고 2단계 이후 진행)
--
-- 되돌리기: 백업 복원 (컬럼 제거는 되돌릴 수 없음)

BEGIN;

-- 1단계: 영향 범위 확인 (읽기 전용)
SELECT count(*) AS total_members FROM member;
SELECT count(*) AS members_without_play_record
FROM member m
WHERE NOT EXISTS (SELECT 1 FROM game_result g WHERE g.member_id = m.id);

-- 2단계: 플레이 기록 없는 기존 계정 정리 (가입만 하고 한 판도 안 한 계정, 랭킹 영향 없음)
-- 플레이 기록이 있는 계정은 랭킹 보존을 위해 유지
DELETE FROM refresh_token
WHERE member_id IN (
  SELECT m.id FROM member m
  WHERE NOT EXISTS (SELECT 1 FROM game_result g WHERE g.member_id = m.id)
);

DELETE FROM member m
WHERE NOT EXISTS (SELECT 1 FROM game_result g WHERE g.member_id = m.id);

-- 3단계: 로그인·개인정보용 컬럼 제거
ALTER TABLE member DROP COLUMN IF EXISTS email;
ALTER TABLE member DROP COLUMN IF EXISTS password;
ALTER TABLE member DROP COLUMN IF EXISTS birth_date;
ALTER TABLE member DROP COLUMN IF EXISTS gender;
ALTER TABLE member DROP COLUMN IF EXISTS profile_image_url;

-- 4단계: 정리 결과 확인 후 COMMIT (이상하면 ROLLBACK)
SELECT count(*) AS remaining_members FROM member;

COMMIT;

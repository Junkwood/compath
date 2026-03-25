------------------------------------------------------------
-- 필요 시 기존 데이터 삭제
------------------------------------------------------------
-- DELETE FROM tasks;
-- DELETE FROM milestone_mapping;
-- DELETE FROM milestones;
-- DELETE FROM projects;
-- DELETE FROM task_types;
-- DELETE FROM task_statuses;


------------------------------------------------------------
-- 1. 업무유형 / 업무상태 마스터
-- tasks FK 때문에 먼저 생성
------------------------------------------------------------

INSERT INTO task_types (
    task_type_id, type_name, description, is_active, created_at, updated_at, created_by, editor_user_id
) VALUES (
    1, '기획', '기획 업무', 'O1', SYSTIMESTAMP, SYSTIMESTAMP, NULL, NULL
);

INSERT INTO task_types (
    task_type_id, type_name, description, is_active, created_at, updated_at, created_by, editor_user_id
) VALUES (
    2, '개발', '개발 업무', 'O1', SYSTIMESTAMP, SYSTIMESTAMP, NULL, NULL
);

INSERT INTO task_statuses (
    task_status_id, status_name, description, is_active, created_at, updated_at, created_by, editor_user_id
) VALUES (
    1, '시작 전', '업무 시작 전', 'O1', SYSTIMESTAMP, SYSTIMESTAMP, NULL, NULL
);

INSERT INTO task_statuses (
    task_status_id, status_name, description, is_active, created_at, updated_at, created_by, editor_user_id
) VALUES (
    2, '진행중', '업무 진행중', 'O1', SYSTIMESTAMP, SYSTIMESTAMP, NULL, NULL
);

INSERT INTO task_statuses (
    task_status_id, status_name, description, is_active, created_at, updated_at, created_by, editor_user_id
) VALUES (
    3, '완료', '업무 완료', 'O1', SYSTIMESTAMP, SYSTIMESTAMP, NULL, NULL
);


------------------------------------------------------------
-- 2. 큰 프로젝트 2개
------------------------------------------------------------
INSERT INTO projects (
    project_id, parent_project_id, project_name, description,
    start_date, end_date, status, is_public,
    created_at, updated_at, created_by, pm_user_id, pl_user_id, editor_user_id
) VALUES (
    'PJT-001', NULL, 'COMPATH 통합 플랫폼 구축', '최상위 프로젝트 1',
    DATE '2026-04-01', DATE '2026-12-31', 'F1', 'P1',
    SYSTIMESTAMP, SYSTIMESTAMP, NULL, NULL, NULL, NULL
);

INSERT INTO projects (
    project_id, parent_project_id, project_name, description,
    start_date, end_date, status, is_public,
    created_at, updated_at, created_by, pm_user_id, pl_user_id, editor_user_id
) VALUES (
    'PJT-002', NULL, '삼성라이온즈 베리즈몰 구축', '최상위 프로젝트 2',
    DATE '2026-04-01', DATE '2026-12-31', 'F1', 'P1',
    SYSTIMESTAMP, SYSTIMESTAMP, NULL, NULL, NULL, NULL
);

SELECT * FROM MILESTONES;
------------------------------------------------------------
-- 3. 마일스톤 6개
------------------------------------------------------------
INSERT INTO milestones (
    milestone_id, milestone_name, description,
    start_date, due_date, status, created_at, updated_at, created_by
) VALUES (
    101, '요구사항 정의', 'PJT-001 마일스톤 1',
    DATE '2026-03-25', DATE '2026-04-24', 'E1',
    SYSTIMESTAMP, SYSTIMESTAMP, NULL
);

INSERT INTO milestones (
    milestone_id, milestone_name, description,
    start_date, due_date, status, created_at, updated_at, created_by
) VALUES (
    102, '설계', 'PJT-001 마일스톤 2',
    DATE '2026-03-25', DATE '2026-04-24', 'E1',
    SYSTIMESTAMP, SYSTIMESTAMP, NULL
);

INSERT INTO milestones (
    milestone_id, milestone_name, description,
    start_date, due_date, status, created_at, updated_at, created_by
) VALUES (
    103, '개발', 'PJT-001 마일스톤 3',
    DATE '2026-03-25', DATE '2026-04-24', 'E1',
    SYSTIMESTAMP, SYSTIMESTAMP, NULL
);

INSERT INTO milestones (
    milestone_id, milestone_name, description,
    start_date, due_date, status, created_at, updated_at, created_by
) VALUES (
    201, '요구사항 분석', 'PJT-002 마일스톤 1',
    DATE '2026-03-25', DATE '2026-04-24', 'E1',
    SYSTIMESTAMP, SYSTIMESTAMP, NULL
);

INSERT INTO milestones (
    milestone_id, milestone_name, description,
    start_date, due_date, status, created_at, updated_at, created_by
) VALUES (
    202, '설계', 'PJT-002 마일스톤 2',
    DATE '2026-03-25', DATE '2026-04-24', 'E1',
    SYSTIMESTAMP, SYSTIMESTAMP, NULL
);

INSERT INTO milestones (
    milestone_id, milestone_name, description,
    start_date, due_date, status, created_at, updated_at, created_by
) VALUES (
    203, '개발 및 오픈 준비', 'PJT-002 마일스톤 3',
    DATE '2026-03-25', DATE '2026-04-24', 'E1',
    SYSTIMESTAMP, SYSTIMESTAMP, NULL
);

SELECT * FROM projects;

------------------------------------------------------------
-- 4. 하위 프로젝트 18개
-- 날짜 통일: 2026-03-25 ~ 2026-04-24
------------------------------------------------------------

-- PJT-001 > MS 101

INSERT INTO projects VALUES (
    'PJT-001-001', 'PJT-001', '요구사항-공통관리', '하위 프로젝트',
    DATE '2026-03-25', DATE '2026-04-24', 'F1', 'P2',
    SYSTIMESTAMP, SYSTIMESTAMP, NULL, NULL, NULL, NULL
);

INSERT INTO projects VALUES (
    'PJT-001-002', 'PJT-001', '요구사항-업무관리', '하위 프로젝트',
    DATE '2026-03-25', DATE '2026-04-24', 'F1', 'P2',
    SYSTIMESTAMP, SYSTIMESTAMP, NULL, NULL, NULL, NULL
);

INSERT INTO projects VALUES (
    'PJT-001-003', 'PJT-001', '요구사항-문서관리', '하위 프로젝트',
    DATE '2026-03-25', DATE '2026-04-24', 'F1', 'P2',
    SYSTIMESTAMP, SYSTIMESTAMP, NULL, NULL, NULL, NULL
);

-- PJT-001 > MS 102
INSERT INTO projects VALUES (
    'PJT-001-004', 'PJT-001', '설계-권한설계', '하위 프로젝트',
    DATE '2026-03-25', DATE '2026-04-24', 'F1', 'P2',
    SYSTIMESTAMP, SYSTIMESTAMP, NULL, NULL, NULL, NULL
);

INSERT INTO projects VALUES (
    'PJT-001-005', 'PJT-001', '설계-DB설계', '하위 프로젝트',
    DATE '2026-03-25', DATE '2026-04-24', 'F1', 'P2',
    SYSTIMESTAMP, SYSTIMESTAMP, NULL, NULL, NULL, NULL
);

INSERT INTO projects VALUES (
    'PJT-001-006', 'PJT-001', '설계-화면설계', '하위 프로젝트',
    DATE '2026-03-25', DATE '2026-04-24', 'F1', 'P2',
    SYSTIMESTAMP, SYSTIMESTAMP, NULL, NULL, NULL, NULL
);

-- PJT-001 > MS 103
INSERT INTO projects VALUES (
    'PJT-001-007', 'PJT-001', '개발-프로젝트모듈', '하위 프로젝트',
    DATE '2026-03-25', DATE '2026-04-24', 'F1', 'P2',
    SYSTIMESTAMP, SYSTIMESTAMP, NULL, NULL, NULL, NULL
);

INSERT INTO projects VALUES (
    'PJT-001-008', 'PJT-001', '개발-업무모듈', '하위 프로젝트',
    DATE '2026-03-25', DATE '2026-04-24', 'F1', 'P2',
    SYSTIMESTAMP, SYSTIMESTAMP, NULL, NULL, NULL, NULL
);

INSERT INTO projects VALUES (
    'PJT-001-009', 'PJT-001', '개발-문서모듈', '하위 프로젝트',
    DATE '2026-03-25', DATE '2026-04-24', 'F1', 'P2',
    SYSTIMESTAMP, SYSTIMESTAMP, NULL, NULL, NULL, NULL
);

-- PJT-002 > MS 201
INSERT INTO projects VALUES (
    'PJT-002-001', 'PJT-002', '요구사항-상품관리', '하위 프로젝트',
    DATE '2026-03-25', DATE '2026-04-24', 'F1', 'P2',
    SYSTIMESTAMP, SYSTIMESTAMP, NULL, NULL, NULL, NULL
);

INSERT INTO projects VALUES (
    'PJT-002-002', 'PJT-002', '요구사항-주문결제', '하위 프로젝트',
    DATE '2026-03-25', DATE '2026-04-24', 'F1', 'P2',
    SYSTIMESTAMP, SYSTIMESTAMP, NULL, NULL, NULL, NULL
);

INSERT INTO projects VALUES (
    'PJT-002-003', 'PJT-002', '요구사항-회원혜택', '하위 프로젝트',
    DATE '2026-03-25', DATE '2026-04-24', 'F1', 'P2',
    SYSTIMESTAMP, SYSTIMESTAMP, NULL, NULL, NULL, NULL
);

-- PJT-002 > MS 202
INSERT INTO projects VALUES (
    'PJT-002-004', 'PJT-002', '설계-상품상세화면', '하위 프로젝트',
    DATE '2026-03-25', DATE '2026-04-24', 'F1', 'P2',
    SYSTIMESTAMP, SYSTIMESTAMP, NULL, NULL, NULL, NULL
);

INSERT INTO projects VALUES (
    'PJT-002-005', 'PJT-002', '설계-장바구니결제', '하위 프로젝트',
    DATE '2026-03-25', DATE '2026-04-24', 'F1', 'P2',
    SYSTIMESTAMP, SYSTIMESTAMP, NULL, NULL, NULL, NULL
);

INSERT INTO projects VALUES (
    'PJT-002-006', 'PJT-002', '설계-이벤트혜택', '하위 프로젝트',
    DATE '2026-03-25', DATE '2026-04-24', 'F1', 'P2',
    SYSTIMESTAMP, SYSTIMESTAMP, NULL, NULL, NULL, NULL
);

-- PJT-002 > MS 203
INSERT INTO projects VALUES (
    'PJT-002-007', 'PJT-002', '개발-상품모듈', '하위 프로젝트',
    DATE '2026-03-25', DATE '2026-04-24', 'F1', 'P2',
    SYSTIMESTAMP, SYSTIMESTAMP, NULL, NULL, NULL, NULL
);

INSERT INTO projects VALUES (
    'PJT-002-008', 'PJT-002', '개발-주문결제모듈', '하위 프로젝트',
    DATE '2026-03-25', DATE '2026-04-24', 'F1', 'P2',
    SYSTIMESTAMP, SYSTIMESTAMP, NULL, NULL, NULL, NULL
);

INSERT INTO projects VALUES (
    'PJT-002-009', 'PJT-002', '개발-오픈준비', '하위 프로젝트',
    DATE '2026-03-25', DATE '2026-04-24', 'F1', 'P2',
    SYSTIMESTAMP, SYSTIMESTAMP, NULL, NULL, NULL, NULL
);

SELECT * FROM MILESTONE_MAPPING;
------------------------------------------------------------
-- 5. 마일스톤 - 하위프로젝트 매핑
------------------------------------------------------------
INSERT INTO milestone_mapping (milestone_mapping_id, milestone_id, project_id, relation) VALUES (1001, 101, 'PJT-001-001', NULL);
INSERT INTO milestone_mapping (milestone_mapping_id, milestone_id, project_id, relation) VALUES (1002, 101, 'PJT-001-002', NULL);
INSERT INTO milestone_mapping (milestone_mapping_id, milestone_id, project_id, relation) VALUES (1003, 101, 'PJT-001-003', NULL);

INSERT INTO milestone_mapping (milestone_mapping_id, milestone_id, project_id, relation) VALUES (1004, 102, 'PJT-001-004', NULL);
INSERT INTO milestone_mapping (milestone_mapping_id, milestone_id, project_id, relation) VALUES (1005, 102, 'PJT-001-005', NULL);
INSERT INTO milestone_mapping (milestone_mapping_id, milestone_id, project_id, relation) VALUES (1006, 102, 'PJT-001-006', NULL);

INSERT INTO milestone_mapping (milestone_mapping_id, milestone_id, project_id, relation) VALUES (1007, 103, 'PJT-001-007', NULL);
INSERT INTO milestone_mapping (milestone_mapping_id, milestone_id, project_id, relation) VALUES (1008, 103, 'PJT-001-008', NULL);
INSERT INTO milestone_mapping (milestone_mapping_id, milestone_id, project_id, relation) VALUES (1009, 103, 'PJT-001-009', NULL);

INSERT INTO milestone_mapping (milestone_mapping_id, milestone_id, project_id, relation) VALUES (1010, 201, 'PJT-002-001', NULL);
INSERT INTO milestone_mapping (milestone_mapping_id, milestone_id, project_id, relation) VALUES (1011, 201, 'PJT-002-002', NULL);
INSERT INTO milestone_mapping (milestone_mapping_id, milestone_id, project_id, relation) VALUES (1012, 201, 'PJT-002-003', NULL);

INSERT INTO milestone_mapping (milestone_mapping_id, milestone_id, project_id, relation) VALUES (1013, 202, 'PJT-002-004', NULL);
INSERT INTO milestone_mapping (milestone_mapping_id, milestone_id, project_id, relation) VALUES (1014, 202, 'PJT-002-005', NULL);
INSERT INTO milestone_mapping (milestone_mapping_id, milestone_id, project_id, relation) VALUES (1015, 202, 'PJT-002-006', NULL);

INSERT INTO milestone_mapping (milestone_mapping_id, milestone_id, project_id, relation) VALUES (1016, 203, 'PJT-002-007', NULL);
INSERT INTO milestone_mapping (milestone_mapping_id, milestone_id, project_id, relation) VALUES (1017, 203, 'PJT-002-008', NULL);
INSERT INTO milestone_mapping (milestone_mapping_id, milestone_id, project_id, relation) VALUES (1018, 203, 'PJT-002-009', NULL);


------------------------------------------------------------
-- 6. 업무 36개
-- 날짜 통일: 2026-03-25 ~ 2026-04-24
------------------------------------------------------------

-- PJT-001-001
INSERT INTO tasks VALUES (
    10001, NULL, 'PJT-001-001', 101, 1, 1,
    '공통관리 요구사항 분석', '공통관리 요구사항 정리',
    NULL, 'H2', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    8, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

INSERT INTO tasks VALUES (
    10002, NULL, 'PJT-001-001', 101, 2, 1,
    '공통관리 화면 정의', '공통관리 화면 정의서 작성',
    NULL, 'H3', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    12, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

-- PJT-001-002
INSERT INTO tasks VALUES (
    10003, NULL, 'PJT-001-002', 101, 1, 1,
    '업무관리 요구사항 분석', '업무관리 요구사항 정리',
    NULL, 'H2', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    10, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

INSERT INTO tasks VALUES (
    10004, NULL, 'PJT-001-002', 101, 2, 1,
    '업무관리 정책 정리', '업무 상태/우선순위 정책 정리',
    NULL, 'H3', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    10, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

-- PJT-001-003
INSERT INTO tasks VALUES (
    10005, NULL, 'PJT-001-003', 101, 1, 1,
    '문서관리 요구사항 분석', '문서관리 요구사항 정리',
    NULL, 'H2', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    8, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

INSERT INTO tasks VALUES (
    10006, NULL, 'PJT-001-003', 101, 2, 1,
    '문서 분류체계 정의', '카테고리/댓글정책 정의',
    NULL, 'H3', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    8, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

-- PJT-001-004
INSERT INTO tasks VALUES (
    10007, NULL, 'PJT-001-004', 102, 1, 1,
    '권한 매트릭스 설계', '역할별 권한 설계',
    NULL, 'H1', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    14, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

INSERT INTO tasks VALUES (
    10008, NULL, 'PJT-001-004', 102, 2, 1,
    '권한 API 설계', '권한 체크 API 정의',
    NULL, 'H2', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    10, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

-- PJT-001-005
INSERT INTO tasks VALUES (
    10009, NULL, 'PJT-001-005', 102, 1, 1,
    '논리 ERD 작성', 'DB 논리모델 작성',
    NULL, 'H1', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    16, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

INSERT INTO tasks VALUES (
    10010, NULL, 'PJT-001-005', 102, 2, 1,
    '물리 테이블 설계', '테이블/인덱스 설계',
    NULL, 'H1', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    20, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

-- PJT-001-006
INSERT INTO tasks VALUES (
    10011, NULL, 'PJT-001-006', 102, 1, 1,
    '메인 화면 설계', '대시보드 화면 설계',
    NULL, 'H2', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    12, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

INSERT INTO tasks VALUES (
    10012, NULL, 'PJT-001-006', 102, 2, 1,
    '상세 화면 설계', '상세/등록 화면 설계',
    NULL, 'H2', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    16, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

-- PJT-001-007
INSERT INTO tasks VALUES (
    10013, NULL, 'PJT-001-007', 103, 2, 1,
    '프로젝트 등록 개발', '상위/하위 프로젝트 등록 기능',
    NULL, 'H1', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    24, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

INSERT INTO tasks VALUES (
    10014, NULL, 'PJT-001-007', 103, 2, 1,
    '프로젝트 목록 개발', '프로젝트 목록/필터 기능',
    NULL, 'H2', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    20, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

-- PJT-001-008
INSERT INTO tasks VALUES (
    10015, NULL, 'PJT-001-008', 103, 2, 1,
    '업무 등록 개발', '업무 생성 기능',
    NULL, 'H1', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    30, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

INSERT INTO tasks VALUES (
    10016, NULL, 'PJT-001-008', 103, 2, 1,
    '업무 상태 변경 개발', '업무 상태 변경 및 이력 기능',
    NULL, 'H1', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    28, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

-- PJT-001-009
INSERT INTO tasks VALUES (
    10017, NULL, 'PJT-001-009', 103, 2, 1,
    '문서 등록 개발', '문서 작성/수정 기능',
    NULL, 'H2', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    24, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

INSERT INTO tasks VALUES (
    10018, NULL, 'PJT-001-009', 103, 2, 1,
    '댓글 기능 개발', '문서 댓글 기능',
    NULL, 'H3', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    16, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

-- PJT-002-001
INSERT INTO tasks VALUES (
    10019, NULL, 'PJT-002-001', 201, 1, 1,
    '상품 요구사항 정리', '상품 분류/상품정보 요구사항 정리',
    NULL, 'H2', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    8, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

INSERT INTO tasks VALUES (
    10020, NULL, 'PJT-002-001', 201, 2, 1,
    '상품 옵션 정책 정리', '사이즈/재고/옵션 정책 정의',
    NULL, 'H3', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    10, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

-- PJT-002-002
INSERT INTO tasks VALUES (
    10021, NULL, 'PJT-002-002', 201, 1, 1,
    '주문결제 요구사항 정리', '주문/결제 프로세스 요구사항 정리',
    NULL, 'H2', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    8, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

INSERT INTO tasks VALUES (
    10022, NULL, 'PJT-002-002', 201, 2, 1,
    '배송 정책 정리', '배송비/배송상태 정책 정리',
    NULL, 'H3', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    10, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

-- PJT-002-003
INSERT INTO tasks VALUES (
    10023, NULL, 'PJT-002-003', 201, 1, 1,
    '회원 혜택 요구사항 정리', '등급/쿠폰/포인트 정책 정리',
    NULL, 'H2', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    8, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

INSERT INTO tasks VALUES (
    10024, NULL, 'PJT-002-003', 201, 2, 1,
    '프로모션 요구사항 정리', '이벤트/혜택 요구사항 정리',
    NULL, 'H3', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    10, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

-- PJT-002-004
INSERT INTO tasks VALUES (
    10025, NULL, 'PJT-002-004', 202, 2, 1,
    '상품상세 UI 설계', '상품상세 화면 UI/구성 설계',
    NULL, 'H2', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    12, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

INSERT INTO tasks VALUES (
    10026, NULL, 'PJT-002-004', 202, 2, 1,
    '상품 조회 흐름 설계', '카테고리/상세 진입 흐름 설계',
    NULL, 'H2', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    10, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

-- PJT-002-005
INSERT INTO tasks VALUES (
    10027, NULL, 'PJT-002-005', 202, 2, 1,
    '장바구니 화면 설계', '장바구니 UI 및 수량변경 설계',
    NULL, 'H2', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    12, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

INSERT INTO tasks VALUES (
    10028, NULL, 'PJT-002-005', 202, 2, 1,
    '결제 프로세스 설계', '주문서/결제 완료 흐름 설계',
    NULL, 'H3', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    10, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

-- PJT-002-006
INSERT INTO tasks VALUES (
    10029, NULL, 'PJT-002-006', 202, 2, 1,
    '이벤트 화면 설계', '이벤트/프로모션 화면 설계',
    NULL, 'H2', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    14, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

INSERT INTO tasks VALUES (
    10030, NULL, 'PJT-002-006', 202, 2, 1,
    '혜택 적용 정책 설계', '쿠폰/포인트 적용 규칙 설계',
    NULL, 'H3', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    10, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

-- PJT-002-007
INSERT INTO tasks VALUES (
    10031, NULL, 'PJT-002-007', 203, 1, 1,
    '상품 목록 개발', '상품 목록/카테고리 기능 개발',
    NULL, 'H1', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    10, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

INSERT INTO tasks VALUES (
    10032, NULL, 'PJT-002-007', 203, 2, 1,
    '상품 상세 개발', '상품 상세/옵션 선택 기능 개발',
    NULL, 'H2', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    8, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

-- PJT-002-008
INSERT INTO tasks VALUES (
    10033, NULL, 'PJT-002-008', 203, 2, 1,
    '주문 기능 개발', '장바구니/주문서 기능 개발',
    NULL, 'H1', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    20, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

INSERT INTO tasks VALUES (
    10034, NULL, 'PJT-002-008', 203, 2, 1,
    '결제 기능 개발', '결제 완료/주문 상태 처리 개발',
    NULL, 'H1', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    20, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

-- PJT-002-009
INSERT INTO tasks VALUES (
    10035, NULL, 'PJT-002-009', 203, 2, 1,
    '오픈 점검', '베리즈몰 오픈 전 최종 점검',
    NULL, 'H1', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    12, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

INSERT INTO tasks VALUES (
    10036, NULL, 'PJT-002-009', 203, 2, 1,
    '오픈 후 안정화', '오픈 후 오류 및 운영 이슈 점검',
    NULL, 'H2', 0,
    DATE '2026-03-25', DATE '2026-04-24',
    14, 0, 'Q1', NULL,
    SYSTIMESTAMP, SYSTIMESTAMP,
    DATE '2026-03-25', DATE '2026-04-24',
    NULL, NULL
);

SELECT * FROM tasks;
COMMIT;
SELECT * FROM tab;
SELECT * FROM tasks;

SELECT * FROM task_statuses;
SELECT * FROM common_code;

--공통 모듈 : 모든 사람이 같이 사용할 수 있는 함수 - common.java : 날자변환 (date util, string utils) 
--front : axios 자체를 만들어서 wrapping하는거 같이 쓰기 - 예외처리 (에러메세지) -> back : / front : exception으로 만들어도 되고. / properties에 넣어도되고. / erro  

SELECT 
	P.PROJECT_ID,
	P.PROJECT_NAME,
	SUM(CASE WHEN TS.STATUS_NAME = '시작 전' THEN 1 ELSE 0 END) AS 신규,
	SUM(CASE WHEN TS.STATUS_NAME = '진행중' THEN 1 ELSE 0 END) AS 진행중,
	SUM(CASE WHEN TS.STATUS_NAME = '개발완료' THEN 1 ELSE 0 END) AS 개발완료,
	SUM(CASE WHEN TS.STATUS_NAME = '반려' THEN 1 ELSE 0 END) AS 반려,
	SUM(CASE WHEN TS.STATUS_NAME = '종료' THEN 1 ELSE 0 END) AS 종료,
	COUNT(*) AS 합계
FROM TASKS T 
JOIN TASK_STATUSES TS
	ON T.TASK_STATUS_ID =TS.TASK_STATUS_ID
LEFT JOIN PROJECTS P
	ON T.PROJECT_ID=P.PROJECT_ID
GROUP BY 
	P.PROJECT_ID,
	P.PROJECT_NAME
ORDER BY
	P.PROJECT_ID;

--부모 프로젝트 하나로 묶어 집계하기 
--TASK테이블 기준으로 조회 => TASK없는 프젝도 나오게 하려면 
SELECT
    NVL(PP.PROJECT_ID, P.PROJECT_ID) AS PROJECT_ID,
    NVL(PP.PROJECT_NAME, P.PROJECT_NAME) AS PROJECT_NAME,
    SUM(CASE WHEN TS.STATUS_NAME = '시작 전' THEN 1 ELSE 0 END) AS 신규,
    SUM(CASE WHEN TS.STATUS_NAME = '진행중' THEN 1 ELSE 0 END) AS 진행중,
    SUM(CASE WHEN TS.STATUS_NAME = '개발완료' THEN 1 ELSE 0 END) AS 개발완료,
    SUM(CASE WHEN TS.STATUS_NAME = '반려' THEN 1 ELSE 0 END) AS 반려,
    SUM(CASE WHEN TS.STATUS_NAME = '종료' THEN 1 ELSE 0 END) AS 종료,
    COUNT(*) AS 합계
FROM TASKS T
JOIN TASK_STATUSES TS
    ON T.TASK_STATUS_ID = TS.TASK_STATUS_ID
JOIN PROJECTS P
    ON T.PROJECT_ID = P.PROJECT_ID
JOIN PROJECTS PP
    ON P.PARENT_PROJECT_ID = PP.PROJECT_ID
GROUP BY
    NVL(PP.PROJECT_ID, P.PROJECT_ID),
    NVL(PP.PROJECT_NAME, P.PROJECT_NAME)
ORDER BY
    PROJECT_ID;


SELECT
    ROOT_P.PROJECT_ID,
    ROOT_P.PROJECT_NAME,
    SUM(CASE WHEN TS.STATUS_NAME = '시작 전' THEN 1 ELSE 0 END) AS 신규,
    SUM(CASE WHEN TS.STATUS_NAME = '진행중' THEN 1 ELSE 0 END) AS 진행중,
    SUM(CASE WHEN TS.STATUS_NAME = '개발완료' THEN 1 ELSE 0 END) AS 개발완료,
    SUM(CASE WHEN TS.STATUS_NAME = '반려' THEN 1 ELSE 0 END) AS 반려,
    SUM(CASE WHEN TS.STATUS_NAME = '종료' THEN 1 ELSE 0 END) AS 종료,
    COUNT(T.TASK_ID) AS 합계
FROM PROJECTS ROOT_P
LEFT JOIN PROJECTS CHILD_P
    ON CHILD_P.PARENT_PROJECT_ID = ROOT_P.PROJECT_ID
    OR CHILD_P.PROJECT_ID = ROOT_P.PROJECT_ID
LEFT JOIN TASKS T
    ON T.PROJECT_ID = CHILD_P.PROJECT_ID
LEFT JOIN TASK_STATUSES TS
    ON T.TASK_STATUS_ID = TS.TASK_STATUS_ID
WHERE ROOT_P.PARENT_PROJECT_ID IS NULL
GROUP BY
    ROOT_P.PROJECT_ID,
    ROOT_P.PROJECT_NAME
ORDER BY
    ROOT_P.PROJECT_ID;
	



INSERT INTO USERS (
    USER_ID,
    USER_NAME,
    EMAIL,
    PASSWORD,
    IS_ACTIVE,
    CREATED_AT,
    UPDATED_AT,
    EDITOR_USER_ID,
    USER_TYPE
) VALUES (
    1003,
    '박피엠',
    'pm.park@example.com',
    '$2a$16$d/yzntBUiHarIJ9861oZQOf7VLyO8XVQA0PdPBFPNsPrxfBOJ4kdS',
    'O1',
    SYSTIMESTAMP,
    NULL,
    NULL,
    'M1'
);

COMMIT;

INSERT INTO USERS (
    USER_ID,
    USER_NAME,
    EMAIL,
    PASSWORD,
    IS_ACTIVE,
    CREATED_AT,
    UPDATED_AT,
    EDITOR_USER_ID,
    USER_TYPE
) VALUES (
    1004,
    '윤피엘',
    'pl.yoon@example.com',
    '$2a$16$d/yzntBUiHarIJ9861oZQOf7VLyO8XVQA0PdPBFPNsPrxfBOJ4kdS',
    'O1',
    SYSTIMESTAMP,
    NULL,
    NULL,
    'M2'
);

INSERT INTO USERS (
    USER_ID,
    USER_NAME,
    EMAIL,
    PASSWORD,
    IS_ACTIVE,
    CREATED_AT,
    UPDATED_AT,
    EDITOR_USER_ID,
    USER_TYPE
) VALUES (
    1005,
    '강사원',
    'staff.kang@example.com',
    '$2a$16$d/yzntBUiHarIJ9861oZQOf7VLyO8XVQA0PdPBFPNsPrxfBOJ4kdS',
    'O1',
    SYSTIMESTAMP,
    NULL,
    NULL,
    'M2'
);

INSERT INTO groups (
    group_id,
    group_name,
    group_type,
    description,
    is_active,
    created_by,
    updated_at,
    created_at,
    editor_user_id
) VALUES (
    1,
    'back',
    'C1',
    '백엔드 그룹',
    'O1',
    1001,
    NULL,
    SYSTIMESTAMP,
    NULL
);

INSERT INTO groups (
    group_id,
    group_name,
    group_type,
    description,
    is_active,
    created_by,
    updated_at,
    created_at,
    editor_user_id
) VALUES (
    2,
    'front',
    'C1',
    '프론트엔드 그룹',
    'O1',
    1001,
    NULL,
    SYSTIMESTAMP,
    NULL
);

INSERT INTO groups (
    group_id,
    group_name,
    group_type,
    description,
    is_active,
    created_by,
    updated_at,
    created_at,
    editor_user_id
) VALUES (
    3,
    'planning',
    'C1',
    '기획 그룹',
    'O1',
    1001,
    NULL,
    SYSTIMESTAMP,
    NULL
);

INSERT INTO groups (
    group_id,
    group_name,
    group_type,
    description,
    is_active,
    created_by,
    updated_at,
    created_at,
    editor_user_id
) VALUES (
    4,
    'design',
    'C1',
    '디자인 그룹',
    'O1',
    1001,
    NULL,
    SYSTIMESTAMP,
    NULL
);

INSERT INTO groups (
    group_id,
    group_name,
    group_type,
    description,
    is_active,
    created_by,
    updated_at,
    created_at,
    editor_user_id
) VALUES (
    5,
    'PM',
    'C1',
    'PM 그룹',
    'O1',
    1001,
    NULL,
    SYSTIMESTAMP,
    NULL
);

INSERT INTO groups (
    group_id,
    group_name,
    group_type,
    description,
    is_active,
    created_by,
    updated_at,
    created_at,
    editor_user_id
) VALUES (
    6,
    'LeaderPL',
    'C1',
    '총괄 PL 그룹',
    'O1',
    1001,
    NULL,
    SYSTIMESTAMP,
    NULL
);

--그룹멤버 더미
INSERT INTO ROLES (
    ROLE_ID,
    ROLE_NAME,
    DESCRIPTION,
    IS_ACTIVE,
    CREATED_AT,
    UPDATED_AT,
    EDITOR_USER_ID
) VALUES (
    1,
    'test',
    '테스트 역할',
    'O1',
    SYSTIMESTAMP,
    NULL,
    NULL
);

INSERT INTO GROUP_MEMBERS (
    GROUP_MEMBER_ID,
    USER_ID,
    GROUP_ID,
    ROLE_ID,
    JOINED_AT,
    IS_ACTIVE
) VALUES (
    1,
    1003,
    1,
    1,
    SYSTIMESTAMP,
    'O1'
);

INSERT INTO GROUP_MEMBERS (
    GROUP_MEMBER_ID,
    USER_ID,
    GROUP_ID,
    ROLE_ID,
    JOINED_AT,
    IS_ACTIVE
) VALUES (
    2,
    1002,
    4,
    1,
    SYSTIMESTAMP,
    'O1'
);

INSERT INTO GROUP_MEMBERS (
    GROUP_MEMBER_ID,
    USER_ID,
    GROUP_ID,
    ROLE_ID,
    JOINED_AT,
    IS_ACTIVE
) VALUES (
    3,
    1004,
    1,
    1,
    SYSTIMESTAMP,
    'O1'
);

INSERT INTO GROUP_MEMBERS (
    GROUP_MEMBER_ID,
    USER_ID,
    GROUP_ID,
    ROLE_ID,
    JOINED_AT,
    IS_ACTIVE
) VALUES (
    4,
    1005,
    3,
    1,
    SYSTIMESTAMP,
    'O1'
);

UPDATE projects
SET PM_USER_ID=1003
WHERE PM_USER_ID =1002;
COMMIT;

UPDATE GROUP_MEMBERS
SET GROUP_ID=6
WHERE USER_ID =1004;
COMMIT;

SELECT * FROM users;
SELECT * FROM projects;
SELECT * FROM common_code;
SELECT * FROM GROUP_MEMBERS;

SELECT * FROM GROUPS;
SELECT * FROM roles;

SELECT
	GM.GROUP_MEMBER_ID,
	GM.USER_ID,
	GM.GROUP_ID,
	G.GROUP_NAME,
	U.USER_NAME 
FROM GROUPS G
LEFT JOIN GROUP_MEMBERS GM 
	ON G.GROUP_ID = GM.GROUP_ID
JOIN USERS U
	ON U.USER_ID=GM.USER_ID;

SELECT 
	U.USER_ID, 
	U.USER_NAME
FROM USERS U
JOIN GROUP_MEMBERS GM
	ON GM.USER_ID = U.USER_ID 
WHERE GM.GROUP_ID = 6 AND U.is_active = 'O1' AND U.user_type = 'M2';

SELECT * FROM common_code;
SELECT * FROM projects;
ALTER TABLE projects ADD use_milestone VARCHAR2(2);
COMMIT;

CREATE SEQUENCE project_seq
    START WITH 7
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

DROP SEQUENCE PROJECT_SEQ;

UPDATE projects
SET PROJECT_ID=10001
WHERE PROJECT_ID=101;

UPDATE TASKS
SET PROJECT_ID=10001
WHERE PROJECT_ID=101;

SELECT * FROM TASKS;
COMMIT;

SELECT
	PROJECT_NAME,
	START_DATE,
	END_DATE
FROM PROJECTS;





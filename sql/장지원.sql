-- 테이블 전체 목록 및 데이터 확인
SELECT * FROM tab;
SELECT * FROM users;
SELECT * FROM projects;
SELECT * FROM tasks;

SELECT task_id, editor_user_id, updated_at 
FROM tasks 
WHERE task_id = 10152;

SELECT * FROM groups;
SELECT * FROM roles;
SELECT * FROM project_members;

SELECT * FROM task_types;
SELECT * FROM task_statuses;

SELECT * FROM notifications;
SELECT * FROM notification_targets;
SELECT * FROM task_rejections;
SELECT * FROM time_entries;


DELETE FROM notification_targets;
DELETE FROM notifications;
COMMIT;

SELECT * FROM attachments;
-- 시퀀스도 초기화
DROP SEQUENCE notifications_seq;
CREATE SEQUENCE notifications_seq
  START WITH 1
  INCREMENT BY 1
  NOCACHE
  NOCYCLE;

DROP SEQUENCE notification_targets_seq;
CREATE SEQUENCE notification_targets_seq
  START WITH 1
  INCREMENT BY 1
  NOCACHE;


-- 1. 시퀀스 현재값 한번 뽑기
SELECT notification_targets_seq.NEXTVAL FROM DUAL;
ALTER SEQUENCE notification_targets_seq INCREMENT BY 500;
SELECT notification_targets_seq.NEXTVAL FROM DUAL;
ALTER SEQUENCE notification_targets_seq INCREMENT BY 1;



SELECT notification_targets_seq.NEXTVAL FROM DUAL;
SELECT MAX(notification_target_no) FROM notification_targets;

ALTER SEQUENCE notification_targets_seq INCREMENT BY 2;
SELECT notification_targets_seq.NEXTVAL FROM DUAL;
ALTER SEQUENCE notification_targets_seq INCREMENT BY 1;

SELECT * FROM notification_targets ORDER BY notification_target_no DESC;


UPDATE task_statuses 
SET DESCRIPTION = 'G5' 
WHERE TASK_STATUS_ID = 5;

SELECT notification_targets_seq.NEXTVAL FROM DUAL;
SELECT MAX(notification_target_no) FROM notification_targets;

ALTER SEQUENCE notification_targets_seq INCREMENT BY 2;
SELECT notification_targets_seq.NEXTVAL FROM DUAL;
ALTER SEQUENCE notification_targets_seq INCREMENT BY 1;





SELECT * FROM milestones;
SELECT * FROM milestone_mapping;
SELECT * FROM task_rejections;
SELECT * FROM common_code;

UPDATE common_code
SET CODE_VALUE = 'G5'
WHERE CODE_VALUE = 'G6' AND GROUP_VALUE = '0G';

UPDATE tasks 
SET task_status_id = '3'
WHERE Task_id ='10127';


-- 특정 프로젝트 마일스톤 조회
SELECT m.milestone_id, m.milestone_name
FROM milestones m
JOIN milestone_mapping mm ON m.milestone_id = mm.milestone_id
WHERE mm.project_id = 106;

-- 테이블 컬럼 정보 확인
SELECT column_name FROM USER_TAB_COLUMNS WHERE TABLE_NAME = 'TASKS';
SELECT column_name FROM USER_TAB_COLUMNS WHERE TABLE_NAME = 'PROJECTS';
SELECT column_name FROM USER_TAB_COLUMNS WHERE TABLE_NAME = 'MILESTONE_MAPPING';
SELECT column_name FROM USER_TAB_COLUMNS WHERE TABLE_NAME = 'TASK_REJECTIONS';
SELECT column_name FROM USER_TAB_COLUMNS WHERE TABLE_NAME = 'NOTIFICATIONS';
SELECT column_name FROM USER_TAB_COLUMNS WHERE TABLE_NAME = 'NOTIFICATION_TARGETS';

-- 테이블 구조 변경
ALTER TABLE tasks MODIFY (assignee_user_id NULL);

-- 시퀀스 관리 (task_rejection)
CREATE SEQUENCE task_rejection_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

CREATE SEQUENCE notifications_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

CREATE SEQUENCE notification_targets_seq START WITH 1 INCREMENT BY 1;

-- 시퀀스 관리 (task) : 기존 시퀀스 삭제 후 최대값 기준으로 재설정
DROP SEQUENCE task_seq;
SELECT MAX(task_id) FROM tasks; -- 현재 최대값 확인 후 아래 START WITH 설정

CREATE SEQUENCE task_seq
    START WITH 10037
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

DROP SEQUENCE notifications_seq;
CREATE SEQUENCE notifications_seq
    START WITH 200
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

DROP SEQUENCE notification_targets_seq;
CREATE SEQUENCE notification_targets_seq
    START WITH 400
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;


-- 특정 업무 상세 조회 (VO/DTO 매핑용)
SELECT 
    task_id as taskId,
    project_id as projectId,
    task_type_id as taskTypeId,
    title,
    content,
    assignee_user_id as assigneeUserId,
    task_status_id as taskStatusId,
    priority_code as priorityCode,
    milestone_id as milestoneId,
    start_date as startDate,
    due_date as dueDate,
    actual_hours as actualHours,
    progress_rate as progressRate
FROM tasks
WHERE task_id = 10083;

-- 업무 정보 업데이트
UPDATE tasks
SET 
    title = '로그인 화면 API 구성(수정)', 
    content = '기존 로직에서 예외 처리 추가 및 성능 개선 작업 진행',
    task_status_id = 2,           
    priority_code = 'H2',         
    assignee_user_id = 1002,     
    progress_rate = 50,           
    actual_hours = 8,             
    start_date = TO_DATE('2026-03-27', 'YYYY-MM-DD'),
    due_date = TO_DATE('2026-04-10', 'YYYY-MM-DD'),
    updated_at = SYSDATE,         
    editor_user_id = 1002     
WHERE task_id = 10080;

-- 데이터 삭제
DELETE FROM COMMON_CODE  WHERE code_value = 'G5';

SELECT pm.*, u.user_name, r.role_name
FROM project_members pm
JOIN users u ON pm.user_id = u.user_id
JOIN project_member_roles pmr ON pm.project_member_id = pmr.project_member_id
JOIN roles r ON pmr.role_id = r.role_id
WHERE pm.project_id = '1'
AND pm.is_active = 'O1';


DECLARE
    v_cursor SYS_REFCURSOR;
    v_id NUMBER;
BEGIN
    SP_GET_NOTIFICATION_RECEIVERS(1, 1004, v_cursor);
    LOOP
        FETCH v_cursor INTO v_id;
        EXIT WHEN v_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('receiver: ' || v_id);
    END LOOP;
END;
SELECT * FROM milestones;

---------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE SP_GET_TASK_TOTAL_INFO (
    p_task_id      IN  NUMBER,
    p_project_id   IN  NUMBER,
    taskDetail     OUT SYS_REFCURSOR,
    projectList    OUT SYS_REFCURSOR,
    userList       OUT SYS_REFCURSOR,
    taskTypeList   OUT SYS_REFCURSOR,
    milestoneList  OUT SYS_REFCURSOR,
    statusList     OUT SYS_REFCURSOR,
    timeEntryList  OUT SYS_REFCURSOR ,
    attachmentList OUT SYS_REFCURSOR
) AS
    v_target_project_id NUMBER;
    v_root_project_id   NUMBER;
BEGIN
    -- 프로젝트 id 결정
    IF p_task_id IS NOT NULL THEN
        SELECT project_id INTO v_target_project_id FROM tasks WHERE task_id = p_task_id;
    ELSE
        v_target_project_id := p_project_id;
    END IF;

    -- 루트 프로젝트 id결정
	SELECT NVL(parent_project_id, project_id)
    INTO v_root_project_id
    FROM projects
    WHERE project_id = v_target_project_id;

    -- 업무 상세 
OPEN taskDetail FOR
SELECT
    t.task_id,
    t.project_id,
    t.parent_task_id,
    t.milestone_id,
    t.task_type_id,
    t.task_status_id,
    t.title,
    t.content,
    t.assignee_user_id,
    t.priority_code,
    t.progress_rate,
    t.estimated_hours,
    t.actual_hours,      
    t.start_date,
    t.due_date,
    t.est_start_date,
    t.est_end_date,
    t.created_by,
    t.created_at,
    t.updated_at,
    t.is_deleted,
    t.editor_user_id,
    t.attachment_group_id,
    u.user_name AS assigneename,
    (SELECT NVL(SUM(hours), 0) 
     FROM time_entries WHERE task_id = t.task_id) AS total_time_entries,
    CASE
        WHEN (SELECT SUM(hours) FROM time_entries WHERE task_id = t.task_id) > 0
        THEN (SELECT SUM(hours) FROM time_entries WHERE task_id = t.task_id)
        ELSE t.actual_hours
    END AS final_actual_hours
FROM tasks t
LEFT JOIN users u ON t.assignee_user_id = u.user_id
WHERE t.task_id = p_task_id;

    -- 타임 엔트리 목록 ← 추가
    OPEN timeEntryList FOR
    SELECT
        te.time_entry_id,
        te.user_id,
        u.user_name,
        te.work_date,
        te.hours,
        te.task_desc,
 		TO_CHAR(te.created_at, 'YYYY-MM-DD HH24:MI:SS') AS created_at   
 		FROM time_entries te
    JOIN users u ON te.user_id = u.user_id
    WHERE te.task_id = p_task_id
    ORDER BY te.work_date DESC;


    -- 프로젝트 목록
    OPEN projectList FOR
    SELECT * FROM projects 
    WHERE project_id = v_target_project_id
       OR project_id = (SELECT parent_project_id FROM projects WHERE project_id = v_target_project_id)
       OR parent_project_id = v_target_project_id;

    -- 유저 
    OPEN userList FOR
    SELECT
        u.user_id   AS user_id,
        u.user_name AS user_name,
        r.role_name AS role_name
    FROM users u
    JOIN project_members pm ON u.user_id = pm.user_id
        AND pm.project_id = v_root_project_id
        AND pm.is_active = 'O1'
    JOIN project_member_roles pmr ON pm.project_member_id = pmr.project_member_id
    JOIN roles r ON pmr.role_id = r.role_id
    WHERE u.is_active = 'O1'
      AND r.role_name = '개발자';
    
-- 업무 유형 
    OPEN taskTypeList FOR SELECT task_type_id, type_name FROM task_types WHERE is_active = 'O1';

    -- 마일스톤 
	OPEN milestoneList FOR
	SELECT milestone_id, milestone_name
	FROM milestones
	WHERE (project_id = v_target_project_id
	   OR project_id = (SELECT parent_project_id FROM projects WHERE project_id = v_target_project_id))
	  AND status!= 'E3' AND status != 'E2';    
    --업무 상태
    OPEN statusList FOR
    SELECT task_status_id, status_name, is_final
    FROM task_statuses
    WHERE is_active = 'O1'
    	ORDER BY task_status_id;
    
      -- 첨부파일 목록 추가
OPEN attachmentList FOR
SELECT
    a.attachment_id,
    a.file_name AS filename,   -- filename → file_name
    a.file_path,
    a.file_ext,
    a.attachment_group_id
FROM attachments a
JOIN tasks t ON a.attachment_group_id = t.attachment_group_id
WHERE t.task_id = p_task_id;

END;


------------------------------------------------------------------------

--알림 전용 프로시저
CREATE OR REPLACE PROCEDURE SP_GET_NOTIFICATION_RECEIVERS (
    p_project_id  IN  NUMBER,
    p_assignee_id IN  NUMBER,
    receiverList  OUT SYS_REFCURSOR
) AS
    v_root_project_id NUMBER;
BEGIN
    -- 루트 프로젝트 id 결정
    SELECT NVL(parent_project_id, project_id)
    INTO v_root_project_id
    FROM projects
    WHERE project_id = p_project_id;

    -- PM/PL/총괄PL 알림 전용
    OPEN receiverList FOR
        SELECT DISTINCT u.user_id
        FROM users u
        JOIN project_members pm ON u.user_id = pm.user_id
        JOIN project_member_roles pmr ON pm.project_member_id = pmr.project_member_id
        JOIN roles r ON pmr.role_id = r.role_id
        WHERE pm.project_id = v_root_project_id
          AND pm.is_active = 'O1'
          AND r.role_id IN (1, 2, 3)  -- PM, 총괄PL, PL
        UNION
        SELECT p_assignee_id FROM DUAL;  -- 담당자
END;


-----------------------------------------------------------------
--소요시간
CREATE OR REPLACE PROCEDURE SP_GET_TIME_ENTRY_REPORT (
    p_project_id        IN  NUMBER,
    p_assignee_user_id  IN  NUMBER,
    p_task_type_id      IN  NUMBER,
    p_keyword           IN  VARCHAR2,
    p_date_from         IN  DATE,
    p_date_to           IN  DATE,
    reportList          OUT SYS_REFCURSOR,
    assigneeList        OUT SYS_REFCURSOR,
    taskTypeList        OUT SYS_REFCURSOR
) AS
BEGIN

OPEN reportList FOR
SELECT
    t.task_id,
    t.title,
    t.start_date,
    t.due_date,
    t.progress_rate,
    p.project_id,
    NVL(pp.project_name, p.project_name) AS parent_project_name,
    CASE WHEN p.parent_project_id IS NULL THEN NULL ELSE p.project_name END AS sub_project_name,
    p.project_name,
    u.user_id,
    u.user_name,
    tt.task_type_id,
    tt.type_name,
    CASE
        WHEN NVL(te_sum.total_hours, 0) > 0 THEN te_sum.total_hours
        ELSE t.actual_hours
    END AS total_hours,
    te_sum.first_work_date,
    te_sum.last_work_date,
    NVL(te_sum.entry_count, 0) AS entry_count
FROM tasks t
JOIN projects p ON t.project_id = p.project_id
LEFT JOIN projects pp ON p.parent_project_id = pp.project_id  -- parent 조인
JOIN users u ON t.assignee_user_id = u.user_id
LEFT JOIN task_types tt ON t.task_type_id = tt.task_type_id
LEFT JOIN (
    -- 날짜 필터는 여기서만 적용
    SELECT
        task_id,
        SUM(hours)          AS total_hours,
        MIN(work_date)      AS first_work_date,
        MAX(work_date)      AS last_work_date,
        COUNT(time_entry_id) AS entry_count
    FROM time_entries
    WHERE (p_date_from IS NULL OR work_date >= p_date_from)
      AND (p_date_to   IS NULL OR work_date <= p_date_to)
    GROUP BY task_id
) te_sum ON t.task_id = te_sum.task_id
WHERE (t.is_deleted IS NULL OR t.is_deleted = 'Q2')
  AND t.project_id IN (
      SELECT project_id FROM projects
      START WITH project_id = p_project_id
      CONNECT BY parent_project_id = PRIOR project_id
  )
  AND (p_assignee_user_id IS NULL OR t.assignee_user_id = p_assignee_user_id)
  AND (p_task_type_id     IS NULL OR t.task_type_id     = p_task_type_id)
  AND (p_keyword          IS NULL OR t.title LIKE '%' || p_keyword || '%')
  AND (NVL(te_sum.total_hours, 0) > 0 OR t.actual_hours > 0)  
ORDER BY te_sum.last_work_date DESC NULLS LAST, t.task_id DESC;


    OPEN assigneeList FOR
    SELECT DISTINCT u.user_id, u.user_name
    FROM users u
    JOIN project_members pm ON u.user_id = pm.user_id
        AND pm.is_active = 'O1'
    WHERE pm.project_id IN (
        SELECT project_id FROM projects
        START WITH project_id = p_project_id
        CONNECT BY parent_project_id = PRIOR project_id
    )
    AND u.is_active = 'O1'
    ORDER BY u.user_name;

    OPEN taskTypeList FOR
    SELECT task_type_id, type_name
    FROM task_types
    WHERE is_active = 'O1'
    ORDER BY task_type_id;
    
END;    

-----------------------------------------------------------------------
--소요시간 상세
-- 조회
CREATE OR REPLACE PROCEDURE SP_GET_TIME_ENTRIES (
    p_task_id          IN  NUMBER,
    p_assignee_user_id IN  NUMBER,
    p_task_type_id     IN  NUMBER,
    p_date_from        IN  DATE,
    p_date_to          IN  DATE,
    entryList          OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN entryList FOR
    SELECT
        te.time_entry_id,
        te.task_id,
        te.user_id,
        u.user_name,
        te.work_date,
        te.hours,
        te.task_desc,
        te.created_at,
        tt.task_type_id,
        tt.type_name,
        t.title,
        NVL(pp.project_name, p.project_name) AS parent_project_name,
        CASE WHEN p.parent_project_id IS NULL THEN NULL ELSE p.project_name END AS sub_project_name
    FROM time_entries te
    JOIN users u ON te.user_id = u.user_id
    JOIN tasks t ON te.task_id = t.task_id
    JOIN projects p ON t.project_id = p.project_id
    LEFT JOIN projects pp ON p.parent_project_id = pp.project_id
    LEFT JOIN task_types tt ON t.task_type_id = tt.task_type_id
    WHERE te.task_id = p_task_id
      AND (p_assignee_user_id IS NULL OR te.user_id = p_assignee_user_id)
      AND (p_task_type_id     IS NULL OR t.task_type_id = p_task_type_id)
      AND (p_date_from        IS NULL OR te.work_date >= p_date_from)
      AND (p_date_to          IS NULL OR te.work_date <= p_date_to)
    ORDER BY te.work_date DESC;
END;

CREATE OR REPLACE PROCEDURE SP_INSERT_TIME_ENTRY (

    p_task_id  IN NUMBER,
    p_user_id  IN NUMBER,
    p_work_date IN DATE,
    p_hours    IN NUMBER,
    p_task_desc IN VARCHAR2
) AS
BEGIN
    INSERT INTO time_entries (task_id, user_id, work_date, hours, created_at, task_desc)
    VALUES (p_task_id, p_user_id, p_work_date, p_hours, SYSDATE, p_task_desc);
    COMMIT;
END;

-- 수정
CREATE OR REPLACE PROCEDURE SP_UPDATE_TIME_ENTRY (

    p_time_entry_id IN NUMBER,
    p_work_date     IN DATE,
    p_hours         IN NUMBER,
    p_task_desc     IN VARCHAR2
) AS
BEGIN
    UPDATE time_entries
    SET work_date = p_work_date,
        hours     = p_hours,
        task_desc = p_task_desc
    WHERE time_entry_id = p_time_entry_id;
    COMMIT;
END;

-- 삭제
CREATE OR REPLACE PROCEDURE SP_DELETE_TIME_ENTRY (
    p_time_entry_id IN NUMBER
) AS
BEGIN
    DELETE FROM time_entries WHERE time_entry_id = p_time_entry_id;
    COMMIT;
END;

SELECT t.task_id, t.title, t.actual_hours, te.task_id AS te_task_id, te.hours
FROM tasks t
LEFT JOIN time_entries te ON t.task_id = te.task_id
WHERE t.project_id IN (
  SELECT project_id FROM projects
  START WITH project_id = 1  -- 실제 projectId
  CONNECT BY parent_project_id = PRIOR project_id
)
AND ROWNUM <= 10;

SELECT DISTINCT is_deleted FROM tasks;

------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE SP_REJECT_TASK_COMPLETE (
    p_task_id          IN NUMBER,   
    p_rejection_reason IN VARCHAR2, 
    p_rejected_by      IN NUMBER,   
    o_result           OUT VARCHAR2 
) AS
BEGIN
    -- 반려 사유 저장
    INSERT INTO task_rejections (
        task_rejection_id,
        task_id,
        rejection_reason,
        rejected_by,
        rejection_date
    ) VALUES (
        task_rejection_seq.NEXTVAL,
        p_task_id,
        p_rejection_reason,
        p_rejected_by,
        SYSDATE
    );

    -- 업무 상태 변경 (G4: 반려 상태)
    UPDATE tasks
    SET task_status_id = 'G4'
    WHERE task_id = p_task_id;

    o_result := 'SUCCESS';
    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        o_result := 'ERROR: ' || SQLERRM;
END SP_REJECT_TASK_COMPLETE;

-- 컴파일
ALTER PROCEDURE SP_GET_TASK_TOTAL_INFO COMPILE;

-- 상태 확인
SELECT object_name, status, last_ddl_time
FROM user_objects
WHERE object_name = 'SP_GET_TASK_TOTAL_INFO';

-- 에러 확인
SELECT line, position, text
FROM user_errors
WHERE name = 'SP_GET_TASK_TOTAL_INFO'
ORDER BY line;

SELECT 
        t.*, 
        u.user_name AS assigneeName
    FROM tasks t
    LEFT JOIN users u ON t.assignee_user_id = u.user_id;
SELECT * FROM users;


--업무 전체 조회
        SELECT t.task_id,
        t.parent_task_id,
        t.milestone_id,
        t.task_type_id,
        t.task_status_id,
        t.title,
        t.content,
        t.assignee_user_id,
        t.actual_hours,
        u.user_name,
        t.priority_code,
        c.code_name,
        t.progress_rate,
        t.start_date,
        t.due_date,
        p.project_name,
        t2.type_name,
        t3.status_name
        FROM tasks t
        LEFT JOIN projects p ON t.project_id = p.project_id
        LEFT JOIN task_types t2 ON t.task_type_id = t2.task_type_id
        LEFT JOIN task_statuses t3 ON t.task_status_id = t3.task_status_id
        LEFT JOIN common_code c ON t.priority_code = c.code_value
        LEFT JOIN users u ON t.assignee_user_id = u.user_id;
        WHERE t.project_id = #{id} OR p.parent_project_id = #{pid}


SELECT * FROM tasks;
SELECT * FROM projects;

SELECT t.task_id,
       t.parent_task_id,
       p.PROJECT_NAME,
	   t.title,
	   t.assignee_user_id,
	   t.task_type_id,
       t2.type_name,
	   t.start_date,
       t.due_date,
       t.EST_START_DATE,
       t.EST_END_DATE 
	   FROM tasks t
	   LEFT JOIN projects p ON t.project_id = p.project_id
       LEFT JOIN task_types t2 ON t.task_type_id = t2.task_type_id
       LEFT JOIN users u ON t.ASSIGNEE_USER_ID = u.user_id
       WHERE t.project_id = p.PROJECT_ID  OR p.parent_project_id = p.PARENT_PROJECT_ID;

	   

	   






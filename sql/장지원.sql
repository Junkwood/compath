SELECT * FROM tab;

SELECT * FROM users;



SELECT * FROM tasks;
SELECT * FROM projects;

SELECT * FROM task_rejections;

CREATE SEQUENCE task_rejection_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;



SELECT column_name FROM USER_TAB_COLUMNS 
WHERE TABLE_NAME = 'TASK_REJECTIONS';

SELECT column_name FROM USER_TAB_COLUMNS 
WHERE TABLE_NAME = 'PROJECTS';

DELETE FROM tasks WHERE task_id = '10108';

SELECT * FROM task_types;
SELECT * FROM projects;
SELECT * FROM MILESTONES;

SELECT * FROM MILESTONE_MAPPING;
SELECT column_name FROM USER_TAB_COLUMNS 
WHERE TABLE_NAME = 'MILESTONE_MAPPING';

SELECT column_name FROM USER_TAB_COLUMNS 
WHERE TABLE_NAME = 'TASKS';


SELECT column_name FROM USER_TAB_COLUMNS 
WHERE TABLE_NAME = '';

SELECT * FROM COMMON_CODE;

SELECT * FROM milestones;
SELECT * FROM 

DROP SEQUENCE task_seq;

SELECT MAX(task_id) FROM tasks;

CREATE SEQUENCE task_seq
    START WITH 10037
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

SELECT * FROM ;

ALTER TABLE tasks MODIFY (assignee_user_id  NULL);

SELECT 
    m.milestone_id, 
    m.milestone_name
FROM milestones m
JOIN milestone_mapping mm ON m.milestone_id = mm.milestone_id
WHERE mm.project_id = 106;

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

ALTER PROCEDURE SP_GET_TASK_TOTAL_INFO COMPILE;

-- pl/sql 업무+ 프로젝트 + 상태 + 마일스톤
CREATE OR REPLACE PROCEDURE SP_GET_TASK_TOTAL_INFO (
    p_task_id      IN  NUMBER,
    p_project_id   IN  NUMBER,
    taskDetail     OUT SYS_REFCURSOR,
    projectList    OUT SYS_REFCURSOR,
    userList       OUT SYS_REFCURSOR,
    taskTypeList   OUT SYS_REFCURSOR,
    milestoneList  OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN taskDetail FOR
    SELECT t.*, u.user_name as ASSIGNEENAME
    FROM tasks t
    LEFT JOIN users u ON t.assignee_user_id = u.user_id
    WHERE t.task_id = p_task_id;

	OPEN projectList FOR
    SELECT * FROM projects 
    WHERE project_id IN (
        SELECT NVL(p_project_id, project_id) FROM tasks WHERE task_id = p_task_id
        UNION
        SELECT project_id FROM projects WHERE project_id = p_project_id --등록 할때
        UNION
        SELECT parent_project_id FROM projects WHERE project_id = p_project_id
    );   
	
	OPEN userList FOR SELECT user_id, user_name FROM users WHERE is_active = 'O1';

    OPEN taskTypeList FOR SELECT task_type_id, type_name FROM task_types WHERE is_active = 'O1';

	OPEN milestoneList FOR
    SELECT milestone_id, milestone_name FROM milestones
    WHERE project_id = NVL(
        (SELECT project_id FROM tasks WHERE task_id = p_task_id), -- 수정 시
        p_project_id -- 등록 시
    )
    OR project_id = (SELECT parent_project_id FROM projects WHERE project_id = p_project_id);
END;

SELECT line, position, text
FROM user_errors
WHERE name = 'SP_GET_TASK_TOTAL_INFO'
ORDER BY line;

SELECT object_name, status, last_ddl_time
FROM user_objects
WHERE object_name = 'SP_GET_TASK_TOTAL_INFO';
SELECT line, position, text
FROM user_errors
WHERE name = 'SP_GET_TASK_TOTAL_INFO'
ORDER BY line;



SELECT line, position, text
FROM user_errors
WHERE name = 'SP_GET_TASK_TOTAL_INFO';


CREATE OR REPLACE PROCEDURE SP_REJECT_TASK_COMPLETE (
    p_task_id          IN NUMBER,   
    p_rejection_reason IN VARCHAR2, 
    p_rejected_by      IN NUMBER,   -
    o_result           OUT VARCHAR2 
) AS
BEGIN
    -- 반려 사유 테이블에 저장
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

    -- 업무 테이블의 상태
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

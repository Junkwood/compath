SELECT * FROM tab;

SELECT * FROM users;



SELECT * FROM tasks;
SELECT * FROM task_rejections;

CREATE SEQUENCE task_rejection_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;



SELECT column_name FROM USER_TAB_COLUMNS 
WHERE TABLE_NAME = 'TASK_REJECTIONS';

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




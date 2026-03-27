SELECT * FROM tab;

SELECT * FROM users;

SELECT * FROM tasks;
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



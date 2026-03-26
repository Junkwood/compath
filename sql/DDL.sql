CREATE TABLE task_statuses (
	task_status_id	number			NOT NULL,
	status_name		varchar(100)	NULL,
	description		varchar(500)	NULL,
	is_active		varchar(2)		NULL,
	created_at		timestamp		NULL,
	updated_at		timestamp		NULL,
	created_by		number			NULL,
	editor_user_id	number			NULL
);


CREATE TABLE activity_logs (
	activity_log_id	number			NOT NULL,
	target_type		varchar(100)	NULL,
	target_id		varchar(50)		NULL,
	action_type		varchar(2)		NULL,
	actor_user_id		number		NULL,
	before_value		varchar(500)	NULL,
	after_value		varchar(500)	NULL,
	created_at		timestamp		NULL
);



CREATE TABLE notification_targets (
	notification_target_no	number		NOT NULL,
	notification_id			number		NOT NULL,
	receiver_id				number		NULL,
	is_read					varchar(2)	NULL,
	read_at					timestamp	NULL
);

CREATE TABLE project_members (
	project_member_id	number		NOT NULL,
	user_id			number		NOT NULL,
	project_id		number		NULL,
	joined_at		timestamp	NULL,
	is_active		varchar(2)	NULL
);

CREATE TABLE project_member_roles (
	project_member_role_id	number		NOT NULL,
	role_id			number		NOT NULL,
	project_member_id		number		NOT NULL,
	assigned_at		timestamp	NULL
);


CREATE TABLE milestone_mapping (
	milestone_mapping_id	number			NOT NULL,
	milestone_id		number			NOT NULL,
	project_id		number		NOT NULL,
	relation		varchar(2)		NULL
);

CREATE TABLE users (
	user_id			number			NOT NULL,
	user_name		varchar(50)		NULL,
	email			varchar(100)	NULL,
	password		varchar(255)	NULL,
	is_active		varchar(2)		NULL,
	created_at		timestamp		NULL,
	updated_at		timestamp		NULL,
	editor_user_id	number			NULL
);

CREATE TABLE permissions (
	permission_id		number			NOT NULL,
	permission_name		varchar(100)	NULL,
	permission_code		varchar(100)	NULL,
	description		varchar(500)	NULL,
	created_at		timestamp		NULL,
	permission_url		varchar(200)	NULL,
	permission_method	varchar(200)	NULL
);

CREATE TABLE group_members (
	group_member_id	number		NOT NULL,
	user_id		number		NOT NULL,
	group_id		number		NOT NULL,
	role_id		number		NOT NULL,
	joined_at		timestamp	NULL,
	is_active		varchar(2)	NULL
);

CREATE TABLE projects (
	project_id		number		NOT NULL,
	identifier		varchar(100) NULL,
	parent_project_id	number		NULL,
	project_name		varchar(100)	NULL,
	description		clob			NULL,
	start_date		date			NULL,
	end_date		date			NULL,
	status			varchar(30)		NULL,
	is_public		varchar(2)		NULL,
	created_at		timestamp		NULL,
	updated_at		timestamp		NULL,
	created_by		number			NULL,
	pm_user_id		number			NULL,
	pl_user_id		number			NULL,
	editor_user_id	number			NULL
);


CREATE TABLE groups (
	group_id		number			NOT NULL,
	group_name	varchar(100)	NULL,
	group_type	varchar(50)		NULL,
	description	varchar(500)	NULL,
	is_active		varchar(2)		NULL,
	created_by	number			NULL,
	updated_at	timestamp		NULL,
	created_at	timestamp		NULL,
	editor_user_id	number		NULL
);


CREATE TABLE meeting_logs (
	meeting_log_id		number		NOT NULL,
	project_id		number		NOT NULL,
	title			varchar(200)		NULL,
	meeting_type_code	varchar(2)		NULL,
	meeting_date		timestamp		NULL,
	place			varchar(100)		NULL,
	content			clob		NULL,
	ai_summary		clob		NULL,
	ai_action_items	clob		NULL,
	created_by		number		NULL,
	created_at		timestamp		NULL,
	updated_at		timestamp		NULL,
	stt_text		clob		NULL,
	attachment_group_id	number		NULL,
	editor_user_id	number		NULL
);


CREATE TABLE tasks (
	task_id			number		NOT NULL,
	parent_task_id		number		NULL,
	project_id		number		NOT NULL,
	milestone_id		number		NOT NULL,
	task_type_id		number		NOT NULL,
	task_status_id		number		NOT NULL,
	title			varchar(200)		NULL,
	content			clob		NULL,
	assignee_user_id	number		NULL,
	priority_code		varchar(30)		NULL,
	progress_rate		number		NULL,
	start_date		date		NULL,
	due_date		date		NULL,
	estimated_hours		number		NULL,
	actual_hours		number		NULL,
	is_deleted		varchar(2)		NULL,
	created_by		number		NULL,
	created_at		timestamp		NULL,
	updated_at		timestamp		NULL,
	est_start_date		date		NULL,
	est_end_date		date		NULL,
	attachment_group_id	number		NULL,
	editor_user_id	number		NULL
);



CREATE TABLE task_rejections (
	task_rejection_id	number		NOT NULL,
	task_id	number		NOT NULL,
	rejection_reason	varchar(1000)		NULL,
	rejected_by	number		NULL,
	rejection_date	timestamp		NULL
);

CREATE TABLE milestones (
	milestone_id	number		NOT NULL,
	created_by	number		NULL,
	milestone_name	varchar(200)		NULL,
	description	varchar(1000)		NULL,
	start_date	date		NULL,
	due_date	date		NULL,
	status	varchar(2)		NULL,
	created_at	timestamp		NULL,
	updated_at	timestamp		NULL,
	editor_user_id	number		NULL
);


CREATE TABLE attachments (
	attachment_id	number		NOT NULL,
	filename	varchar(255)		NULL,
	file_path	varchar(500)		NULL,
	file_ext	varchar(20)		NULL,
	attachment_group_id	number		NULL
);

CREATE TABLE task_types (
	task_type_id	number		NOT NULL,
	type_name	varchar(100)		NULL,
	description	varchar(500)		NULL,
	is_active	varchar(2)		NULL,
	created_at	timestamp		NULL,
	updated_at	timestamp		NULL,
	created_by	number		NULL,
	editor_user_id	number		NULL
);


CREATE TABLE role_permissions (
	role_permission_id	number		NOT NULL,
	role_id	number		NOT NULL,
	permission_id	number		NOT NULL,
	created_at	timestamp		NULL
);

CREATE TABLE memos (
	memo_id	number		NOT NULL,
	project_id	number		NOT NULL,
	user_id	number		NULL,
	memo_content	clob		NULL,
	created_at	timestamp		NULL
);

CREATE TABLE documents (
	document_id	number		NOT NULL,
	project_id	number		NOT NULL,
	title	varchar(100)		NULL,
	content	clob		NULL,
	is_pinned	varchar(2)		NULL,
	is_deleted	varchar(2)		NULL,
	created_by	number		NULL,
	created_at	timestamp		NULL,
	updated_at	timestamp		NULL,
	is_comment	varchar(2)		NULL,
	category	varchar(100)		NULL,
	attachment_group_id	number		NULL,
	editor_user_id	number		NULL
);


CREATE TABLE common_code (
	code_value	varchar(20)		NOT NULL,
	group_value	varchar(20)		NULL,
	code_name	varchar(20)		NULL,
	is_active	varchar(2)		NULL
);

CREATE TABLE time_entries (
	time_entry_id	number		NOT NULL,
	task_id	number		NOT NULL,
	user_id	number		NULL,
	work_date	date		NULL,
	hours	number		NULL,
	created_at	timestamp		NULL,
	task_desc	varchar(500)		NULL
);


CREATE TABLE task_workflows (
	task_workflow_id	number		NOT NULL,
	role_id	number		NOT NULL,
	task_type_id	number		NULL,
	from_status_id	number		NULL,
	to_status_id	number		NULL,
	is_allowed	varchar(2)		NULL,
	created_at	timestamp		NULL
);

CREATE TABLE roles (
	role_id	number		NOT NULL,
	role_name	varchar(100)		NULL,
	description	varchar(255)		NULL,
	is_active	varchar(2)		NULL,
	created_at	timestamp		NULL,
	updated_at	timestamp		NULL,
	editor_user_id	number		NULL
);

CREATE TABLE notifications (
	notification_id	number		NOT NULL,
	notification_type	varchar(2)		NULL,
	target_type	varchar(50)		NULL,
	target_id	number		NULL,
	title	varchar(100)		NULL,
	message	clob		NULL,
	created_by	number		NULL,
	created_at	timestamp		NULL
);


CREATE TABLE document_comments (
	document_comment_id	number		NOT NULL,
	document_id	number		NOT NULL,
	user_id	number		NULL,
	content	varchar(500)		NULL,
	is_deleted	varchar(2)		NULL,
	created_at	timestamp		NULL,
	updated_at	timestamp		NULL,
	editor_user_id	number		NULL
);

CREATE TABLE notices (
	notice_id			number		NOT NULL,
	project_id			number		NOT NULL,
	title				varchar(100)		NULL,
	content				clob		NULL,
	is_pinned			varchar(2)		NULL,
	is_deleted			varchar(2)		NULL,
	created_by			number		NULL,
	created_at			timestamp		NULL,
	updated_at			timestamp		NULL,
	category				varchar(100)		NULL,
	attachment_group_id		number		NULL,
	editor_user_id			number		NULL
);


CREATE TABLE meeting_tasks (
	meeting_task_id	number		NOT NULL,
	meeting_log_id	number		NOT NULL,
	task_id	number		NULL,
	created_at	timestamp		NULL
);

CREATE TABLE meeting_participants (
	meeting_participant_id	number		NOT NULL,
	meeting_log_id	number		NOT NULL,
	user_id	number		NULL
);


ALTER TABLE task_statuses ADD CONSTRAINT PK_TASK_STATUSES PRIMARY KEY (
	task_status_id
);

ALTER TABLE activity_logs ADD CONSTRAINT PK_ACTIVITY_LOGS PRIMARY KEY (
	activity_log_id
);

ALTER TABLE notification_targets ADD CONSTRAINT PK_NOTIFICATION_TARGETS PRIMARY KEY (
	notification_target_no
);

ALTER TABLE project_members ADD CONSTRAINT PK_PROJECT_MEMBERS PRIMARY KEY (
	project_member_id
);

ALTER TABLE project_member_roles ADD CONSTRAINT PK_PROJECT_MEMBER_ROLES PRIMARY KEY (
	project_member_role_id
);

ALTER TABLE milestone_mapping ADD CONSTRAINT PK_MILESTONE_MAPPING PRIMARY KEY (
	milestone_mapping_id
);

ALTER TABLE users ADD CONSTRAINT PK_USERS PRIMARY KEY (
	user_id
);

ALTER TABLE permissions ADD CONSTRAINT PK_PERMISSIONS PRIMARY KEY (
	permission_id
);

ALTER TABLE group_members ADD CONSTRAINT PK_GROUP_MEMBERS PRIMARY KEY (
	group_member_id
);

ALTER TABLE projects ADD CONSTRAINT PK_PROJECTS PRIMARY KEY (
	project_id
);

ALTER TABLE groups ADD CONSTRAINT PK_GROUPS PRIMARY KEY (
	group_id
);

ALTER TABLE meeting_logs ADD CONSTRAINT PK_MEETING_LOGS PRIMARY KEY (
	meeting_log_id
);

ALTER TABLE tasks ADD CONSTRAINT PK_TASKS PRIMARY KEY (
	task_id
);

ALTER TABLE task_rejections ADD CONSTRAINT PK_TASK_REJECTIONS PRIMARY KEY (
	task_rejection_id
);

ALTER TABLE milestones ADD CONSTRAINT PK_MILESTONES PRIMARY KEY (
	milestone_id
);

ALTER TABLE attachments ADD CONSTRAINT PK_ATTACHMENTS PRIMARY KEY (
	attachment_id
);

ALTER TABLE task_types ADD CONSTRAINT PK_TASK_TYPES PRIMARY KEY (
	task_type_id
);

ALTER TABLE role_permissions ADD CONSTRAINT PK_ROLE_PERMISSIONS PRIMARY KEY (
	role_permission_id
);

ALTER TABLE memos ADD CONSTRAINT PK_MEMOS PRIMARY KEY (
	memo_id
);

ALTER TABLE documents ADD CONSTRAINT PK_DOCUMENTS PRIMARY KEY (
	document_id
);

ALTER TABLE common_code ADD CONSTRAINT PK_COMMON_CODE PRIMARY KEY (
	code_value
);

ALTER TABLE time_entries ADD CONSTRAINT PK_TIME_ENTRIES PRIMARY KEY (
	time_entry_id
);

ALTER TABLE task_workflows ADD CONSTRAINT PK_TASK_WORKFLOWS PRIMARY KEY (
	task_workflow_id
);

ALTER TABLE roles ADD CONSTRAINT PK_ROLES PRIMARY KEY (
	role_id
);

ALTER TABLE notifications ADD CONSTRAINT PK_NOTIFICATIONS PRIMARY KEY (
	notification_id
);

ALTER TABLE document_comments ADD CONSTRAINT PK_DOCUMENT_COMMENTS PRIMARY KEY (
	document_comment_id
);

ALTER TABLE notices ADD CONSTRAINT PK_NOTICES PRIMARY KEY (
	notice_id
);

ALTER TABLE meeting_tasks ADD CONSTRAINT PK_MEETING_TASKS PRIMARY KEY (
	meeting_task_id
);

ALTER TABLE meeting_participants ADD CONSTRAINT PK_MEETING_PARTICIPANTS PRIMARY KEY (
	meeting_participant_id
);

ALTER TABLE notification_targets ADD CONSTRAINT FK_notice_TO_notice_targets_1 FOREIGN KEY (
	notification_id
)
REFERENCES notifications (
	notification_id
);

ALTER TABLE project_members ADD CONSTRAINT FK_users_project_members_1 FOREIGN KEY (
	user_id
)
REFERENCES users (
	user_id
);

ALTER TABLE project_member_roles ADD CONSTRAINT FK_roles_project_mem_roles_1 FOREIGN KEY (
	role_id
)
REFERENCES roles (
	role_id
);

ALTER TABLE project_member_roles ADD CONSTRAINT FK_pj_mem_pj_mem_roles_1 FOREIGN KEY (
	project_member_id
)
REFERENCES project_members (
	project_member_id
);

ALTER TABLE milestone_mapping ADD CONSTRAINT FK_mile_TO_mile_mapping_1 FOREIGN KEY (
	milestone_id
)
REFERENCES milestones (
	milestone_id
);

ALTER TABLE milestone_mapping ADD CONSTRAINT FK_pj_TO_milestone_m_1 FOREIGN KEY (
	project_id
)
REFERENCES projects (
	project_id
);

ALTER TABLE group_members ADD CONSTRAINT FK_users_TO_group_members_1 FOREIGN KEY (
	user_id
)
REFERENCES users (
	user_id
);

ALTER TABLE group_members ADD CONSTRAINT FK_groups_TO_group_members_1 FOREIGN KEY (
	group_id
)
REFERENCES groups (
	group_id
);

ALTER TABLE group_members ADD CONSTRAINT FK_roles_group_mem_1 FOREIGN KEY (
	role_id
)
REFERENCES roles (
	role_id
);

ALTER TABLE projects ADD CONSTRAINT FK_projects_TO_projects_1 FOREIGN KEY (
	parent_project_id
)
REFERENCES projects (
	project_id
);

ALTER TABLE meeting_logs ADD CONSTRAINT FK_project_meet_logs_1 FOREIGN KEY (
	project_id
)
REFERENCES projects (
	project_id
);

ALTER TABLE tasks ADD CONSTRAINT FK_tasks_TO_tasks_1 FOREIGN KEY (
	parent_task_id
)
REFERENCES tasks (
	task_id
);

ALTER TABLE tasks ADD CONSTRAINT FK_projects_TO_tasks_1 FOREIGN KEY (
	project_id
)
REFERENCES projects (
	project_id
);

ALTER TABLE tasks ADD CONSTRAINT FK_milestones_TO_tasks_1 FOREIGN KEY (
	milestone_id
)
REFERENCES milestones (
	milestone_id
);

ALTER TABLE tasks ADD CONSTRAINT FK_task_types_TO_tasks_1 FOREIGN KEY (
	task_type_id
)
REFERENCES task_types (
	task_type_id
);

ALTER TABLE tasks ADD CONSTRAINT FK_task_stat_TO_tasks_1 FOREIGN KEY (
	task_status_id
)
REFERENCES task_statuses (
	task_status_id
);

ALTER TABLE task_rejections ADD CONSTRAINT FK_tasks_task_rejections_1 FOREIGN KEY (
	task_id
)
REFERENCES tasks (
	task_id
);

ALTER TABLE role_permissions ADD CONSTRAINT FK_roles_role_permissions_1 FOREIGN KEY (
	role_id
)
REFERENCES roles (
	role_id
);

ALTER TABLE role_permissions ADD CONSTRAINT FK_per_role_per_1 FOREIGN KEY (
	permission_id
)
REFERENCES permissions (
	permission_id
);

ALTER TABLE memos ADD CONSTRAINT FK_projects_TO_memos_1 FOREIGN KEY (
	project_id
)
REFERENCES projects (
	project_id
);

ALTER TABLE documents ADD CONSTRAINT FK_projects_documents_1 FOREIGN KEY (
	project_id
)
REFERENCES projects (
	project_id
);

ALTER TABLE time_entries ADD CONSTRAINT FK_tasks_time_entries_1 FOREIGN KEY (
	task_id
)
REFERENCES tasks (
	task_id
);

ALTER TABLE task_workflows ADD CONSTRAINT FK_roles_task_wf_1 FOREIGN KEY (
	role_id
)
REFERENCES roles (
	role_id
);

ALTER TABLE document_comments ADD CONSTRAINT FK_doc_doc_comments_1 FOREIGN KEY (
	document_id
)
REFERENCES documents (
	document_id
);

ALTER TABLE notices ADD CONSTRAINT FK_projects_notices_1 FOREIGN KEY (
	project_id
)
REFERENCES projects (
	project_id
);

ALTER TABLE meeting_tasks ADD CONSTRAINT FK_meet_logs_meet_tasks_1 FOREIGN KEY (
	meeting_log_id
)
REFERENCES meeting_logs (
	meeting_log_id
);

ALTER TABLE meeting_participants ADD CONSTRAINT FK_meet_logs_meet_par_1 FOREIGN KEY (
	meeting_log_id
)
REFERENCES meeting_logs (
	meeting_log_id
);

-- 1. 프로젝트 및 업무 담당자/생성자 연결
ALTER TABLE projects ADD CONSTRAINT FK_users_pj_created FOREIGN KEY (created_by) REFERENCES users (user_id);
ALTER TABLE projects ADD CONSTRAINT FK_users_TO_projects_pm FOREIGN KEY (pm_user_id) REFERENCES users (user_id);
ALTER TABLE projects ADD CONSTRAINT FK_users_TO_projects_pl FOREIGN KEY (pl_user_id) REFERENCES users (user_id);

ALTER TABLE tasks ADD CONSTRAINT FK_users_TO_tasks_created FOREIGN KEY (created_by) REFERENCES users (user_id);
ALTER TABLE tasks ADD CONSTRAINT FK_users_TO_tasks_assignee FOREIGN KEY (assignee_user_id) REFERENCES users (user_id);

-- 2. 회의록 및 참여자 연결
ALTER TABLE meeting_logs ADD CONSTRAINT FK_users_meet_logs_created FOREIGN KEY (created_by) REFERENCES users (user_id);
ALTER TABLE meeting_participants ADD CONSTRAINT FK_users_meet_par_user FOREIGN KEY (user_id) REFERENCES users (user_id);
ALTER TABLE meeting_tasks ADD CONSTRAINT FK_tasks_TO_meeting_tasks_1 FOREIGN KEY (task_id) REFERENCES tasks (task_id);

-- 3. 게시판(공지/문서) 연결
ALTER TABLE notices ADD CONSTRAINT FK_users_TO_notices FOREIGN KEY (created_by) REFERENCES users (user_id);
ALTER TABLE documents ADD CONSTRAINT FK_users_TO_doc FOREIGN KEY (created_by) REFERENCES users (user_id);
ALTER TABLE document_comments ADD CONSTRAINT FK_users_doc_comments_user FOREIGN KEY (user_id) REFERENCES users (user_id);

-- 4. 기타 활동 기록
ALTER TABLE activity_logs ADD CONSTRAINT FK_users_activity_logs_actor FOREIGN KEY (actor_user_id) REFERENCES users (user_id);
ALTER TABLE time_entries ADD CONSTRAINT FK_users_time_entries_user FOREIGN KEY (user_id) REFERENCES users (user_id);

COMMIT;
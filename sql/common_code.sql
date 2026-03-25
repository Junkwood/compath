ALTER TABLE COMMON_CODE 
MODIFY code_name VARCHAR2(100);

DELETE common_code
WHERE group_value = '0G';

SELECT * FROM common_code ORDER BY code_value;
COMMIT;

INSERT INTO common_code (code_value, group_value, code_name, is_active) VALUES ('0A', 'ROOT', '권한 코드', 'O1');
INSERT INTO common_code (code_value, group_value, code_name, is_active) VALUES ('0C', 'ROOT', '그룹 유형 코드', 'O1');
INSERT INTO common_code (code_value, group_value, code_name, is_active) VALUES ('0D', 'ROOT', '회의록처리 상태코드', 'O1');
INSERT INTO common_code (code_value, group_value, code_name, is_active) VALUES ('0E', 'ROOT', '마일스톤 상태 코드', 'O1');
INSERT INTO common_code (code_value, group_value, code_name, is_active) VALUES ('0H', 'ROOT', '업무 우선순위 코드', 'O1');
INSERT INTO common_code (code_value, group_value, code_name, is_active) VALUES ('0I', 'ROOT', '읽음 여부 코드', 'O1');
INSERT INTO common_code (code_value, group_value, code_name, is_active) VALUES ('0J', 'ROOT', '활동 유형 코드', 'O1');
INSERT INTO common_code (code_value, group_value, code_name, is_active) VALUES ('0K', 'ROOT', '댓글 사용 여부 코드', 'O1');
INSERT INTO common_code (code_value, group_value, code_name, is_active) VALUES ('0L', 'ROOT', '삭제 여부 코드', 'O1');
INSERT INTO common_code (code_value, group_value, code_name, is_active) VALUES ('0M', 'ROOT', '계정 분류 코드', 'O1');
INSERT INTO common_code (code_value, group_value, code_name, is_active) VALUES ('0N', 'ROOT', '회의록 유형 코드', 'O1');
INSERT INTO common_code (code_value, group_value, code_name, is_active) VALUES ('0O', 'ROOT', '활성 여부 코드', 'O1');
INSERT INTO common_code (code_value, group_value, code_name, is_active) VALUES ('0P', 'ROOT', '공개 여부 코드', 'O1');
INSERT INTO common_code (code_value, group_value, code_name, is_active) VALUES ('0Q', 'ROOT', '삭제 여부 코드', 'O1');
INSERT INTO common_code (code_value, group_value, code_name, is_active) VALUES ('0R', 'ROOT', '알림 유형 코드', 'O1');

------------------------------------------------------------
-- 2. 권한 코드 (0A)
------------------------------------------------------------
INSERT INTO common_code VALUES ('A1',  '0A', '상위프로젝트 조회', 'O1');
INSERT INTO common_code VALUES ('A2',  '0A', '상위프로젝트 생성', 'O1');
INSERT INTO common_code VALUES ('A3',  '0A', '상위프로젝트 수정', 'O1');
INSERT INTO common_code VALUES ('A4',  '0A', '상위프로젝트 삭제', 'O1');
INSERT INTO common_code VALUES ('A5',  '0A', '하위프로젝트 조회', 'O1');
INSERT INTO common_code VALUES ('A6',  '0A', '하위프로젝트 생성', 'O1');
INSERT INTO common_code VALUES ('A7',  '0A', '하위프로젝트 수정', 'O1');
INSERT INTO common_code VALUES ('A8',  '0A', '하위프로젝트 삭제', 'O1');
INSERT INTO common_code VALUES ('A9',  '0A', '업무 조회', 'O1');
INSERT INTO common_code VALUES ('A10', '0A', '업무 생성', 'O1');
INSERT INTO common_code VALUES ('A11', '0A', '업무 수정', 'O1');
INSERT INTO common_code VALUES ('A12', '0A', '업무 삭제', 'O1');
INSERT INTO common_code VALUES ('A13', '0A', '문서 조회', 'O1');
INSERT INTO common_code VALUES ('A14', '0A', '문서 생성', 'O1');
INSERT INTO common_code VALUES ('A15', '0A', '문서 수정', 'O1');
INSERT INTO common_code VALUES ('A16', '0A', '문서 삭제', 'O1');
INSERT INTO common_code VALUES ('A17', '0A', '회의록 조회', 'O1');
INSERT INTO common_code VALUES ('A18', '0A', '회의록 생성', 'O1');
INSERT INTO common_code VALUES ('A19', '0A', '회의록 수정', 'O1');
INSERT INTO common_code VALUES ('A20', '0A', '회의록 삭제', 'O1');

------------------------------------------------------------
-- 3. 그룹 유형 코드 (0C)
------------------------------------------------------------
INSERT INTO common_code VALUES ('C1', '0C', '직권 그룹', 'O1');
INSERT INTO common_code VALUES ('C2', '0C', '프로젝트 그룹', 'O1');

------------------------------------------------------------
-- 4. 회의록 처리 상태 코드 (0D) - 임시안
------------------------------------------------------------
INSERT INTO common_code VALUES ('D1', '0D', '작성중', 'O1');
INSERT INTO common_code VALUES ('D2', '0D', '완료', 'O1');
INSERT INTO common_code VALUES ('D3', '0D', '반려', 'O1');

------------------------------------------------------------
-- 5. 마일스톤 상태 코드 (0E)
------------------------------------------------------------
INSERT INTO common_code VALUES ('E1', '0E', '진행중', 'O1');
INSERT INTO common_code VALUES ('E2', '0E', '완료', 'O1');

------------------------------------------------------------
-- 6. 업무 상태 코드 (0G)
------------------------------------------------------------
INSERT INTO common_code VALUES ('G1', '0G', '시작 전', 'O1');
INSERT INTO common_code VALUES ('G2', '0G', '진행중', 'O1');
INSERT INTO common_code VALUES ('G3', '0G', '개발 완료', 'O1');
INSERT INTO common_code VALUES ('G4', '0G', '반려', 'O1');
INSERT INTO common_code VALUES ('G5', '0G', '피드백', 'O1');
INSERT INTO common_code VALUES ('G6', '0G', '종료', 'O1');

------------------------------------------------------------
-- 7. 업무 우선순위 코드 (0H)
------------------------------------------------------------
INSERT INTO common_code VALUES ('H1', '0H', '긴급', 'O1');
INSERT INTO common_code VALUES ('H2', '0H', '상', 'O1');
INSERT INTO common_code VALUES ('H3', '0H', '중', 'O1');
INSERT INTO common_code VALUES ('H4', '0H', '하', 'O1');

------------------------------------------------------------
-- 8. 읽음 여부 코드 (0I)
------------------------------------------------------------
INSERT INTO common_code VALUES ('I1', '0I', '읽음', 'O1');
INSERT INTO common_code VALUES ('I2', '0I', '안읽음', 'O1');

------------------------------------------------------------
-- 9. 활동 유형 코드 (0J)
------------------------------------------------------------
INSERT INTO common_code VALUES ('J0', '0J', 'create', 'O1');
INSERT INTO common_code VALUES ('J1', '0J', 'update', 'O1');
INSERT INTO common_code VALUES ('J2', '0J', 'delete', 'O1');
INSERT INTO common_code VALUES ('J3', '0J', 'status_change', 'O1');

------------------------------------------------------------
-- 10. 댓글 사용 여부 코드 (0K)
------------------------------------------------------------
INSERT INTO common_code VALUES ('K1', '0K', '사용', 'O1');
INSERT INTO common_code VALUES ('K2', '0K', '미사용', 'O1');

------------------------------------------------------------
-- 11. 삭제 여부 코드 (0Q)
------------------------------------------------------------
INSERT INTO common_code VALUES ('Q1', '0Q', '미삭제', 'O1');
INSERT INTO common_code VALUES ('Q2', '0Q', '삭제', 'O1');

------------------------------------------------------------
-- 12. 계정 분류 코드 (0M)
------------------------------------------------------------
INSERT INTO common_code VALUES ('M1', '0M', '관리자 계정', 'O1');
INSERT INTO common_code VALUES ('M2', '0M', '일반 계정', 'O1');

------------------------------------------------------------
-- 13. 회의록 유형 코드 (0N)
------------------------------------------------------------
INSERT INTO common_code VALUES ('N1', '0N', '일간', 'O1');
INSERT INTO common_code VALUES ('N2', '0N', '주간', 'O1');
INSERT INTO common_code VALUES ('N3', '0N', '월간', 'O1');
INSERT INTO common_code VALUES ('N4', '0N', '클라이언트', 'O1');
INSERT INTO common_code VALUES ('N5', '0N', '비정기', 'O1');

------------------------------------------------------------
-- 14. 활성 여부 코드 (0O)
------------------------------------------------------------
INSERT INTO common_code VALUES ('O1', '0O', '활성', 'O1');
INSERT INTO common_code VALUES ('O2', '0O', '비활성', 'O1');

------------------------------------------------------------
-- 15. 공개 여부 코드 (0P)
------------------------------------------------------------
INSERT INTO common_code VALUES ('P1', '0P', '공개', 'O1');
INSERT INTO common_code VALUES ('P2', '0P', '구성원에게 공개', 'O1');

------------------------------------------------------------
-- 16. 알림 유형 코드 (0R)
------------------------------------------------------------
INSERT INTO common_code VALUES ('R1', '0R', '공지사항 등록', 'O1');
INSERT INTO common_code VALUES ('R2', '0R', '업무 등록', 'O1');
INSERT INTO common_code VALUES ('R3', '0R', '업무 상태 변경', 'O1');
INSERT INTO common_code VALUES ('R4', '0R', '문서 등록', 'O1');
INSERT INTO common_code VALUES ('R5', '0R', '문서 댓글 등록', 'O1');
INSERT INTO common_code VALUES ('R7', '0R', '회의록 등록', 'O1');

------------------------------------------------------------
-- 프로젝트 상태 코드 그룹(0F)
------------------------------------------------------------
INSERT INTO common_code (code_value, group_value, code_name, is_active)
VALUES ('0F', 'ROOT', '프로젝트 상태 코드', 'O1');

------------------------------------------------------------
-- 프로젝트 상태 코드 상세
------------------------------------------------------------
INSERT INTO common_code (code_value, group_value, code_name, is_active)
VALUES ('F1', '0F', '진행중', 'O1');

INSERT INTO common_code (code_value, group_value, code_name, is_active)
VALUES ('F2', '0F', '완료', 'O1');

UPDATE common_code
SET code_name = '진행중'
WHERE code_name = '완료';

COMMIT;

INSERT INTO common_code (code_value, group_value, code_name, is_active)
VALUES ('F5', '0F', '개발완료', 'O1');



COMMIT;

SELECT * FROM COMMON_CODE ORDER BY code_value;

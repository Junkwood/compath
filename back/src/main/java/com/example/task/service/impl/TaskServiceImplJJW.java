package com.example.task.service.impl;

import com.example.alarm.dto.NotificationDto;
import com.example.alarm.mapper.NotificationMapper;
import com.example.alarm.service.NotificationService;
import com.example.attachment.service.AttachmentService;
import com.example.project.dto.ProjectDtoJJW;
import com.example.task.dto.*;
import com.example.task.mapper.TaskMapperJJW;
import com.example.task.service.TaskServiceJJW;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional
public class TaskServiceImplJJW implements TaskServiceJJW {

    private final TaskMapperJJW taskMapperJJW;
    private final NotificationService notificationService;
    private final AttachmentService attachmentService;

    //업무등록
    public void insert(TaskReqDtoJJW re,List<MultipartFile> files) throws IOException {
        // 파일이 있으면 먼저 등록 후 groupId 받아옴
        if (files != null && !files.isEmpty()) {
            int groupId = attachmentService.registerAttachments(files, 0);
            re.setAttachmentGroupId(groupId);
        }

        taskMapperJJW.insert(re);

        // 상위 업무일 때만 알림
        if (re.getParentTaskId() == null) {
            notificationService.sendToProjectMembers(
                    re.getProjectId(),
                    re.getAssigneeUserId(),
                    "R2",
                    re.getTaskId(),
                    "업무 등록",
                    "새로운 업무가 등록되었습니다.",
                    re.getAssigneeUserId()
            );
        }

        // 하위 업무 알림 (선택)
        else {
            notificationService.sendToOne(
                    re.getAssigneeUserId(),
                    "R2",
                    re.getTaskId(),
                    "하위 업무 등록",
                    "하위 업무가 추가되었습니다.",
                    re.getAssigneeUserId()
            );
        }
    }
    //업무 수정
    @Override
    public int updateTask(TaskReqDtoJJW dto,List<MultipartFile> files) throws IOException {
        if (dto.getTaskStatusId() != null && dto.getTaskStatusId() == 11) {
            if (dto.getStartDate() == null) dto.setStartDate(new Date());
        }
        // 첨부파일 처리
        if (files != null && !files.isEmpty()) {
            if (dto.getAttachmentGroupId() != null && dto.getAttachmentGroupId() > 0) {
                // 기존 groupId에 추가
                attachmentService.registerAttachments(files, dto.getAttachmentGroupId());
            } else {
                // 새 group 생성
                int groupId = attachmentService.registerAttachments(files, 0);
                dto.setAttachmentGroupId(groupId);
            }
        }

        if (dto.getDeletedAttachmentIds() != null && !dto.getDeletedAttachmentIds().isEmpty()) {
            taskMapperJJW.deleteAttachments(dto);
        }

        int result = taskMapperJJW.updateTask(dto);

        // 진행중
        if (dto.getTaskStatusId() == 11) {
            notificationService.sendToProjectMembers(
                    dto.getProjectId(), dto.getAssigneeUserId(),
                    "R3", dto.getTaskId(),
                    "업무 진행중", "담당 업무가 진행중으로 변경 되었습니다.",
                    dto.getAssigneeUserId()
            );
        }

        // 개발완료
        if (dto.getTaskStatusId() == 12) {
            notificationService.sendToProjectMembers(
                    dto.getProjectId(), dto.getAssigneeUserId(),
                    "R3", dto.getTaskId(),
                    "개발완료", "담당 업무가 개발완료 되었습니다.",
                    dto.getAssigneeUserId()
            );
        }

        // 업무 종료(보내는 사람 0은 임의로 해둠 딱히 문제 x)
        if (dto.getTaskStatusId() == 14) {
            notificationService.sendToOne(
                    dto.getAssigneeUserId(),
                    "R3", dto.getTaskId(),
                    "업무 종료", "담당 업무가 종료되었습니다.",
                    0
            );
        }
        return result;
    }
    //반려 사유 등록
    @Override
    public void insert1(TaskRejectDtoJJW re) {
        taskMapperJJW.insert1(re);
        taskMapperJJW.updateTaskStatus(re.getTaskId(), 13);

        TaskReqDtoJJW task = taskMapperJJW.getTaskById(re.getTaskId());

        notificationService.sendToOne(
                task.getAssigneeUserId(),
                "R3", re.getTaskId(),
                "업무 반려", "담당하신 업무가 반려되었습니다.",
                re.getRejectedBy()
        );
    }
    //업무 전체 조회
    @Override
    public List<TaskReqDtoJJW> getTaskAll() {
        return taskMapperJJW.getTaskAll();
    }

    //프로시저 사용
    @Override
    public Map<String, Object> getTaskInitData(Integer taskId, Integer projectId) {
        // 프로시저에 전달할 파라미터 맵
        Map<String, Object> params = new HashMap<>();
        params.put("taskId", taskId);
        params.put("projectId", projectId);

        // OUT 파라미터들이 params 맵에 들어감 ?
        taskMapperJJW.getTaskTotalInfo(params);

        // 커서 결과 값으로 보내기
        Map<String, Object> result = new HashMap<>();
        result.put("taskDetail", params.get("taskDetail"));
        result.put("projectList", params.get("projectList"));
        result.put("userList", params.get("userList"));
        result.put("taskTypeList", params.get("taskTypeList"));
        result.put("milestoneList", params.get("milestoneList"));
        result.put("statusList", params.get("statusList"));
        result.put("timeEntryList", params.get("timeEntryList"));
        result.put("attachmentList", params.get("attachmentList"));
        return result;


    }

    @Override
    public List<ProjectDtoJJW> getProjectList() {
        return taskMapperJJW.getProjectList();    }

}
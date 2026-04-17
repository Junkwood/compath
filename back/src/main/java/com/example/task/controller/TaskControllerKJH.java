package com.example.task.controller;

import com.example.attachment.dto.AttachmentDTO;
import com.example.attachment.service.AttachmentService;
import com.example.meeting.dto.MeetingDTO;
import com.example.task.dto.TaskDetailDTOKJH;
import com.example.task.dto.TaskListDTOKJH;
import com.example.task.dto.TaskLogDto;
import com.example.task.dto.TaskReqDtoJJW;
import com.example.task.service.TaskServiceKJH;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TaskControllerKJH {

    private final TaskServiceKJH service;
    private final AttachmentService attachmentService;

    // 목록 조회
    @GetMapping("/api/tasks")
    public List<TaskListDTOKJH> getAllTasks(TaskListDTOKJH dto) {
        return service.getAllTasks(dto);
    }

    // 필터링 조건 조회
    @GetMapping("/api/tasksFilters/{id}")
    public Map<String, Object> getAllFilterInfo(@PathVariable Integer id) {
        return service.getAllFiterInfo(id);
    }

    // 프로젝트 이름 조회
    @GetMapping("/api/tasks/projectname/{id}")
    public TaskListDTOKJH getProjectName(@PathVariable Integer id) {
        return service.getProjectName(id);
    }

    // 업무 단건 조회
    @GetMapping("/api/tasks/detail/{id}")
    public Map<String, Object> getTaskById(@PathVariable Integer id) {
        // 결과 담을 그릇
        Map<String, Object> result = new HashMap<>();

        TaskDetailDTOKJH info = service.getTaskById(id);
        result.put("taskInfo", info);

        Integer groupId = info.getAttachmentGroupId();

        if (groupId != null) {
            List<AttachmentDTO> list = attachmentService.getFileList(groupId);
            result.put("attachmentList", list);
        }

        return result;
    }

    // 소요시간 등록
    @PostMapping("/api/tasks/timelog")
    public List<TaskDetailDTOKJH> registerTimeEntries(@RequestBody TaskDetailDTOKJH dto) {
        return service.registerTimeEntries(dto);
    }

    //소요시간 단건 조회
    @GetMapping("/api/tasks/timelog/{id}")
    public List<TaskDetailDTOKJH> getTimeEntriesById(@PathVariable Integer id) {
        return service.getTimeEntriesById(id);
    }

    // 활동내역 목록 조회
    @GetMapping("/api/tasks/activityLog/{id}")
    public List<TaskDetailDTOKJH> getTaskActivityLogs(@PathVariable Integer id) {
        return service.getTimeLog(id);
    }


    // 공지사항, 문서 작성시 업무 유형
    @GetMapping("/api/tasks/typeList")
    public List<TaskDetailDTOKJH> getAllTaskType() {
        return service.getAllTaskType();
    }

    // 업무 비활성화
    @PutMapping("/api/tasks/modifyStatus/{id}")
    public int modifyTaskStatus(@PathVariable Integer id) {
        return service.modifyTaskStatus(id);
    }

    // 업무 비활성화
    @PutMapping("/api/tasks/modifyUser")
    public int modifyTaskUser(@RequestBody TaskListDTOKJH dto) {
        return service.modifyTaskUser(dto);
    }

    // 업무 작업이력
    @GetMapping("/api/tasks/activityLogs/{id}")
    public List<TaskLogDto> getActivityLogsByTaskId(@PathVariable Integer id) {
        return service.getActivityLogsByTaskId(id);
    }
}
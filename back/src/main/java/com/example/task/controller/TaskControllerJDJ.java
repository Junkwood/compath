package com.example.task.controller;

import com.example.task.dto.TaskListMainDashDtoJDJ;
import com.example.task.dto.TaskListSubDashDtoJDJ;
import com.example.task.dto.TaskSummaryDtoJDJ;
import com.example.task.service.TaskServiceJDJ;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class TaskControllerJDJ {

    final TaskServiceJDJ taskServiceJDJ;

    //프로젝트 개별 대쉬보드
    @ResponseBody
    @GetMapping("/TaskListDash")
    public List<TaskListMainDashDtoJDJ> list(){
        return taskServiceJDJ.getTaskListDash();
    }

    //하위프로젝트대쉬보드 업무통계 보기
    @GetMapping("/TaskSummary/{projectId}")
    public List<TaskSummaryDtoJDJ> getTaskSummaryByProjectId(@PathVariable int projectId) {
        return taskServiceJDJ.getTaskSummaryByProjectId(projectId);
    }

    //하위프로젝트대쉬보드 업무목록 조회
    @GetMapping("/SubProjectTaskList/{subProjectId}")
    public List<TaskListSubDashDtoJDJ> getTaskListBySubProjectId(@PathVariable int subProjectId){
        return taskServiceJDJ.getTaskListBySubProjectId(subProjectId);
    }
}

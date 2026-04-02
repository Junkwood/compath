package com.example.task.controller;

import com.example.milestone.dto.MilestoneDtoJJW;
import com.example.project.dto.ProjectDtoJJW;
import com.example.task.dto.*;
import com.example.task.service.TaskServiceJJW;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TaskControllerJJW {

    final TaskServiceJJW taskServiceJJW;

    //상위 업무 등록
    @PostMapping("/tasks")
    public TaskReqDtoJJW registerTasks(@Valid @RequestBody TaskReqDtoJJW dto){
     taskServiceJJW.insert(dto);
     return dto;
    }

    //사유 모달 등록
    @PostMapping("/reject")
    public TaskRejectDtoJJW regiseterRejected(@RequestBody TaskRejectDtoJJW re){
        taskServiceJJW.insert1(re);
        return re;
    }

    //상위 업무 수정
    @PutMapping("/task/{taskId}")
    public TaskReqDtoJJW updateTasks(@PathVariable("taskId") Integer taskId,
                                     @RequestBody TaskReqDtoJJW dto) {
        dto.setTaskId(taskId);
        taskServiceJJW.updateTask(dto);
        return dto;
    }

    //업무 전체 조회(간트 차트용)
    @ResponseBody
    @GetMapping("/task/gantt")
    public List<TaskReqDtoJJW> list5(){
        return  taskServiceJJW.getTaskAll();
    }

    //프로시저(모든 조회 가능)
    @ResponseBody
    @GetMapping("/task-total-info")
    public Map<String, Object> getTaskTotalInfo(
            @RequestParam(value = "taskId", required = false) Integer taskId,
            @RequestParam(value = "projectId", required = false) Integer projectId) {
        return taskServiceJJW.getTaskInitData(taskId, projectId);
    }

    //업무 상세 조회
//    @ResponseBody
//    @GetMapping("/task/{taskId}")
//    public TaskReqDtoJJW getTaskById(@PathVariable("taskId")Integer taskId){
//        return  taskServiceJJW.getTaskById(taskId);
//    }
//
//    //프로젝트 조회
//    @ResponseBody
//    @GetMapping("/projectList")
//    public List<ProjectDtoJJW> list(){
//        return  taskServiceJJW.getProjectList();
//    }
//
//    //프로젝트 상세 조회
//    @ResponseBody
//    @GetMapping("/projectDetail/{projectId}")
//    public ProjectDtoJJW projectDetail(@PathVariable("projectId") Integer projectId) {
//        return taskServiceJJW.getProjectDetail(projectId);
//    }
//    //업무 상태 조회
//    @ResponseBody
//    @GetMapping("/taskStatus")
//    public  List<TaskStatusDtoJJW> list1(){
//        return  taskServiceJJW.getTaskStatus();
//    }
//
//    //업무 유형 조회
//    @ResponseBody
//    @GetMapping("/taskType")
//    public List<TaskTypeDtoJJW> list2(){
//        return  taskServiceJJW.getTaskType();
//    }
//
//    //담당자 조회
//    @ResponseBody
//    @GetMapping("/taskUser")
//    public List<UserDtoJJW> list3(){
//        return  taskServiceJJW.getUserList();
//    }
//
//    //마일스톤 조회
//    @ResponseBody
//    @GetMapping("/taskMileStone")
//    public  List<MilestoneDtoJJW> list4(@RequestParam("projectId") Integer projectId){
//        return taskServiceJJW.getMilestone(projectId);
//    }


}

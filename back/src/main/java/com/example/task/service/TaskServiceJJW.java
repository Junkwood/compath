package com.example.task.service;

import com.example.task.dto.TaskReqDtoJJW;


public interface TaskServiceJJW {
    //상위 업무 생성(등록)
    void insert(TaskReqDtoJJW dto);

}

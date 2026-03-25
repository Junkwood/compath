package com.example.task.mapper;

import com.example.task.dto.TaskReqDtoJJW;

public interface TaskMapperJJW {

    //업무 생성(등록)
    void insert(TaskReqDtoJJW vo);
}

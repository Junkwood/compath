package com.example.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Data
public class UserDtoJJW {
    // 담당자 선택 팝업용
    private Long userId;
    private String userName;
    private String userType;
    private String roleName;
}

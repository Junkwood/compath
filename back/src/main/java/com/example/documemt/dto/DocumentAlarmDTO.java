package com.example.documemt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentAlarmDTO { // 문서

    //    문서 알림
    private Integer notificationId;
    private Integer userId;
    private String userName;
    private Integer roleId;
    private String roleName;

    private String notificationType;
    private String targetType;
    private Integer targetId;
    private String title;
    private String Message;
    private Integer created_by;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

//    notification_target
    private Integer receiverId;

}
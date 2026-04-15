package com.example.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto { //알림
    // notifications 테이블
    private Integer notificationId;
    private String notificationType;
    private String targetType;
    private Integer targetId;
    private String title;
    private String message;
    private Integer createdBy;
    private Integer projectId;
    private LocalDateTime createdAt;
    //notification_targets 테이블
    private Integer notificationTargetNo;
    private Integer  receiverId;
    private String isRead;
    private LocalDateTime readAt;


    //알림 받을 사람들
    private List<Integer> receiverList;

}

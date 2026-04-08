package com.example.alarm.mapper;

import com.example.alarm.dto.NotificationDto;

import java.util.List;

public interface NotificationMapper {
    // 알림 Register
    void registerNotification(NotificationDto dto);

    // 알림 수신자 Register
    void registerNotificationTarget(NotificationDto dto);

    // 특정 유저 알림 목록 조회
    List<NotificationDto> getNotificationsByUserId(Integer receiverId);

    // 안 읽은 알림 개수
    int countUnread(Integer receiverId);

    // 단건 읽음 처리
    void modifyReadOne(Integer notificationTargetNo);

    // 전체 읽음 처리
    void modifyReadAll(Integer receiverId);

}

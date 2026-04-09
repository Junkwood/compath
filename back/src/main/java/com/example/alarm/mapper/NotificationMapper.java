package com.example.alarm.mapper;

import com.example.alarm.dto.NotificationDto;

import java.util.List;
import java.util.Map;

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

    //알림 받을 사람 프로시저
    void getNotificationReceivers(Map<String, Object> params);

    //전체 사람들에게 알림 보내기
    List<Integer> getAllUserIds();

    //프로젝트 별 전체 사용자
    List<Integer> getProjectMemberIds(int projectId);



}

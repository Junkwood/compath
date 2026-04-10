package com.example.alarm.service.impl;

import com.example.alarm.SseEmitterManager;
import com.example.alarm.dto.NotificationDto;
import com.example.alarm.mapper.NotificationMapper;
import com.example.alarm.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final SseEmitterManager sseEmitterManager;


    // 프로젝트 멤버 전체에게 전송 (수신자 포함)
    @Override
    public void sendToProjectMembers(int projectId, int assigneeId,
                                     String type, int targetId,
                                     String title, String message, int createdBy) {
        Map<String, Object> params = new HashMap<>();
        params.put("projectId", projectId);
        params.put("assigneeId", assigneeId);
        notificationMapper.getNotificationReceivers(params);
        List<Integer> receivers = (List<Integer>) params.get("receiverList");

        // 루프 밖으로 이동
        NotificationDto dto = NotificationDto.builder()
                .notificationType(type)
                .targetType(type)
                .targetId(targetId)
                .title(title)
                .message(message)
                .createdBy(createdBy)
                .build();
        notificationMapper.registerNotification(dto);

        // target만 루프
        for (Integer receiverId : receivers) {
            dto.setReceiverId(receiverId);
            notificationMapper.registerNotificationTarget(dto);
            sseEmitterManager.sendToUser(receiverId, dto);
        }
    }

    @Override
    public void sendToAllProjectMembers(int projectId, String type, int targetId,
                                        String title, String message, int createdBy) {
        List<Integer> receivers = notificationMapper.getProjectMemberIds(projectId);

        NotificationDto dto = NotificationDto.builder()
                .notificationType(type)
                .targetType(type)
                .targetId(targetId)
                .title(title)
                .message(message)
                .createdBy(createdBy)
                .build();
        notificationMapper.registerNotification(dto);

        for (Integer receiverId : receivers) {
            if (receiverId == createdBy) continue; // 작성자 제외
            dto.setReceiverId(receiverId);
            notificationMapper.registerNotificationTarget(dto);
            sseEmitterManager.sendToUser(receiverId, dto);
        }
    }

    // 단일 수신자에게 전송
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendToOne(int receiverId, String type, int targetId,
                          String title, String message, int createdBy) {
        NotificationDto dto = NotificationDto.builder()
                .notificationType(type)
                .targetType(type)
                .targetId(targetId)
                .title(title)
                .message(message)
                .receiverId(receiverId)
                .createdBy(createdBy)
                .build();
        notificationMapper.registerNotification(dto);
        notificationMapper.registerNotificationTarget(dto);
        sseEmitterManager.sendToUser(receiverId, dto);
    }


    //알림 등록
    @Override
    public void registerNotification(NotificationDto dto) {
        notificationMapper.registerNotification(dto);
    }

    //알림 수신자 등록
    @Override
    public void registerNotificationTarget(NotificationDto dto) {
        notificationMapper.registerNotificationTarget(dto);
        sseEmitterManager.sendToUser(dto.getReceiverId(), dto);
    }

    //특정 유저 알림 목록 조회
    @Override
    public List<NotificationDto> getNotificationsByUserId(Integer receiverId) {
        return notificationMapper.getNotificationsByUserId(receiverId);
    }

    // 안 읽은 알림 개수
    @Override
    public int countUnread(Integer receiverId) {
        return notificationMapper.countUnread(receiverId);
    }

    // 단건 읽음 처리
    @Override
    public void modifyReadOne(Integer notificationTargetNo) {
        notificationMapper.modifyReadOne(notificationTargetNo);
    }

    // 전체 읽음 처리
    @Override
    public void modifyReadAll(Integer receiverId) {
        notificationMapper.modifyReadAll(receiverId);
    }

    //전체 사용자
    @Override
    public void sendToAllUsers(String type, int targetId, String title, String message, int createdBy) {
        List<Integer> allUsers = notificationMapper.getAllUserIds();

        NotificationDto dto = NotificationDto.builder()
                .notificationType(type)
                .targetType(type)
                .targetId(targetId)
                .title(title)
                .message(message)
                .createdBy(createdBy)
                .build();
        notificationMapper.registerNotification(dto); // 루프 밖에서 1번만

        for (Integer receiverId : allUsers) {
            if (receiverId == createdBy) continue; // 작성자 본인 제외
            dto.setReceiverId(receiverId);
            notificationMapper.registerNotificationTarget(dto);
            sseEmitterManager.sendToUser(receiverId, dto);
        }
    }
}
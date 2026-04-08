package com.example.alarm.service.impl;

import com.example.alarm.SseEmitterManager;
import com.example.alarm.dto.NotificationDto;
import com.example.alarm.mapper.NotificationMapper;
import com.example.alarm.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final SseEmitterManager sseEmitterManager;

    //알림 등록
    @Override
    public void registerNotification(NotificationDto dto) {
        notificationMapper.registerNotification(dto);
    }

    //알림 수신자 등록
    @Override
    public void registerNotificationTarget(NotificationDto dto) {
        notificationMapper.registerNotificationTarget(dto);
        // DB저장 후 해당 유저가 접속중이면 실시간 전송
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
}

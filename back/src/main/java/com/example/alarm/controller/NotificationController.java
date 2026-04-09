package com.example.alarm.controller;

import com.example.alarm.SseEmitterManager;
import com.example.alarm.dto.NotificationDto;
import com.example.alarm.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NotificationController {
    private final NotificationService notificationService;
    private final SseEmitterManager sseEmitterManager;

    // SSE 연결
    @GetMapping(value = "/notifications/subscribe/{userId}",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Integer userId) {
        return sseEmitterManager.createEmitter(userId);
    }

    //알림 등록
    @PostMapping("/notifications/register")
    public NotificationDto registerNotification(@RequestBody NotificationDto dto) {
        notificationService.registerNotification(dto);
        notificationService.registerNotificationTarget(dto);
        return dto;
    }

    //알림 목록 조회
    @GetMapping("/notifications/list/{userId}")
    public List<NotificationDto> Notification(@PathVariable Integer userId){
        return notificationService.getNotificationsByUserId(userId);
    }

    // 안 읽은 알림 개수
    @GetMapping("/notifications/unread/{userId}")
    public int countUnread(@PathVariable Integer userId) {
        return notificationService.countUnread(userId);
    }

    // 단건 읽음 처리
    @PutMapping("/notifications/read")
    public void readOne(@RequestBody NotificationDto dto) {
        notificationService.modifyReadOne(dto.getNotificationTargetNo());
    }

    // 전체 읽음 처리
    @PutMapping("/notifications/read-all")
    public void readAll(@RequestBody NotificationDto dto) {
        notificationService.modifyReadAll(dto.getReceiverId());
    }


}

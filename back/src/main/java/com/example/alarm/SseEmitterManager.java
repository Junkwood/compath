package com.example.alarm;

import com.example.alarm.dto.NotificationDto;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//component
@Component
public class SseEmitterManager {
    // 현재 접속중인 유저들의 SSE 연결을 저장
    private final Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();

    // 회원이 접속하면 SSE 연결 생성
    public SseEmitter createEmitter(Integer userId) {

        // 30분짜리 연결 생성
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);
        emitters.put(userId, emitter);

        // 연결 끊기면 Map에서 제거
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));

        // 연결 직후 더미 이벤트 전송
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("연결 완료"));
        } catch (IOException e) {
            emitters.remove(userId);
        }

        return emitter;
    }

    // 특정 유저한테 알림 전송
    public void sendToUser(Integer userId, NotificationDto dto) {
        SseEmitter emitter = emitters.get(userId);

        if (emitter != null) {  // 유저가 지금 접속 중일 때만
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(dto));
            } catch (IOException e) {
                emitters.remove(userId);
            }
        }
    }

}

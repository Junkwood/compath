package com.example.attachment.controller;

import com.example.alarm.SseEmitterManager;
import com.example.alarm.dto.NotificationDto;
import com.example.alarm.service.NotificationService;
import com.example.attachment.dto.AttachmentDTO;
import com.example.attachment.service.AttachmentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class attachmentController {

    private final AttachmentService service;

    @Value("${file.upload-dir}") // application.yml의 C:/uploads/
    private String baseDir;

    @GetMapping("/attachment/download/resp")
    public void downloadFile(@RequestParam("fullPath") String fullPath, HttpServletResponse response) throws  IOException {

        Path filePath = Paths.get(baseDir).resolve(fullPath).normalize();


        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "파일이 존재하지 않습니다.");
            return;
        }

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            // 타입을 알 수 없는 경우에만 기본값 설정
            contentType = "application/octet-stream";
        }

        // 파일명 한글/특수문자 처리
        String encodedFilename = URLEncoder.encode(filePath.getFileName().toString(), "UTF-8").replaceAll("\\+", "%20");

        // 응답 헤더 설정
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFilename + "\"");
        response.setContentLengthLong(Files.size(filePath));

        // 파일 스트림 전송
        try (InputStream is = Files.newInputStream(filePath); OutputStream os = response.getOutputStream()) {

            byte[] buffer = new byte[8192]; // 8KB 버퍼
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        }
    }

}

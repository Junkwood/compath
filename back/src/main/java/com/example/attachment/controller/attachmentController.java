package com.example.attachment.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.example.attachment.dto.AttachmentDTO;
import com.example.attachment.mapper.AttachmentMapper;
import com.example.attachment.service.AttachmentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class attachmentController {

    private final AttachmentService service;
    private final AttachmentMapper mapper;
    private final AmazonS3 S3Config;

    @Value("${cloud.aws.s3.bucket}") // application.yml에 정의한 버킷명
    private String bucket;

    @GetMapping("/attachment/download/resp/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Integer id, HttpServletRequest request, HttpServletResponse response )throws  IOException {

        AttachmentDTO file = mapper.getFile(id);

        String s3Key = file.getFilePath(); // DB의 FILE_PATH 컬럼 값
        String downloadFileName = file.getFileName(); // DB의 FILE_NAME 컬럼 값
        if (s3Key != null && s3Key.startsWith("http")) {
            s3Key = s3Key.substring(s3Key.lastIndexOf(bucket) + bucket.length() + 1);
        }

        try{
            // 3. S3의 데이터 스트림을 바로 연결 (메모리 절약)
            String fileName = makeFileName(request, Objects.requireNonNullElse(downloadFileName, s3Key));
            S3Object s3Object = S3Config.getObject(bucket, s3Key);
            S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);

            // 파일을 스트리밍 방식으로 응답
            return new ResponseEntity<>(new InputStreamResource(objectInputStream), headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("파일 다운로드 실패: {}", e.getMessage());
            return null;
        }
    }

    private String makeFileName(HttpServletRequest request, String displayFileName) throws UnsupportedEncodingException {
        String header = request.getHeader("User-Agent");

        String encodedFilename = null;
        if (header.contains("MSIE")) {
            encodedFilename = URLEncoder.encode(displayFileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        } else if (header.contains("Trident")) {
            encodedFilename = URLEncoder.encode(displayFileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        } else if (header.contains("Chrome")) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < displayFileName.length(); i++) {
                char c = displayFileName.charAt(i);
                if (c > '~') {
                    sb.append(URLEncoder.encode("" + c, StandardCharsets.UTF_8));
                } else {
                    sb.append(c);
                }
            }
            encodedFilename = sb.toString();
        } else if (header.contains("Opera")) {
            encodedFilename = "\"" + new String(displayFileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + "\"";
        } else if (header.contains("Safari")) {
            encodedFilename = URLDecoder.decode("\"" + new String(displayFileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + "\"", StandardCharsets.UTF_8);
        } else {
            encodedFilename = URLDecoder.decode("\"" + new String(displayFileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + "\"", StandardCharsets.UTF_8);
        }

        return encodedFilename;

    }

    // 첨부파일 삭제
    @DeleteMapping("/attachment/remove")
    public List<AttachmentDTO> removeAttachment(@RequestBody AttachmentDTO dto) {
        return service.removeAttachment(dto);
    }

}

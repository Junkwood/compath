package com.example.attachment.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.attachment.dto.AttachmentDTO;
import com.example.attachment.mapper.AttachmentMapper;
import com.example.attachment.service.AttachmentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentMapper attachmentMapper;
    private final AmazonS3 S3Config;

    @Value("${cloud.aws.s3.bucket}") // application.yml에 정의한 버킷명
    private String bucket;

    @Override
    public int registerAttachments(List<MultipartFile> files, Integer id) throws IOException {
        int sharedGroupId = id;

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;



            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            String uuidName = UUID.randomUUID().toString() + extension;
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

            String cleanFileName = uuidName.replaceAll("\\s", "_");
            String s3Key = datePath + "/" + cleanFileName;

            log.info("제발 이번엔! s3Key: [{}]", s3Key);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            try (InputStream inputStream = file.getInputStream()) {
                S3Config.putObject(new PutObjectRequest(bucket, s3Key, inputStream, metadata));
            }

            // 3. DB 저장을 위한 dto 세팅
            AttachmentDTO dto = new AttachmentDTO();
            dto.setFileName(file.getOriginalFilename()); // 원본파일명
            dto.setFilePath(S3Config.getUrl(bucket, s3Key).toString()); // S3 전체 URL 저장
            dto.setFileExt(originalName.substring(originalName.lastIndexOf(".") + 1));
            dto.setAttachmentGroupId(sharedGroupId);

            attachmentMapper.registerAttachments(dto);

            // 5. 첫 번째 파일 저장 후 생성된 그룹 ID를 변수에 담아 다음 파일들과 공유
            if (sharedGroupId == 0) {
                sharedGroupId = dto.getAttachmentGroupId();
            }
        }
        return sharedGroupId;
    }

    @Override
    public List<AttachmentDTO> getFileList(Integer id) {
        return attachmentMapper.getFileList(id);
    }

    @Override
    public List<AttachmentDTO> removeAttachment(AttachmentDTO dto) {

        String fileUrl = dto.getFilePath();
        String s3Key = extractKeyFromUrl(fileUrl);

        // 첨부파일 삭제
        int id = dto.getAttachmentId();
        attachmentMapper.removeAttachment(id);

        // 첨부파일 조회
        int gId = dto.getAttachmentGroupId();
        return attachmentMapper.getFileList(gId);
    }

    private String extractKeyFromUrl(String url) {
        return url.substring(url.lastIndexOf(".com/") + 5);
    }
}
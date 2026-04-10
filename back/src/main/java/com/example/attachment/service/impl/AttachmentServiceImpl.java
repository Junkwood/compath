package com.example.attachment.service.impl;

import com.example.attachment.dto.AttachmentDTO;
import com.example.attachment.mapper.AttachmentMapper;
import com.example.attachment.service.AttachmentService;
import com.example.common.util.FileUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentMapper attachmentMapper;

    @Value("${file.upload-dir}") // application.yml의 C:/uploads/
    private String baseDir;

    @Override
    public int registerAttachments(List<MultipartFile> files) throws IOException {
        int sharedGroupId = 0;

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            // 1. FileUtils를 사용하여 날짜 폴더 + UUID 파일명 계획 생성
            FileUtils.FilePlan plan = FileUtils.dateFolderWithUuidName(baseDir, file.getOriginalFilename());

            // 2. 물리적 파일 저장 (fullPath 사용)
            file.transferTo(plan.getFullPath().toFile());

            // 3. DB 저장을 위한 dto 세팅
            AttachmentDTO dto = new AttachmentDTO();
            dto.setFileName(file.getOriginalFilename()); // 원본파일명

            // DB에는 baseDir을 제외한 상대 경로만 저장하는 것이 관리상 유리합니다.
            // 예: 2026/04/10/uuid.png
            String relativePath = Paths.get(baseDir).relativize(plan.getFullPath()).toString().replace("\\", "/");
            dto.setFilePath(relativePath);

            // 확장자 추출
            String originalName = file.getOriginalFilename();
            dto.setFileExt(originalName.substring(originalName.lastIndexOf(".") + 1));

            // 그룹 ID 설정 (처음엔 0 전달 -> selectKey가 생성해줌)
            dto.setAttachmentGroupId(sharedGroupId);

            // 4. Mapper 호출
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
}
package com.example.attachment.service;

import com.example.attachment.dto.AttachmentDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface AttachmentService {

//    첨부파일 등록
    public int registerAttachments(List<MultipartFile> files, Integer id) throws IOException;


    //    첨부파일 조회
    public List<AttachmentDTO> getFileList(Integer id);

    //    첨부파일 삭제
    public List<AttachmentDTO> removeAttachment(AttachmentDTO dto);
}
package com.example.attachment.mapper;

import com.example.attachment.dto.AttachmentDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Mapper
public interface AttachmentMapper {

//    첨부파일 등록
    public int registerAttachments(AttachmentDTO dto);

//    첨부파일 조회
    public List<AttachmentDTO> getFileList(Integer id);
}
package com.example.document.mapper;

import com.example.document.dto.DocumentAlarmDTO;
import com.example.document.dto.DocumentCommentDTO;
import com.example.document.dto.DocumentDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DocumentMapper {

// 문서 등록
  public int registerDocument(DocumentDTO dto);

//  문서 단건 조회
  public DocumentDTO getDocumentById(Integer id);

//  문서 수정
  public int modifyDocument(DocumentDTO dto);

//  문서 작성자 목록(필터링)
  public List<DocumentDTO> getFilterUser(Integer id);

//  문서 카테고리 목록(필터링)
  public List<DocumentDTO> getFilterCategroy(Integer id);

  //  문서 전체목록
  public List<DocumentDTO> getAllDocuments(DocumentDTO dto);

  // 문서 댓글 등록
  public int registerComment(DocumentCommentDTO dto);

  // 문서별 댓글 조회
  public List<DocumentCommentDTO> getAllComments(Integer id);

  //  문서 댓글 수정
  public int modifyComment(DocumentCommentDTO dto);

  // 문서 알림 테이블 등록
  public int registerCommentAlarm(DocumentAlarmDTO dto);

  // 문서 알림 대상테이블 등록
  public int registerAlarmTarget(DocumentAlarmDTO dto);

  // 문서 알림 테이블 조회
  public int getDocumentAlarmById(Integer targetId);
}
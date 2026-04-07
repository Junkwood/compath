package com.example.documemt.mapper;

import com.example.documemt.dto.DocumentDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DocumentMapper {
//  문서 테이블 최고 번호
  public int getLastNum();

// 문서 등록
  public int registerDocument(DocumentDTO dto);

//  문서 단건 조회
  public DocumentDTO getDocumentById(Integer id);

//  공지사항 수정
  public int modifyDocument(DocumentDTO dto);

//  공지사항 작성자 목록(필터링)
  public List<DocumentDTO> getFilterUser(Integer id);

//  공지사항 카테고리 목록(필터링)
  public List<DocumentDTO> getFilterCategroy(Integer id);

  //  공지사항 전체목록
  public List<DocumentDTO> getAllDocuments(DocumentDTO dto);

//  공지사항 비활성
  public int modifyNoticeLock(String isDeleted,Integer noticeId);
}
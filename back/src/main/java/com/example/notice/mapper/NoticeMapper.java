package com.example.notice.mapper;

import com.example.notice.dto.NoticeDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NoticeMapper {
//  공지사항 테이블 최고 번호
  public int getLastNum();

// 공지사항 등록
  public int registerNotice(NoticeDTO dto);

//  공지사항 단건 조회
  public NoticeDTO getNoticeById(Integer id);

//  공지사항 수정
  public int modifyNotice(NoticeDTO dto);

//  공지사항 작성자 목록(필터링)
  public List<NoticeDTO> getFilterUser(Integer id);

//  공지사항 카테고리 목록(필터링)
  public List<NoticeDTO> getFilterCategroy(Integer id);

  //  공지사항 전체목록
  public List<NoticeDTO> getAllNotices(NoticeDTO dto);

//  공지사항 비활성
  public int modifyNoticeLock(String isDeleted,Integer noticeId);
}
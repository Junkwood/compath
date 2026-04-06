package com.example.notice.mapper;

import com.example.notice.dto.NoticeDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeMapper {
//  공지사항 테이블 최고 번호
  public int getLastNum();

// 공지사항 등록
  public int registerNotice(NoticeDTO dto);

//  공지사항 단건 조회
  public NoticeDTO getNoticeById(Integer id);
}
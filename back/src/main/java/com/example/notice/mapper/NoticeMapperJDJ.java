package com.example.notice.mapper;

import com.example.notice.dto.NoticeDtoJDJ;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NoticeMapperJDJ {
    public List<NoticeDtoJDJ> getNoticeListByPid(Integer projectId);
}

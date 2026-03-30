package com.example.memo.mapper;

import com.example.memo.dto.MemoCreateDto;
import com.example.memo.dto.MemoListDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemoMapper {
    //메모 등록
    int registerMemo(MemoCreateDto dto);

    //메모리스트 불러오기
    public List<MemoListDto> getMemoList(
            @Param("userId") int userId,
            @Param("projectId") int projectId
    );
}

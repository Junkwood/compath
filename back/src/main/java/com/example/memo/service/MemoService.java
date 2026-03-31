package com.example.memo.service;

import com.example.memo.dto.MemoCreateDto;
import com.example.memo.dto.MemoListDto;

import java.util.List;

public interface MemoService {
    //메모 등록
    void registerMemo(MemoCreateDto dto);

    //메모리스트 불러오기
    List<MemoListDto> getMemoList(int userId, int projectId);

    //메모 삭제 (상태값 Q2로 변경)
    void updateStatByMemoId(MemoCreateDto dto);

    //작성한 메모 수정하기
    void updateMemoContent(MemoCreateDto dto);
}

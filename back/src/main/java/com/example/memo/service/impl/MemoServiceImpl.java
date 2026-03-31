package com.example.memo.service.impl;

import com.example.memo.dto.MemoCreateDto;
import com.example.memo.dto.MemoListDto;
import com.example.memo.mapper.MemoMapper;
import com.example.memo.service.MemoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class MemoServiceImpl implements MemoService {

    private final MemoMapper memoMapper;

    //메모 등록
    @Override
    public void registerMemo(MemoCreateDto dto) {
        memoMapper.registerMemo(dto);
    }

    //메모리스트 불러오기
    @Override
    public List<MemoListDto> getMemoList(int userId, int projectId) {
        return memoMapper.getMemoList(userId, projectId);
    }

    //메모 삭제 (상태값 Q2로 변경)
    @Override
    public void updateStatByMemoId(MemoCreateDto dto) {
        memoMapper.updateStatByMemoId(dto);
    }

    //작성한 메모 수정하기
    @Override
    public void updateMemoContent(MemoCreateDto dto) {
        memoMapper.updateMemoContent(dto);
    }

}

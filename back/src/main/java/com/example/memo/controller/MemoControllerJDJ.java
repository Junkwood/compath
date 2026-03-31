package com.example.memo.controller;

import com.example.memo.dto.MemoCreateDto;
import com.example.memo.dto.MemoListDto;
import com.example.memo.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemoControllerJDJ {

    final MemoService memoService;

    //메모 등록
    @PostMapping("/MemoRegister")
    public MemoCreateDto registerMemo(@RequestBody MemoCreateDto dto){
        memoService.registerMemo(dto);
        return dto;
    }

    //메모리스트 불러오기
    @GetMapping("/MemoList/{projectId}")
    public List<MemoListDto> mlist(@PathVariable Integer projectId,
                                   @RequestParam Integer userId){
        return memoService.getMemoList(userId, projectId);
    }

    //메모 삭제 (상태값 Q2로 변경)
    @PostMapping("/MemoStatUpdate")
    public MemoCreateDto updateStatByMemoId(@RequestBody MemoCreateDto dto){
        memoService.updateStatByMemoId(dto);
        return dto;
    }

    //작성한 메모 수정하기
    @PostMapping("/MemoContentUpdate")
    public MemoCreateDto updateMemoContent(@RequestBody MemoCreateDto dto){
        memoService.updateMemoContent(dto);
        return dto;
    }
}

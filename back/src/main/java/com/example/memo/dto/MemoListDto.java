package com.example.memo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemoListDto {
    private Integer memoId;
    private Integer projectId;
    private Integer userId;
    private String memoContent;

}

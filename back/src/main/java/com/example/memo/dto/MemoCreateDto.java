package com.example.memo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemoCreateDto {
    private Integer memoId;
    private Integer projectId;
    private Integer userId;
    private String memoContent;
    private LocalDateTime createdAt;
}

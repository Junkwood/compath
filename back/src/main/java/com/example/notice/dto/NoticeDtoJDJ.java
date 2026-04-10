package com.example.notice.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class NoticeDtoJDJ {
    private String title;
    private LocalDateTime createdAt;
    private String isPinned;
}

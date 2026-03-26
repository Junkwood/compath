package com.example.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Data
public class MilestoneDtoJJW {
    private Long milestoneId;
    private  String milestoneName;
}

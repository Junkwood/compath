package com.example.milestone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MilestoneDtoJJW {
    //마일스톤
    private Integer milestoneId;
    private  String milestoneName;

    //마일스톤 매핑
    private Integer milestoneMappingId;
    private Integer projectId;
}

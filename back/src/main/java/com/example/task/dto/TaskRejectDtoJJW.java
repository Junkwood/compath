package com.example.task.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class TaskRejectDtoJJW {
    Integer taskRejectionId;
    Integer taskId;
    String rejectionReason;
    Integer rejectedBy;
    LocalDateTime rejectionDate;
}

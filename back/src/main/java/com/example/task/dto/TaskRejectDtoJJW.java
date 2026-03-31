package com.example.task.dto;

import java.time.LocalDateTime;

public class TaskRejectDtoJJW {
    Integer taskRejectionId;
    Integer taskId;
    String rejectionReason;
    Integer rejectedBy;
    LocalDateTime rejectionDate;
}

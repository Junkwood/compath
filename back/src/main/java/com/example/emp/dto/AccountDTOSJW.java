package com.example.emp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTOSJW {
    private Integer userId;
    private String email;
    private String password;
    private Integer emailId;
    private String emailNo;
}

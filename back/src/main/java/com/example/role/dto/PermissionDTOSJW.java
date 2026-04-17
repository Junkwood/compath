package com.example.role.dto; // 💡 실제 프로젝트 패키지 경로에 맞게 수정하세요!

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionDTOSJW {
    private Integer rolePermissionId;
    private Integer roleId;
    private Integer permissionId;      // 권한 PK (1, 2, 3...)
    private String permissionName;     // 권한명 (예: 상위프로젝트 조회)
    private Integer permissionGroupId;
    private String permissionGroupName;
    private String description;        // 권한 설명
    private String permissionUrl;      // 매핑 URL (예: /api/upper-projects)
    private String permissionMethod;   // HTTP 메서드 (예: GET, POST)
    private String createdAt;          // 생성일
    private Integer groupPermissionId;// 대표그룹 퍼미션 테이블용 아이디

}
package com.example.permission.mapper;

import org.apache.ibatis.annotations.Param;

public interface PermissionMapper {
    boolean isUrlRegisteredInDb(@Param("urlPattern") String urlPattern, @Param("method") String method);

    // 유저가 해당 프로젝트에서 권한을 가졌는지 확인 (어제 확정한 그 쿼리)
    int checkUserPermission(@Param("userId") String userId, @Param("projectId") Integer projectId, @Param("method") String method, @Param("url") String url);
}

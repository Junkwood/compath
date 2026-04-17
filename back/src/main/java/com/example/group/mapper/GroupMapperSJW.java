package com.example.group.mapper;

import com.example.emp.entity.EmpVOSJW;
import com.example.group.dto.GroupDTOSJW;
import com.example.group.dto.GroupLogsDTOSJW;
import com.example.group.entity.GroupVOSJW;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupMapperSJW {
    List<GroupVOSJW> getAll();
    GroupDTOSJW getById(Integer id);
    Integer registerGroup(GroupDTOSJW group);
    List<GroupDTOSJW>getAllGroupForEmpRegister();
    Integer checkDuplicatedName(String name);
    List<GroupLogsDTOSJW> getGroupActivityLogs(Integer id);
    Integer modifyGroup(GroupDTOSJW group);
    // 그룹의 활성 멤버 조회
    List<EmpVOSJW> getMembersByGroupId(Integer groupId);

    // 구성원 추가 (새 멤버 & 역할 바뀐 멤버)
    void insertGroupMember(@Param("groupId") Integer groupId,
                           @Param("userId") Integer userId,
                           @Param("roleId") Integer roleId,
                           @Param("editorUserId") Integer editorUserId);
    // 구성원 추가 (새 멤버 & 역할 바뀐 멤버)
    void updateGroupMemberRole(@Param("groupId") Integer groupId,
                           @Param("userId") Integer userId,
                           @Param("roleId") Integer roleId,
                           @Param("editorUserId") Integer editorUserId);

    // [제외] 구성원 완전 삭제 (Hard Delete)
    void deleteGroupMember(@Param("groupId") Integer groupId,
                           @Param("userId") Integer userId);

    // ==========================================
    // 4. 수동 로그 기록용
    // ==========================================

    // [로그] 구성원 제외 수동 기록 (DELETE 직전에 Java에서 호출)
    void insertDeleteLog(@Param("groupId") String groupId,
                         @Param("logBeforeValue") Integer logBeforeValue,
                         @Param("editorUserId") Integer editorUserId);

    void deletePermissionsByGroupId(@Param("groupId") Integer groupId);
    void insertGroupPermissions(@Param("groupId") Integer groupId, @Param("permissionIds") List<Integer> permissionIds);
}
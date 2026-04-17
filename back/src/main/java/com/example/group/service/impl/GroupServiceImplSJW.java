package com.example.group.service.impl;

import com.example.emp.entity.EmpVOSJW;
import com.example.emp.mapper.EmpMapperSJW;
import com.example.group.dto.GroupDTOSJW;
import com.example.group.entity.GroupVOSJW;
import com.example.group.mapper.GroupMapperSJW;
import com.example.group.service.GroupServiceSJW;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImplSJW implements GroupServiceSJW {
    private final GroupMapperSJW groupMapper;
    private final EmpMapperSJW empMapperSJW;

    @Override
    public List<GroupVOSJW> getAll() {
        return groupMapper.getAll();
    }

    @Override
    public List<GroupDTOSJW> getAllGroupForEmpRegister() {
        return groupMapper.getAllGroupForEmpRegister();
    }

    @Override
    public GroupDTOSJW getById(Integer id) {
        GroupDTOSJW groupDetail = groupMapper.getById(id);
        if (groupDetail != null) {
            groupDetail.setLogs(groupMapper.getGroupActivityLogs(id));
        }
        return groupDetail;
    }


    @Override
    public String checkDuplicatedName(String name) {
        Integer result = groupMapper.checkDuplicatedName(name);
        if (result <= 0) {
            return "Y";
        }
        return "N";
    }

    @Override
    @Transactional
    public String registerGroup(GroupDTOSJW group) {
        Integer result = groupMapper.registerGroup(group);
        if (result > 0) {
            group.getMembers().forEach(item -> {
                EmpVOSJW emp = new EmpVOSJW();
                emp.setUserId(item.getUserId());
                emp.setGroupId(group.getGroupId());
                emp.setIsPrimary("N");
                emp.setRoleId(item.getRoleId());
                empMapperSJW.insertGroupMember(emp);
            });
            if (group.getPermissionIds() != null && !group.getPermissionIds().isEmpty()) {
                groupMapper.insertGroupPermissions(group.getGroupId(), group.getPermissionIds());
            }
            return "Y";
        }
        return "N";
    }

    @Override
    @Transactional
    public GroupDTOSJW modifyGroup(GroupDTOSJW group) {

        // 1. 그룹 기본 정보 수정
        groupMapper.modifyGroup(group);
        if (group.getMembers() != null) {
            if (group.getMembers().size() <= 0) {
                return null;
            }
            // 권한 리스트가 파라미터로 넘어왔다면 (비어있는 배열 포함)
            if (group.getPermissionIds() != null) {
                // 해당 그룹의 기존 권한을 싹 다 지우고
                groupMapper.deletePermissionsByGroupId(group.getGroupId());

                // 새로 넘어온 권한이 1개라도 있다면 새로 Insert
                if (!group.getPermissionIds().isEmpty()) {
                    groupMapper.insertGroupPermissions(group.getGroupId(), group.getPermissionIds());
                }
            }
            // 2. DB에 있던 '기존 멤버' 조회 (💡 주의: Mapper에서 is_active = 'Y'인 사람만 가져와야 함)
            List<EmpVOSJW> oldMembers = groupMapper.getMembersByGroupId(group.getGroupId());


            Map<Integer, EmpVOSJW> oldMemberMap = oldMembers.stream()
                    .collect(Collectors.toMap(
                            EmpVOSJW::getUserId,
                            m -> m,
                            (existing, replacement) -> existing // 중복 키가 들어오면 기존 것을 유지
                    ));

            // 💡 새로 넘어온 멤버 리스트를 Map으로 바꿀 때 중복 방어
            Map<Integer, EmpVOSJW> newMemberMap = group.getMembers().stream()
                    .collect(Collectors.toMap(
                            EmpVOSJW::getUserId,
                            m -> m,
                            (existing, replacement) -> existing //
                    ));

            // 3. 추가 및 역할 변경(비활성화 후 INSERT) 처리
            // ... [이전 코드 동일] ...

            Integer editorUserId = group.getEditorUserId();

// 1. 추가 및 역할 변경 (INSERT & UPDATE)
            for (EmpVOSJW newMember : group.getMembers()) {
                EmpVOSJW oldMember = oldMemberMap.get(newMember.getUserId());

                if (oldMember == null) {
                    // 새로 추가 (트리거가 MA 로그 자동 생성)
                    groupMapper.insertGroupMember(group.getGroupId(), newMember.getUserId(), newMember.getRoleId(), editorUserId);
                } else {
                    // 역할 변경 여부 확인 (트리거가 MR 로그 자동 생성)
                    if (!Objects.equals(oldMember.getRoleId(), newMember.getRoleId())) {
                        groupMapper.updateGroupMemberRole(group.getGroupId(), newMember.getUserId(), newMember.getRoleId(), editorUserId);
                    }
                }
            }

// 2. 제외된 구성원 처리 (DELETE)
            for (EmpVOSJW oldMember : oldMembers) {
                if (!newMemberMap.containsKey(oldMember.getUserId())) {

                    // 💡 1. 동명이인 방지를 위해 "이름(사번)" 형태로 문자열 조립!
                    String logBeforeValue = oldMember.getUserName() + "(" + oldMember.getUserId() + ")";

                    // 💡 2. 조립된 문자열을 수동 로그 쿼리에 넘겨줍니다.
                    groupMapper.insertDeleteLog(
                            String.valueOf(group.getGroupId()),
                            oldMember.getUserId(), // 👈 "김유저(1002)" 형태로 들어감
                            editorUserId
                    );

                    // 💡 3. 실제 멤버 삭제
                    groupMapper.deleteGroupMember(group.getGroupId(), oldMember.getUserId());
                }
            }
        }
        return group;
    }


}
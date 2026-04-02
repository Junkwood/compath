package com.example.group.service.impl;

import com.example.emp.entity.EmpVOSJW;
import com.example.emp.mapper.EmpMapperSJW;
import com.example.group.dto.GroupDTOSJW;
import com.example.group.entity.GroupVOSJW;
import com.example.group.mapper.GroupMapperSJW;
import com.example.group.service.GroupServiceSJW;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
        if(result <= 0) {
            return "Y";
        }
        return "N";
    }

    @Override
    @Transactional
    public String registerGroup(GroupDTOSJW group) {
        Integer result = groupMapper.registerGroup(group);
        if(result > 0) {
            group.getMembers().forEach(item -> {
                EmpVOSJW emp = new EmpVOSJW();
                emp.setUserId(item.getUserId());
                emp.setGroupId(group.getGroupId());
                emp.setIsPrimary("N");
                emp.setRoleId(item.getRoleId());
                empMapperSJW.insertGroupMember(emp);
            });
            return "Y";
        }
        return "N";
    }

    @Override
    @Transactional
    public GroupDTOSJW modifyGroup(GroupDTOSJW group) {

        // 1. 그룹 기본 정보 수정
        groupMapper.modifyGroup(group);
        if(group.getMembers() != null ) {
        if(group.getMembers().size()<=0){return null;}

            // 2. DB에 있던 '기존 멤버' 조회 (💡 주의: Mapper에서 is_active = 'Y'인 사람만 가져와야 함)
            List<EmpVOSJW> oldMembers = groupMapper.getMembersByGroupId(group.getGroupId());


            Map<Integer, EmpVOSJW> oldMemberMap = oldMembers.stream()
                    .collect(Collectors.toMap(EmpVOSJW::getUserId, m -> m));

            Map<Integer, EmpVOSJW> newMemberMap = group.getMembers().stream()
                    .collect(Collectors.toMap(EmpVOSJW::getUserId, m -> m));

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

                    // 💡 핵심: 진짜로 지우기 전에 수동으로 제외(MD) 로그를 쏩니다!
                    groupMapper.insertDeleteLog(String.valueOf(oldMember.getGroupMemberId()), editorUserId);

                    // 그 다음 깔끔하게 삭제
                    groupMapper.deleteGroupMember(group.getGroupId(), oldMember.getUserId());
                }
            }
        }
        return group;
    }



}
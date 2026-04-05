package com.example.group.controller;

import com.example.group.dto.GroupDTOKJH;
import com.example.group.dto.GroupDTOSJW;
import com.example.group.entity.GroupVOSJW;
import com.example.group.service.GroupServiceKJH;
import com.example.group.service.GroupServiceSJW;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class GroupControllerKJH {
    private final GroupServiceKJH groupService;

    @GetMapping("/api/group/members")
    public List<GroupDTOKJH> getAll(@RequestParam(required = false) Integer id,
                                    @RequestParam(required = false) String name) {
        return groupService.getAllgroupMembers(id,name);
    }

    // 프로젝트 그룹 멤버들
    @GetMapping("/api/projectgroup/members")
    public List<GroupDTOKJH> getAllProjectMem() {
        return groupService.getProjectgroupMembers();
    }
}
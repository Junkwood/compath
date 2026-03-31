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

    @GetMapping("/api/group/members/{id}")
    public List<GroupDTOKJH> getAll(@PathVariable Integer id) {
        return groupService.getAllgroupMembers(id);
    }

}
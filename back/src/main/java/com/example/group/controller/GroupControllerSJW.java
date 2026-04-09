package com.example.group.controller;

import com.example.group.dto.GroupDTOSJW;
import com.example.group.entity.GroupVOSJW;
import com.example.group.service.GroupServiceSJW;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class GroupControllerSJW {
    private final GroupServiceSJW groupService;

    @GetMapping("/group")
    public List<GroupVOSJW> getAll() {
        return groupService.getAll();
    }

    @PostMapping("/group")
    public String register(@RequestBody GroupDTOSJW group) {
        return groupService.registerGroup(group);
    }

    @PutMapping("/group/{groupId}")
    public GroupDTOSJW update(@RequestBody GroupDTOSJW group,@PathVariable Integer groupId) {
        return groupService.modifyGroup(group);
    }

    @GetMapping("/group/active")
    public List<GroupDTOSJW> getAllGroupForEmpRegister() {
        return groupService.getAllGroupForEmpRegister();
    }

    @GetMapping("/group/{id}")
    public GroupDTOSJW getById(@PathVariable Integer id) {
        return groupService.getById(id);
    }

    @GetMapping("/group/dup/{name}")
    public String dup(@PathVariable String name) {
        return groupService.checkDuplicatedName(name);
    }
}
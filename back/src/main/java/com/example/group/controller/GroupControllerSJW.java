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
public class GroupControllerSJW {
    private final GroupServiceSJW groupService;

    @GetMapping("/api/group/list")
    public List<GroupVOSJW> getAll() {
        return groupService.getAll();
    }
    @GetMapping("/api/group/list/active")
    public List<GroupDTOSJW> getAllGroupForEmpRegister() {
        return groupService.getAllGroupForEmpRegister();
    }
    @GetMapping("/api/group/info/{id}")
    public GroupDTOSJW getById(@PathVariable Integer id) {
        return groupService.getById(id);
    }
    @GetMapping("/api/group/dup/{name}")
    public String dup(@PathVariable String name) {
        return groupService.checkDuplicatedName(name);
    }
    @PostMapping("/api/group")
    public String register(@RequestBody GroupDTOSJW group) {
        return groupService.registerGroup(group);
    }
    @PutMapping("/api/group")
    public String update(@RequestBody GroupDTOSJW group) {
        return groupService.modifyGroup(group);
    }
}
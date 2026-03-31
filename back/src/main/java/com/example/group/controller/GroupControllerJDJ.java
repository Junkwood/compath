package com.example.group.controller;

import com.example.group.dto.GroupMemListDtoJDJ;
import com.example.group.service.GroupServiceJDJ;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GroupControllerJDJ {

    final GroupServiceJDJ groupServiceJDJ;

    @GetMapping("/GroupMemList/{projectId}")
    public List<GroupMemListDtoJDJ> gmlist(@PathVariable Integer projectId){
        return groupServiceJDJ.getGroupMemList(projectId);
    }
}

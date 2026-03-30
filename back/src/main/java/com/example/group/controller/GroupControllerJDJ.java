package com.example.group.controller;

import com.example.group.dto.GroupMemListDtoJDJ;
import com.example.group.service.GroupServiceJDJ;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GroupControllerJDJ {

    final GroupServiceJDJ groupServiceJDJ;

    @GetMapping("/GroupMemList")
    public List<GroupMemListDtoJDJ> gmlist(){
        return groupServiceJDJ.getGroupMemList();
    }
}

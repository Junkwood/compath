package com.example.emp.controller;

import com.example.emp.dto.EmpDTOSJW;
import com.example.emp.entity.EmpVOKJH;
import com.example.emp.entity.EmpVOSJW;
import com.example.emp.service.EmpServiceKJH;
import com.example.emp.service.EmpServiceSJW;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
public class EmpControllerKJH {
    private final EmpServiceKJH service;

    @GetMapping("/api/emp/users/{id}")
    public EmpVOKJH getById(@PathVariable Integer id) {
        return service.getById(id);
    }


}
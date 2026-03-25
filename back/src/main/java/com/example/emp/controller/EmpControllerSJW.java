package com.example.emp.controller;

import com.example.emp.entity.EmpVOSJW;
import com.example.emp.service.EmpServiceSJW;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EmpControllerSJW {
    private final EmpServiceSJW empServiceSJW;

    @GetMapping("/api/emp/list")
    public List<EmpVOSJW> findAll() {
        return empServiceSJW.findAll();
    }
    @GetMapping("/api/emp/info/{id}")
    public EmpVOSJW findById(@PathVariable Integer id) {
        return empServiceSJW.findById(id);
    }
}
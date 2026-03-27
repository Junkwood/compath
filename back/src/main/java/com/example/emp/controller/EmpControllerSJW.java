package com.example.emp.controller;

import com.example.emp.dto.EmpDTOSJW;
import com.example.emp.entity.EmpVOSJW;
import com.example.emp.service.EmpServiceSJW;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class EmpControllerSJW {
    private final EmpServiceSJW empService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);

    @GetMapping("/api/emp/list")
    public List<EmpDTOSJW> getAll() {
        return empService.getAll();
    }
    @GetMapping("/api/emp/info/{id}")
    public EmpVOSJW getById(@PathVariable Integer id) {
        return empService.getById(id);
    }
    @GetMapping("/api/emp/status/{id}")
    public String modifyStatusById(@PathVariable Integer id) {
        return empService.modifyStatusById(id);
    }
    @PostMapping("/api/emp")
    public Integer register(@RequestBody EmpVOSJW emp) {
    return empService.registerEmp(emp);
    }
    @PostMapping("/api/login")
    public EmpVOSJW login(@RequestBody EmpVOSJW emp) {
        return  empService.login(emp);
    }
}
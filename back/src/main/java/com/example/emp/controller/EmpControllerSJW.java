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

    @GetMapping("/api/emp/list")
    public List<EmpDTOSJW> findAll() {
        return empService.findAll();
    }
    @GetMapping("/api/emp/info/{id}")
    public EmpVOSJW findById(@PathVariable Integer id) {
        return empService.findById(id);
    }
    @PostMapping("/api/login")
    public EmpVOSJW login(@RequestBody EmpVOSJW emp) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
        Integer id = emp.getUserId();
        String password = emp.getPassword();
        EmpVOSJW result = empService.findById(id);

        if(encoder.matches(password, result.getPassword())) {
            result.setPassword(null);
            return result;
        }
        else  {
            return null;
        }
    }
}
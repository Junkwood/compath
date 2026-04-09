package com.example.emp.controller;

import com.example.emp.dto.AccountDTOSJW;
import com.example.emp.dto.EmpDTOSJW;
import com.example.emp.entity.EmpVOSJW;
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
public class EmpControllerSJW {
    private final EmpServiceSJW empService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);

    @GetMapping("/admin/emp")
    public List<EmpDTOSJW> getAll() {
        return empService.getAll();
    }
    @PostMapping("/admin/emp")
    public Integer register(@RequestBody EmpVOSJW emp) {
    return empService.registerEmp(emp);
    }
    @GetMapping("/admin/emp/group")
    public List<EmpVOSJW> getAllForGroup() {
        return empService.getAllForGroup();
    }
    @GetMapping("/admin/emp/{id}")
    public EmpVOSJW getById(@PathVariable Integer id) {
        return empService.getById(id);
    }
    @PutMapping("/admin/emp/{id}/status")
    public String modifyStatusById(@PathVariable Integer id) {
        return empService.modifyStatusById(id);
    }
    @PutMapping("/admin/emp/{id}")
    public Boolean update(@RequestBody EmpVOSJW emp,@PathVariable Integer id) {
        return  empService.modifyEmpById(emp,id);
    }
    @PutMapping("/auth/emp/{id}")
    public Boolean chagePassword(@RequestBody EmpVOSJW emp,@PathVariable Integer id) {
        return  empService.modifyEmpById(emp,id);
    }
    @PostMapping("/auth/login")
    public EmpVOSJW login(@RequestBody EmpVOSJW emp) {
        return  empService.login(emp);
    }
    @PostMapping("/auth/email/sendCode")
    public ResponseEntity<Map<String, Object>> sendEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Integer userId = Integer.valueOf(request.get("userId"));
        Integer emailId = empService.sendEmail(email,userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("emailId", emailId);
        response.put("message", "인증번호가 발송되었습니다.");
        return ResponseEntity.ok(response);
    }
    @PostMapping("/auth/email/verifyCode")
    public ResponseEntity<Map<String, Object>> verifyCode(@RequestBody Map<String, String> request) {
        Integer code = Integer.valueOf( request.get("code"));
        Integer emailId = Integer.valueOf( request.get("emailId"));
        if(empService.verifyCode(code,emailId)){
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "인증이 완료되었습니다.");
            return ResponseEntity.ok(response);
        }else{
            Map<String, Object> response = new HashMap<>();
            response.put("error", true);
            response.put("message", "인증이 실패하였습니다.");
            return ResponseEntity.badRequest().body(response);
        }
    }

}
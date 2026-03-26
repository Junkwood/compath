package com.example.common.code.controller;

import com.example.common.code.service.CommonCodeService;
import com.example.common.code.service.CommonCodeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommonCodeController {
    final CommonCodeService commonCodeService;

    @ResponseBody
    @GetMapping("/{groupValue}") // Vue가 호출할 주소
    public List<CommonCodeVO> getCodes( @PathVariable String groupValue) {
        return commonCodeService.getCodeListByGroup(groupValue);
    }
}

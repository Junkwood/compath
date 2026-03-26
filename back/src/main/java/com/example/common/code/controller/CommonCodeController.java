package com.example.common.code.controller;

import com.example.common.code.service.CommonCodeService;
import com.example.common.code.service.CommonCodeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommonCodeController {
    final CommonCodeService commonCodeService;

    @ResponseBody
    @GetMapping("/code") // Vue가 호출할 주소
    public Map<String, List<CommonCodeVO>> getCodes( String[] groupValue) {//get json안됨
        Map<String, List<CommonCodeVO>> map = new HashMap<>();
        for(int i=0; i< groupValue.length; i++){
        map.put("c"+groupValue[i], commonCodeService.getCodeListByGroup(groupValue[i]));
        };
        return  map;
    }
}

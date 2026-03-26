package com.example.common.code.service.impl;

import com.example.common.code.mapper.CommonCodeMapper;
import com.example.common.code.service.CommonCodeService;
import com.example.common.code.service.CommonCodeVO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class CommonCodeServiceImpl implements CommonCodeService {

    final CommonCodeMapper commonCodeMapper;

    @Override
    public List<CommonCodeVO> getCodeListByGroup(String groupValue) {
        return commonCodeMapper.selectCodeListByGroup(groupValue);
    }
}

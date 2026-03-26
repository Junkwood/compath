package com.example.common.code.mapper;

import com.example.common.code.service.CommonCodeVO;

import java.util.List;

public interface CommonCodeMapper {
    List<CommonCodeVO> selectCodeListByGroup(String groupValue);
}

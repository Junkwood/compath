package com.example.common.code.service;

import java.util.List;

public interface CommonCodeService {
    List<CommonCodeVO> getCodeListByGroup(String groupValue);
}

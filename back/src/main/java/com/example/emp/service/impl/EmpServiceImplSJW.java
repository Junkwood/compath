package com.example.emp.service.impl;

import com.example.emp.dto.AccountDTOSJW;
import com.example.emp.dto.EmpDTOSJW;
import com.example.emp.entity.EmpVOSJW;
import com.example.emp.mapper.EmpMapperSJW;
import com.example.emp.service.EmpServiceSJW;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmpServiceImplSJW implements EmpServiceSJW {
    private final EmpMapperSJW empMapper;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
    private final JavaMailSender javaMailSender;
    @Override
    public List<EmpDTOSJW> getAll() {
        return empMapper.getAll();
    }
    @Override
    public List<EmpVOSJW> getAllForGroup() {
        return empMapper.getAllForGroup();
    }

    @Override
    public EmpVOSJW getById(Integer id) {
        return empMapper.getById(id);
    }

    @Override
    public String modifyStatusById(Integer id) {
        Map<String, Object> params = new HashMap<>();
        params.put("emp_id", id);
        empMapper.modifyStatusById(params);
        String result =(String) params.get("changed_status");
        if(result.equals("O1")) {
            return "Y";
        }else{
            return "N";
        }
    }

    @Override
    public Integer registerEmp(EmpVOSJW emp) {
        emp.setPassword(encoder.encode(emp.getPassword()));
        if(emp.getUserType().equals("ADMIN")) {
            emp.setUserType("M1");
        }else{
            emp.setUserType("M2");
        }
        empMapper.registerEmp(emp);
        emp.setIsPrimary("Y");
        empMapper.insertGroupMember(emp);
        return emp.getUserId();
    }

    @Override
    public EmpVOSJW login(EmpVOSJW emp) {
        Integer id = emp.getUserId();
        String password = emp.getPassword();
        EmpVOSJW result = this.getById(id);

        if(encoder.matches(password, result.getPassword())) {
            result.setPassword(null);
            return result;
        }
        else  {
            return null;
        }
    }
    @Transactional
    @Override
    public Boolean modifyEmpById(EmpVOSJW vo, Integer userId) {
        if(empMapper.getById(userId)==null){
            return false;
        }
    // 1. 유저 정보 업데이트
        if (vo.getPassword() != null && !vo.getPassword().isEmpty()) {
            vo.setPassword(encoder.encode(vo.getPassword()));
        }
        Integer result = empMapper.modifyEmpById(vo);

        if (result > 0) {

            // [STEP 1] DB에 있는 '기존' 그룹 목록 가져오기
            List<Integer> oldGroups = empMapper.selectActiveGroupIds(vo.getUserId());
            // 화면에서 넘어온 '새로운' 그룹 목록 (null 방지)
            List<Integer> newGroups = vo.getGroupIds() != null ? vo.getGroupIds() : new ArrayList<>();
            for(Integer groupId : newGroups){
                log.error(String.valueOf(groupId));
            }
            // [STEP 2] 비교해서 3가지 리스트로 찢기!

            // 1. 없앨 그룹 (기존에는 있는데 새로 넘어온 배열엔 없는 것)
            List<Integer> toRemove = oldGroups.stream()
                    .filter(id -> !newGroups.contains(id))
                    .collect(Collectors.toList());

            // 2. 추가할 그룹 (새 배열에는 있는데 기존 DB엔 없는 것)
            List<Integer> toAdd = newGroups.stream()
                    .filter(id -> !oldGroups.contains(id))
                    .collect(Collectors.toList());

            // 3. 유지할 그룹 (양쪽 다 있는 교집합)
            List<Integer> toKeep = oldGroups.stream()
                    .filter(newGroups::contains)
                    .collect(Collectors.toList());


            // [STEP 3] 갈라낸 리스트들을 각자 알맞게 DB에 명령 내리기

            // - 빠진 그룹은 비활성화(O2) 처리 (역사 속으로)
            for (Integer groupId : toRemove) {
                empMapper.deactivateGroupMember(vo.getUserId(), groupId);
            }

            // - 새로 들어온 그룹은 INSERT (새로운 역사 시작, joined_at 오늘 날짜로 기록)
            for (Integer groupId : toAdd) {

                String isPrimary = groupId.equals(vo.getPrimaryGroupId()) ? "Y" : "N";
                EmpVOSJW emp =  new EmpVOSJW();
                emp.setUserId(vo.getUserId());
                emp.setGroupId(groupId);
                emp.setIsPrimary(isPrimary);
                empMapper.insertGroupMember(emp);
            }

            // - 유지되는 그룹은 joined_at은 냅두고 대표 그룹 지정(is_primary)만 갱신
            for (Integer groupId : toKeep) {
                String isPrimary = groupId.equals(vo.getPrimaryGroupId()) ? "Y" : "N";
                empMapper.updateGroupMemberPrimary(vo.getUserId(), groupId, isPrimary);
            }
        }

        return result > 0;
    }

    @Override
    public Integer sendEmail(String toEmail, Integer userId) {
        // 먼저 해당 사번이 존재하는지 확인
        EmpVOSJW emp = empMapper.getById(userId);
        if(emp == null) {
            return 0;
        }
        // 1. 완벽해진 6자리 난수 생성 로직!
        int randval = (int) (Math.random() * 1000000);
        String authCode = String.format("%06d", randval);

        // 2. DB에 저장하기 위해 VO 객체 조립
        AccountDTOSJW vo = new AccountDTOSJW();
        vo.setEmail(toEmail);   // 누구한테 보내는지 (테이블에 email 컬럼 추가하셨다면!)
        vo.setEmailNo(authCode); // 6자리 코드

        // 3. DB에 INSERT (이때 mapper XML의 <selectKey>가 작동해서 vo 안에 emailId가 채워집니다!)
        empMapper.insertEmailAuth(vo);

        // 4. 실제 이메일 발송 (우체국 작업)
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[ComPath] 비밀번호 재설정 인증번호 안내");
        message.setText("비밀번호 재설정을 위한 인증번호입니다.\n\n"
                + "인증번호: [" + authCode + "]\n\n"
                + "진행 중인 화면에 위 번호를 3분 이내에 입력해 주세요.");

        javaMailSender.send(message);

        // 5. 프론트엔드에게 돌려줄 고유 ID 리턴!
        return vo.getEmailId();
    }

    @Override
    public Boolean verifyCode(Integer code, Integer emailId) {
        Integer result = empMapper.verifyAuthCode(code,emailId);
        if(result > 0 ){
            empMapper.cleanUpOldCodes(emailId);
            return true;
        }
        return false;
    }

    @Override
    public EmpVOSJW getByIdForMyPage(Integer id) {
        EmpVOSJW emp = empMapper.getByIdForMyPage(id);
        emp.setPassword(null);
        return emp;
    }

}
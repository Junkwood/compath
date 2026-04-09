package com.example.user.service;

import com.example.user.entity.UserEntity;
import com.example.user.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(16);

    public UserEntity create(final UserEntity userEntity) {
        if(userEntity == null || userEntity.getUserId() == null){
            throw new RuntimeException("Invalid arguments");
        }
        final Integer username = userEntity.getUserId();

        // 🚨 수정된 부분: existsBy... 대신 findBy... 로 List를 받아와서 체크
        List<UserEntity> existingUsers = userRepository.findByUserId(username);
        if(existingUsers != null && !existingUsers.isEmpty()){
            log.warn("Username {} already exists", username);
            throw new RuntimeException("Username already exists");
        }
        String rawPassword = userEntity.getPassword();
        String  encodedPassword = passwordEncoder.encode(rawPassword);
        userEntity.setPassword(encodedPassword);
        return userRepository.save(userEntity);
    }

    public UserEntity getByCredentials(final Integer username, final String password) {
        List<UserEntity> users = userRepository.findByUserId(username);

        // 유저가 아예 없으면 로그인 실패
        if (users == null || users.isEmpty()) {
            return null;
        }
        UserEntity result = users.get(0);

        // ⭐ 사용자가 입력한 평문(raw) 비번과 DB의 암호화된 비번을 비교합니다.
        if (passwordEncoder.matches(password, result.getPassword())) {
            // 인증 성공!
            result.setPassword(null); // 클라이언트로 돌려주기 전에 비번은 null로 지워주는 센스!
            return result;
        } else {
            // 비밀번호 불일치
            return null;
        }
    }
}
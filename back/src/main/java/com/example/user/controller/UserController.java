package com.example.user.controller;

import com.example.common.config.security.TokenProvider;
import com.example.user.dto.ResponseDTO;
import com.example.user.dto.UserDTO;
import com.example.user.entity.UserEntity;
import com.example.user.persistence.UserRepository;
import com.example.user.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        try {
            if (userDTO == null || userDTO.getPassword() == null) {
                throw new RuntimeException("Invalid Password value.");
            }
            //요청을 이용해 저장할 유저 만들기
            UserEntity user = UserEntity.builder()
                    .userId(userDTO.getUserId())
                    .password(userDTO.getPassword())
                    .build();
            //서비스를 이용해 리포지토리에 유저 저장
            UserEntity registeredUser = userService.create(user);
            UserDTO responseUserDTO = UserDTO.builder()
                    .userId(registeredUser.getUserId())
                    .build();
            return ResponseEntity.ok().body(responseUserDTO);
        } catch (Exception e) {
            // 유저 정보는 항상 하나이므로 리스트로 만들어야 하는 ,ResponseDTO를 사용하지 않고 그냥 UserDTO리턴.
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
    @PostMapping("/signIn")
    public ResponseEntity<?> authenticate(@RequestBody UserDTO userDTO) {
        UserEntity user = userService.getByCredentials(
                userDTO.getUserId(),
                userDTO.getPassword()
        );
//        log.error(user.toString());3
        log.error("login user = {}", user);
        if(user != null) {
            String accessToken = tokenProvider.createAccessToken(user);
            String refreshToken = tokenProvider.createRefreshToken(user);

            final UserDTO responseUserDTO = UserDTO.builder()
                    .userId(user.getUserId())
                    .token(accessToken)
                    .refreshToken(refreshToken)
                    .primaryGroupName(userDTO.getPrimaryGroupName())
                    .primaryGroupId(userDTO.getPrimaryGroupId())
                    .build();
            return ResponseEntity.ok().body(responseUserDTO);
        }else{
            ResponseDTO responseDTO = ResponseDTO.builder().error("Login failed.")
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken=request.get("refreshToken");
        if(refreshToken==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("refresh token이 없습니다.");
        }
        try{
            String userId = tokenProvider.validateAndGetUserId(refreshToken);//리프레쉬 토큰 검증 및 유저 id 추출

            List<UserEntity> userEntityList = userRepository.findByUserId(Integer.valueOf(userId));
            if (userEntityList.isEmpty()||userEntityList==null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("존재하지 않는 유저입니다.");
            }
            UserEntity user = userEntityList.get(0);
            String newAccessToken = tokenProvider.createAccessToken(user);//새토큰 발급
            Map<String, String> response = new HashMap<>();
            response.put("token", newAccessToken);
            return ResponseEntity.ok(response);
        }
        catch (ExpiredJwtException e) {
            log.warn("리프레시 토큰 만료: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("리프레시 토큰이 만료되었습니다.");

        } catch (Exception e) {
            log.error("리프레시 토큰 검증 실패", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 리프레시 토큰입니다.");
        }
    }

}

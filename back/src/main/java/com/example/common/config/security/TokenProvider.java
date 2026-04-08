package com.example.common.config.security;

import com.example.user.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
public class TokenProvider {
    private static final String SECRET_KEY ="";
    public String create(UserEntity userEntity) {
        Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
        return null;
    }
}

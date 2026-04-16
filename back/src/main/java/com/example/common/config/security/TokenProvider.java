package com.example.common.config.security;

import com.example.user.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
public class TokenProvider {
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public String createAccessToken(UserEntity userEntity) {
        Date expiryDate = Date.from(Instant.now().plus(30    , ChronoUnit.MINUTES));
        /*
        {//header
            "alg":"HS512"
        }.
        {//payload
            "sub":"40288093784915d201784916a40c0001",
            "iss":"demo app",
            "iat":1595733657,
            "exp":1595733657
        }.
        //SECRET_KEY를 이용해 서명한 부분
        Nn4d1MOVLZg79sfFACTIpCPKqWmpZMZQsbNrXdJJNWKRv50_l7bPLQPwhMobT4vbog6Q3JYjhDrKFlBSaUxZOg
         */

        return Jwts.builder()
                //헤더에 들어갈 내용 및 서명을 하기 위한 SECRET_KEY
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                //payload에 들어갈 내용
                .setSubject(String.valueOf(userEntity.getUserId()))
                //커스텀 데이터 추가
                .claim("userId", userEntity.getUserId())
                .claim("userType", userEntity.getUserType())
                .claim("userName", userEntity.getUserName())
                .claim("primaryGroupName", userEntity.getPrimaryGroupName())
                .claim("primaryGroupId", userEntity.getPrimaryGroupId())
                .setIssuer("demo app")
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .compact();
    }
    public String createRefreshToken(UserEntity userEntity) {
        // 만료 시간: 현재 시간으로부터 14일 뒤
        Date expiryDate = Date.from(Instant.now().plus(14, ChronoUnit.DAYS));

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                // 💡 핵심: Refresh Token은 털려도 위험하지 않게 최소한의 식별자(Subject)만 넣습니다.
                .setSubject(String.valueOf(userEntity.getUserId()))
                .setIssuer("demo app")
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .compact();
    }
    public String validateAndGetUserId(String token) {
        //parseClaimsJws 매서드가 base 64로 디코딩 및 파싱.
        //헤더와 페이로드를 setSigningKey로 넘어온 시크릿을 이용해 서명 후 , token의 서명과 비교.
        //위조되지 않았다면 페이로드(Claims) 리턴, 위조라면 예외날림.
        //userId가 필요하니 getBody 부름.
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public String getUserRole(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userType", String.class);
    }
}

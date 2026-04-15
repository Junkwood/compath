package com.example.common.config.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 요청에서 토큰 가져오기
            String token = parseBearerToken(request);

            // 토큰 검사하기
            if (token != null && !token.equalsIgnoreCase("null")) {
                // userId 가져오기 (여기서 토큰이 만료되었으면 ExpiredJwtException이 터집니다)
                String userId = tokenProvider.validateAndGetUserId(token);
                String role = tokenProvider.getUserRole(token);
                List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_" + role);

                log.info("인증된 유저: {}, 권한: {}", userId, role);

                // 인증완료. SecurityContextHolder에 등록
                AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authentication);
                SecurityContextHolder.setContext(securityContext);
            }

        } catch (ExpiredJwtException e) {
            //  1. 만료된 토큰 에러를 잡아서 명시적으로 401 에러를 응답합니다.
            log.error("토큰이 만료되었습니다: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드 셋팅
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"토큰이 만료되었습니다. 다시 로그인해주세요.\"}");

            return; // 여기서 return을 해서 다음 필터로 안 넘어가게 콱 막아야 합니다!

        } catch (Exception e) {
            //  2. 그 외의 위조된 토큰 등 다른 에러 처리
            log.error("SecurityContext에 유저인증을 설정할 수 없습니다.", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"유효하지 않은 토큰입니다.\"}");

            return; // 여기서도 요청을 끝냅니다.
        }

        // 정상적인 요청일 경우에만 다음 필터로 넘어감
        filterChain.doFilter(request, response);
    }

    private String parseBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
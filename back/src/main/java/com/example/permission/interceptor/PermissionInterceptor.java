package com.example.permission.interceptor;

import com.example.permission.mapper.PermissionMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {

    private final PermissionMapper permissionMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true; //정적 리소스는 통과
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 컨트롤러 원본 패턴 가져오기
        String urlPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (urlPattern == null) {
            return true;
        }

        // ADMIN은 다 통과시킴
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        // 헤더에서 프로젝트 ID 추출
        String projectIdStr = request.getHeader("X-Project-Id");
        if (projectIdStr == null) {
            log.debug("⚠️ X-Project-Id 헤더가 없어서 권한 검사를 생략하고 통과시킵니다. (URL: {})", request.getRequestURI());
            return true;
        }

        Integer projectId = Integer.parseInt(projectIdStr);
        String userId = auth.getName();
        String method = request.getMethod();

        // =====================================================================
        // 💡 [1차 검증] 프로젝트 자체 접근 권한 (P1 공개 여부 or P2 멤버 여부)
        // =====================================================================
        boolean hasProjectAccess = permissionMapper.checkProjectAccess(projectId, userId);
        if (!hasProjectAccess) {
            log.warn("🚨 [1차 차단] 비공개 프로젝트 접근 시도 : userId={}, projectId={}", userId, projectId);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("비공개 프로젝트이거나 접근 권한이 없습니다.");
            return false; // 즉시 차단
        }

        // =====================================================================
        // 💡 [2차 검증] 세부 URL/Method 기반 Role 권한 검사 (기존 로직)
        // =====================================================================
        String targetUrl;
        boolean isSpecialPermissionExist = permissionMapper.isUrlRegisteredInDb(urlPattern, method);

        if (isSpecialPermissionExist) {
            targetUrl = urlPattern;
            log.debug("명시적으로 DB에 지정된 요청 : {}", targetUrl);
        } else {
            String packageName = handlerMethod.getBeanType().getPackage().getName();
            targetUrl = mapPackageToDbUrl(packageName);
            log.debug("패키지에 따른 권한으로 매핑된 요청 : {}", packageName);
        }

        if (targetUrl == null) {
            log.debug("매핑 안됨 -> 통과");
            return true; // 매핑 안된 건 일단 통과
        }

        // 최종 세부 권한 검사
        Integer hasPermission = permissionMapper.checkUserPermission(userId, projectId, method, targetUrl);

        if (hasPermission > 0) {
            log.info("✅ 권한 승인 : userId={}, projectId={}, Method={}, TargetUrl={}", userId, projectId, method, targetUrl);
            return true; // 성공
        } else {
            log.warn("⛔ [2차 차단] 권한 거부 : userId={}, projectId={}, Method={}, TargetUrl={}", userId, projectId, method, targetUrl);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("해당 프로젝트에서 요청하신 작업을 수행할 권한이 없습니다.");
            return false;
        }
    }

    // 패키지를 DB의 기존에 큰 틀로 지정해둔 도메인URL로 변환처리.
    private String mapPackageToDbUrl(String packageName) {
        if (packageName.contains(".task")) return "/api/tasks";
        else if (packageName.contains(".project")) return "/api/project";
        else if (packageName.contains(".document")) return "/api/documents";
        else if (packageName.contains(".meeting") || packageName.contains(".gemini")) return "/api/meetings";
        return null;
    }
}
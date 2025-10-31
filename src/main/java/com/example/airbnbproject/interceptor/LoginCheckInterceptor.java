// src/main/java/com/example/airbnbproject/interceptor/LoginCheckInterceptor.java
package com.example.airbnbproject.interceptor;

import com.example.airbnbproject.domain.User;
import com.example.airbnbproject.domain.UserRole;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    private static final Set<String> CSRF_SAFE_METHODS = Set.of("GET", "HEAD", "OPTIONS", "TRACE");

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {

        final String ctx    = req.getContextPath(); // 예: "/_dev"
        final String uri    = req.getRequestURI();  // 예: "/_dev/accommodation/1"
        final String path   = uri.substring(ctx.length()); // 예: "/accommodation/1"  ← ★ 비교는 이걸로
        final String method = req.getMethod();

        HttpSession session = req.getSession(false);
        User loginUser = (session != null) ? (User) session.getAttribute("user") : null;

        // 0) 에러 경로는 통과
        if (path.startsWith("/error")) {
            ensureCsrfToken(session, req);
            return true;
        }

        // 1) auth 파라미터가 있으면 통과 (모달/로그인 렌더)
        if (req.getParameter("auth") != null) {
            ensureCsrfToken(session, req);
            return true;
        }

        // 1.5) 정적/헬스는 통과(필요시)
        if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/")
                || path.startsWith("/actuator")) {
            ensureCsrfToken(session, req);
            return true;
        }

        // 2) 숙소 상세는 비로그인 허용
        if ("GET".equals(method) && path.matches("^/accommodation/\\d+$")) {
            ensureCsrfToken(session, req);
            return true;
        }

        // 3) 로그인 검사
        if (loginUser == null) {
            String query = req.getQueryString();
            // next는 "컨텍스트 포함한 원래 요청"을 그대로 보관
            String next = uri + (query != null ? "?" + query : "");

            // 루프 방지
            if (path.startsWith("/error") || (query != null && query.contains("auth=login"))) {
                next = ctx + "/"; // 컨텍스트 포함 홈으로
            }

            String encoded = URLEncoder.encode(next, StandardCharsets.UTF_8.name());
            // ★ 리다이렉트 대상에도 컨텍스트 붙이기
            res.sendRedirect(ctx + "/?auth=login&next=" + encoded);
            return false;
        }

        // 4) 관리자 보호
        if (path.startsWith("/admin")) {
            if (loginUser.getRole() != UserRole.ADMIN) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
        }

        // 5) CSRF
        if (!CSRF_SAFE_METHODS.contains(method)) {
            session = (session != null) ? session : req.getSession(false);
            String sessionToken = (session != null) ? (String) session.getAttribute("csrfToken") : null;
            String reqToken = req.getParameter("_csrf");
            if (sessionToken == null || !sessionToken.equals(reqToken)) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
        }

        ensureCsrfToken(session, req);
        return true;
    }

    private void ensureCsrfToken(HttpSession session, HttpServletRequest req) {
        HttpSession s = (session != null) ? session : req.getSession(true);
        if (s.getAttribute("csrfToken") == null) {
            s.setAttribute("csrfToken", UUID.randomUUID().toString());
        }
    }
}

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

    // CSRF 체크 면제 메서드 (Safe Methods)
    private static final Set<String> CSRF_SAFE_METHODS = Set.of("GET", "HEAD", "OPTIONS", "TRACE");

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler)
            throws Exception {

        final String uri = req.getRequestURI();
        final String method = req.getMethod();

        HttpSession session = req.getSession(false);
        User loginUser = (session != null) ? (User) session.getAttribute("user") : null;

        // ✅ 0) 에러 경로는 절대 막지 않음 (무한루프 방지의 1차 안전장치)
        if (uri.startsWith("/error")) {
            ensureCsrfToken(session, req);
            return true;
        }

        // ✅ 1) 이미 auth 파라미터가 붙은 요청은 다시 감싸지 않음 (2차 안전장치)
        //    - /?auth=login&next=... 로 한번 왔으면 그대로 통과시켜 모달/로그인 UI가 렌더되도록
        if (req.getParameter("auth") != null) {
            ensureCsrfToken(session, req);
            return true;
        }

        // ✅ 2) 상세보기(GET /accommodation/{숫자})는 로그인 없이 허용 (네 기존 의도 유지)
        if ("GET".equals(method) && uri.matches("^/accommodation/\\d+$")) {
            ensureCsrfToken(session, req);
            return true;
        }

        // ✅ 3) 로그인 검사 (그 외 경로는 보호)
        if (loginUser == null) {
            String current = req.getRequestURI();
            String query = req.getQueryString();
            String next = current + (query != null ? "?" + query : "");

            // 🔒 루프 방지: next가 /error거나, 이미 auth=login이 들어 있으면 홈으로 교체
            if (next.startsWith("/error") || (query != null && query.contains("auth=login"))) {
                next = "/";
            }

            String encoded = URLEncoder.encode(next, StandardCharsets.UTF_8.name());
            res.sendRedirect("/?auth=login&next=" + encoded);
            return false;
        }

        // ✅ 4) 관리자 경로는 ADMIN만
        if (uri.startsWith("/admin")) {
            if (loginUser.getRole() != UserRole.ADMIN) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN); // 403
                return false;
            }
        }

        // ✅ 5) CSRF 검증: 변경 메서드(POST/PUT/DELETE/…)
        if (!CSRF_SAFE_METHODS.contains(method)) {
            session = (session != null) ? session : req.getSession(false);
            String sessionToken = (session != null) ? (String) session.getAttribute("csrfToken") : null;
            String reqToken = req.getParameter("_csrf"); // 폼 hidden 필드명
            if (sessionToken == null || !sessionToken.equals(reqToken)) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN); // 403
                return false;
            }
        }

        // 통과
        ensureCsrfToken(session, req);
        return true;
    }

    // 세션이 없으면 하나 만들고, CSRF 토큰이 없으면 발급
    private void ensureCsrfToken(HttpSession session, HttpServletRequest req) {
        HttpSession s = (session != null) ? session : req.getSession(true);
        if (s.getAttribute("csrfToken") == null) {
            s.setAttribute("csrfToken", UUID.randomUUID().toString());
        }
    }
}

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

        String uri = req.getRequestURI();
        String method = req.getMethod();

        HttpSession session = req.getSession(false);
        User loginUser = (session != null) ? (User) session.getAttribute("user") : null;

        // 0) 상세보기(GET /accommodation/{숫자})는 로그인 없이 허용
        //    - 컨트롤러도 상세보기만 이 경로를 사용 중(등록/수정/삭제는 별도 경로)
        if ("GET".equals(method) && uri.matches("^/accommodation/\\d+$")) {
            ensureCsrfToken(session, req); // (옵션) GET 시에도 토큰 미리 발급해두면 폼에 바로 쓸 수 있음
            return true;
        }

        // 1) 로그인 검사 (그 외 경로는 모두 보호)
        if (loginUser == null) {
            String current = req.getRequestURI();
            String query = req.getQueryString();
            String next = current + (query != null ? "?" + query : "");
            String encoded = URLEncoder.encode(next, StandardCharsets.UTF_8.name());
//            res.sendRedirect("/login?next=" + encoded);
            res.sendRedirect("/?auth=login&next=" + encoded);
            return false;
        }

        // 2) 관리자 경로는 ADMIN만
        if (uri.startsWith("/admin")) {
            if (loginUser.getRole() != UserRole.ADMIN) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN); // 403
                return false;
            }
        }

        // 3) CSRF 검증: 변경 메서드(POST/PUT/DELETE/…)
        if (!CSRF_SAFE_METHODS.contains(method)) {
            String sessionToken = (String) session.getAttribute("csrfToken");
            String reqToken = req.getParameter("_csrf"); // 폼 hidden 필드명
            if (sessionToken == null || !sessionToken.equals(reqToken)) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN); // 403
                return false;
            }
        }

        // 4) 통과
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

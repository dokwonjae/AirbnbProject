package com.example.airbnbproject.interceptor;

import com.example.airbnbproject.domain.User;
import com.example.airbnbproject.domain.UserRole;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("/login?error=needLogin");
            return false;
        }

        User user = (User) session.getAttribute("user");
        if (request.getRequestURI().startsWith("/admin")) {
            if (user == null || user.getRole() != UserRole.ADMIN) {
                response.sendRedirect("/?error=accessDenied");
                return false;
            }
        }


        return true;
    }
}

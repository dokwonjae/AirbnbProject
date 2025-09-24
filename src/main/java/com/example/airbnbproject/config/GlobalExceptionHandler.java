package com.example.airbnbproject.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.AccessDeniedException;

@Slf4j
@ControllerAdvice(annotations = Controller.class)
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException ex, HttpServletResponse res, Model model) {
        res.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
        model.addAttribute("message", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleBadRequest(IllegalArgumentException ex, HttpServletResponse res, Model model) {
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
        model.addAttribute("message", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler({OptimisticLockingFailureException.class})
    public String handleConflict(RuntimeException ex, HttpServletResponse res, Model model) {
        res.setStatus(HttpServletResponse.SC_CONFLICT); // 409
        model.addAttribute("message", "요청이 중복되었거나 다른 요청과 충돌했습니다. 다시 시도해주세요.");
        return "error/error";
    }

    @ExceptionHandler(AuthenticationException.class)
    public String handleAuth(AuthenticationException ex, HttpServletResponse res, Model model) {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        model.addAttribute("message", "인증이 필요합니다.");
        return "error/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleAll(Exception ex, HttpServletResponse res, Model model) {
        log.error("Unexpected error", ex);
        res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
        model.addAttribute("message", "서버 내부 오류가 발생했습니다.");
        return "error/error";
    }
}

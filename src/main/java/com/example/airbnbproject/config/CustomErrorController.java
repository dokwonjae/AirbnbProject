package com.example.airbnbproject.config;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object sc = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int status = (sc == null) ? 500 : Integer.parseInt(sc.toString());

        // 요청 경로 (에러가 발생한 URI)
        String rawPath = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        String decodedPath = rawPath != null ? URLDecoder.decode(rawPath, StandardCharsets.UTF_8) : null;

        // 에러 메시지
        String msg = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        // 모델에 값 넣기
        model.addAttribute("status", status);
        model.addAttribute("path", decodedPath);
        if (msg != null) model.addAttribute("message", msg);

        // 상태 코드별 페이지 선택
        if (status == 404) return "error/404";
        if (status >= 500) return "error/500";
        return "error/error";
    }
}

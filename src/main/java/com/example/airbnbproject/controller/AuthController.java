package com.example.airbnbproject.controller;

import com.example.airbnbproject.domain.User;
import com.example.airbnbproject.dto.UserJoinRequestDto;
import com.example.airbnbproject.dto.UserLoginRequestDto;
import com.example.airbnbproject.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService userService;

    // ===== 기존 유틸 =====
    private String baseFromRef(String ref) {
        if (ref == null || ref.isEmpty()) return "/";
        String lower = ref.toLowerCase();
        if (lower.contains("/join") || lower.contains("/login")) return "/";
        return ref;
    }

    private String stripAuthParams(String url) {
        if (url == null || url.isEmpty()) return "/";
        try {
            return UriComponentsBuilder.fromUriString(url)
                    .replaceQueryParam("auth")
                    .replaceQueryParam("login")
                    .build(true).toUriString();
        } catch (Exception e) {
            return url;
        }
    }

    private String withAuth(String url, String auth) {
        String clean = stripAuthParams(url);
        return clean.contains("?") ? clean + "&auth=" + auth : clean + "?auth=" + auth;
    }

    private Map<String, String> toFieldErrorMap(BindingResult br) {
        return br.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (a, b) -> a
                ));
    }

    // ===== 추가/보강 유틸 =====
    /** 안전한 next 결정: 내부 경로(/로 시작)만 허용, 아니면 ref 기반으로 복귀 */
    private String safeTarget(String next, String ref) {
        if (next != null && next.startsWith("/")) return next;
        return baseFromRef(ref);
    }

    /** auth + next 동시 부착 (auth 모달을 띄우면서 next를 폼에 남기기) */
    private String withAuthAndNext(String url, String auth, String next) {
        String clean = stripAuthParams(url);
        try {
            UriComponentsBuilder b = UriComponentsBuilder.fromUriString(clean)
                    .replaceQueryParam("auth", auth);
            if (next != null && !next.isBlank()) {
                b.replaceQueryParam("next", next);
            }
            return b.build(true).toUriString();
        } catch (Exception e) {
            // 실패 시 최소한 auth만
            return withAuth(url, auth);
        }
    }

    // ===== GET 폼 진입 =====

    @GetMapping("/join")
    public String joinForm(@RequestHeader(value = "Referer", required = false) String ref,
                           @RequestParam(value = "next", required = false) String next) {
        String target = safeTarget(next, ref);
        // 실제로 보여줄 페이지 URL에 모달 파라미터와 next를 유지
        return "redirect:" + withAuthAndNext(target, "join", target);
    }

    @GetMapping("/login")
    public String loginForm(@RequestHeader(value = "Referer", required = false) String ref,
                            @RequestParam(value = "next", required = false) String next) {
        String target = safeTarget(next, ref);
        return "redirect:" + withAuthAndNext(target, "login", target);
    }

    // ===== POST 처리 =====

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute UserJoinRequestDto dto,
                       BindingResult br,
                       @RequestHeader(value = "Referer", required = false) String ref,
                       @RequestParam(value = "next", required = false) String next,
                       RedirectAttributes ra) {
        String back = safeTarget(next, ref);

        if (dto.getPassword() != null && dto.getConfirmPassword() != null) {
            if (!dto.getPassword().equals(dto.getConfirmPassword())) {
                br.rejectValue("confirmPassword", "Mismatch", "비밀번호 확인이 일치하지 않습니다.");
            }
        }

        if (br.hasErrors()) {
            ra.addFlashAttribute("authTab", "join");
            ra.addFlashAttribute("joinData", dto);
            ra.addFlashAttribute("joinFieldErrors", toFieldErrorMap(br));
            return "redirect:" + withAuthAndNext(back, "join", back);
        }

        try {
            userService.register(dto);
            ra.addFlashAttribute("toast", "회원가입 성공");
            // 가입 후 로그인 모달로 자연스럽게 이동 (next 유지)
            return "redirect:" + withAuthAndNext(stripAuthParams(back), "login", back);
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("authTab", "join");
            ra.addFlashAttribute("joinData", dto);
            ra.addFlashAttribute("joinErrorMsg", e.getMessage());
            return "redirect:" + withAuthAndNext(back, "join", back);
        }
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute UserLoginRequestDto dto,
                        BindingResult br,
                        @RequestHeader(value = "Referer", required = false) String ref,
                        @RequestParam(value = "next", required = false) String next,
                        HttpServletRequest request,
                        HttpSession session,
                        RedirectAttributes ra) {
        String back = safeTarget(next, ref);

        if (br.hasErrors()) {
            ra.addFlashAttribute("authTab", "login");
            ra.addFlashAttribute("prevLoginId", dto.getLoginId());
            ra.addFlashAttribute("loginFieldErrors", toFieldErrorMap(br));
            return "redirect:" + withAuthAndNext(back, "login", back);
        }

        try {
            User user = userService.login(dto.getLoginId(), dto.getPassword());

            if (session != null) {
                try { session.invalidate(); } catch (IllegalStateException ignore) {}
            }
            HttpSession newSession = request.getSession(true);

            newSession.setAttribute("user", user);

            newSession.setAttribute("csrfToken", java.util.UUID.randomUUID().toString());

            ra.addFlashAttribute("msg", "로그인 성공");
            return "redirect:" + stripAuthParams(back);

        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("authTab", "login");
            ra.addFlashAttribute("prevLoginId", dto.getLoginId());
            ra.addFlashAttribute("loginErrorMsg", e.getMessage());
            return "redirect:" + withAuthAndNext(back, "login", back);
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session,
                         @RequestHeader(value = "Referer", required = false) String ref) {
        session.invalidate();
        return "redirect:" + stripAuthParams(baseFromRef(ref));
    }
}

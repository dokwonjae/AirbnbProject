package com.example.airbnbproject.controller;

import com.example.airbnbproject.domain.User;
import com.example.airbnbproject.dto.JoinRequestDto;
import com.example.airbnbproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller // Spring MVC의 컨트롤러로 인식됨
@RequiredArgsConstructor // 생성자 자동 생성 (userService 주입됨)
public class UserController {

    private final UserService userService; // 비즈니스 로직 처리 클래스 의존성 주입

    @GetMapping("/join") // 회원가입 폼 페이지 요청
    public String joinForm(Model model) {
        model.addAttribute("joinRequestDto", new JoinRequestDto());
        return "join";
    }
    @GetMapping("/login") // 회원가입 폼 페이지 요청
    public String loginForm() {
        return "login"; // join.jsp 뷰 반환
    }

    @PostMapping("/join") // 회원가입 폼 제출 처리
    public String join(@Valid @ModelAttribute("joinRequestDto") JoinRequestDto dto,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes ra) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("joinRequestDto", dto); // 입력값 유지
            return "join"; // redirect 말고 forward (그대로 넘겨줌)
        }
        try {
            userService.register(dto); // 회원가입 처리 로직 실행
            ra.addFlashAttribute("msg", "회원가입 성공"); // 메시지를 redirect로 전달
            return "redirect:/login"; // 로그인 페이지로 이동
        } catch (IllegalArgumentException e) {
            model.addAttribute("msg", e.getMessage());
            model.addAttribute("joinRequestDto", dto);
            return "join";
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam String loginId,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes ra) {
        User user = userService.login(loginId, password);
        if (user == null) {
            ra.addFlashAttribute("msg", "아이디 또는 비밀번호가 잘못되었습니다.");
            return "redirect:/login";
        }

        session.setAttribute("user", user);
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }


}

package com.example.airbnbproject.controller;

import com.example.airbnbproject.domain.User;
import com.example.airbnbproject.dto.JoinRequestDto;
import com.example.airbnbproject.dto.LoginRequestDto;
import com.example.airbnbproject.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService userService;

    @GetMapping("/join")
    public String joinForm(Model model) {
        model.addAttribute("joinRequestDto", new JoinRequestDto());
        return "join";
    }
    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginRequestDto", new LoginRequestDto());
        return "login";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute("joinRequestDto") JoinRequestDto dto,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes ra) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("joinRequestDto", dto);
            return "join";
        }
        try {
            userService.register(dto);
            ra.addFlashAttribute("msg", "회원가입 성공");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("msg", e.getMessage());
            model.addAttribute("joinRequestDto", dto);
            return "join";
        }
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginRequestDto") LoginRequestDto loginRequestDto,
                        BindingResult bindingResult,
                        Model model,
                        HttpSession session,
                        RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "login";
        }

        try {
            User user = userService.login(loginRequestDto.getLoginId(), loginRequestDto.getPassword());
            session.setAttribute("user", user);
            ra.addFlashAttribute("msg", "로그인 성공");
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("loginError", e.getMessage());  // → 메시지 JSP로 전달
            return "login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }


}

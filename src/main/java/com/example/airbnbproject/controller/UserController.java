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

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/join")
    public String joinForm(Model model) {
        model.addAttribute("joinRequestDto", new JoinRequestDto());
        return "join";
    }
    @GetMapping("/login")
    public String loginForm() {
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

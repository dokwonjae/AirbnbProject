package com.example.airbnbproject.controller;

import com.example.airbnbproject.domain.Accommodation;
import com.example.airbnbproject.domain.User;
import com.example.airbnbproject.dto.AccommodationRequestDto;
import com.example.airbnbproject.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/accommodation")
public class AccommodationRegistrationController {

    private final AccommodationService accommodationService;

    @GetMapping("/register")
    public String showForm(Model model) {
        // 로그인 강제는 인터셉터에서 처리
        model.addAttribute("accommodationRequestDto", new AccommodationRequestDto()); // 폼 바인딩용
        return "accommodationRegistration";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("accommodationRequestDto") AccommodationRequestDto dto,
                           BindingResult bindingResult,
                           HttpSession session,
                           Model model,
                           RedirectAttributes ra) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("accommodationRequestDto", dto);
            return "accommodationRegistration";
        }

        User user = (User) session.getAttribute("user"); // 로그인은 인터셉터 보장
        try {
            Accommodation accommodation = accommodationService.saveAccommodation(dto, user);
            if (accommodation == null) {
                ra.addFlashAttribute("msg", "숙소 등록에 실패했습니다.");
                return "redirect:/accommodation/register";
            }
            return "redirect:/info/register?accommodationId=" + accommodation.getId();
        } catch (Exception e) {
            ra.addFlashAttribute("msg", "서버 오류로 등록에 실패했습니다.");
            return "redirect:/accommodation/register";
        }
    }
}

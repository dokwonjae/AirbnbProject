package com.example.airbnbproject.controller;

import com.example.airbnbproject.domain.Accommodation;
import com.example.airbnbproject.domain.User;
import com.example.airbnbproject.domain.UserRole;
import com.example.airbnbproject.dto.AccommodationRequestDto;
import com.example.airbnbproject.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/accommodation")
public class AccommodationRegistrationController {

    private final AccommodationService accommodationService;


    @GetMapping("/register")
    public String showForm(HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");

        if (user == null || user.getRole() != UserRole.ADMIN) {
            ra.addFlashAttribute("msg", "관리자만 숙소 등록이 가능합니다.");
            return "redirect:/";
        }

        return "accommodationRegistration";
    }


    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("accommodationRequestDto") AccommodationRequestDto dto,
                           BindingResult bindingResult,
                           HttpSession session,
                           Model model,
                           RedirectAttributes ra) {

        User user = (User) session.getAttribute("user");

        if (user == null || user.getRole() != UserRole.ADMIN) {
            ra.addFlashAttribute("msg", "관리자만 숙소 등록이 가능합니다.");
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("accommodationRequestDto", dto); // 입력값 유지
            return "accommodationRegistration";
        }

        try {
            Accommodation accommodation = accommodationService.saveAccommodation(dto, user);
            if (accommodation == null) {
                ra.addFlashAttribute("msg", "숙소 등록에 실패했습니다.");
                return "redirect:/accommodation/register";
            }

            System.out.println("숙소 등록 성공: " + accommodation.getId());
            return "redirect:/info?accommodationId=" + accommodation.getId();

        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("msg", "서버 오류로 등록에 실패했습니다.");
            return "redirect:/accommodation/register";
        }
    }

}

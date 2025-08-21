package com.example.airbnbproject.controller;

import com.example.airbnbproject.domain.User;
import com.example.airbnbproject.dto.*;
import com.example.airbnbproject.repository.AccommodationRepository;
import com.example.airbnbproject.repository.ReservationRepository;
import com.example.airbnbproject.service.AccountService;
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
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;
    private final AccommodationRepository accommodationRepository;
    private final ReservationRepository reservationRepository;

    // 마이페이지 요약 화면
    @GetMapping
    public String myPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        List<MyAccommodationRowResponseDto> myAccommodations =
                accommodationRepository.findMyAccommodationRows(user.getId());
        List<MyReservationRowResponseDto> myReservations =
                reservationRepository.findMyReservationRows(user);

        model.addAttribute("user", user);
        model.addAttribute("myAccommodations", myAccommodations);
        model.addAttribute("myReservations", myReservations);
        return "myPage";
    }

    // ✅ 연락처 변경 폼 (GET)
    @GetMapping("/contact")
    public String contactForm(HttpSession session, Model model) {
        if (!model.containsAttribute("contactUpdateRequestDto")) {
            User user = (User) session.getAttribute("user");
            UserContactUpdateRequestDto form = new UserContactUpdateRequestDto();
            form.setTel(user.getTel());
            form.setEmail(user.getEmail());
            model.addAttribute("contactUpdateRequestDto", form);
        }
        return "accountContact";
    }

    // 연락처 변경 처리 (POST)
    @PostMapping("/contact")
    public String updateContact(@Valid @ModelAttribute("contactUpdateRequestDto") UserContactUpdateRequestDto dto,
                                BindingResult br,
                                HttpSession session,
                                RedirectAttributes ra) {
        if (br.hasErrors()) {
            // 👉 에러 시 자기 페이지로 다시
            return "accountContact";
        }
        User user = (User) session.getAttribute("user");
        try {
            accountService.updateContact(user.getId(), dto);
            // 세션 업데이트
            user.setTel(dto.getTel());
            user.setEmail(dto.getEmail());
            ra.addFlashAttribute("msg", "연락처가 수정되었습니다.");
            return "redirect:/account";
        } catch (IllegalArgumentException e) {
            br.reject("contactError", e.getMessage());
            return "accountContact";
        }
    }

    // ✅ 비밀번호 변경 폼 (GET)
    @GetMapping("/password")
    public String passwordForm(Model model) {
        if (!model.containsAttribute("passwordChangeRequestDto")) {
            model.addAttribute("passwordChangeRequestDto", new UserPasswordChangeRequestDto());
        }
        return "accountPassword";
    }

    // 비밀번호 변경 처리 (POST)
    @PostMapping("/password")
    public String changePassword(@Valid @ModelAttribute("passwordChangeRequestDto") UserPasswordChangeRequestDto dto,
                                 BindingResult br,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        if (br.hasErrors()) {
            // 👉 에러 시 자기 페이지로 다시
            return "accountPassword";
        }
        User user = (User) session.getAttribute("user");
        try {
            accountService.changePassword(user.getId(), dto);
            ra.addFlashAttribute("msg", "비밀번호가 변경되었습니다. 다시 로그인해주세요.");
            session.invalidate();
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            br.reject("passwordError", e.getMessage());
            return "accountPassword";
        }
    }

    @GetMapping("/delete")
    public String deleteForm(@ModelAttribute("accountDeleteRequestDto") AccountDeleteRequestDto dto) {
        return "accountDelete";
    }

    // 🔒 탈퇴 처리
    @PostMapping("/delete")
    public String deleteAccount(@Valid @ModelAttribute("accountDeleteRequestDto") AccountDeleteRequestDto dto,
                                BindingResult br,
                                HttpSession session,
                                RedirectAttributes ra) {
        if (br.hasErrors()) {
            return "accountDelete";
        }
        User user = (User) session.getAttribute("user");
        try {
            accountService.deleteAccount(user.getId(), dto.getCurrentPassword());
            session.invalidate();
            ra.addFlashAttribute("msg", "탈퇴가 처리되었습니다.");
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            br.reject("deleteError", e.getMessage());
            return "accountDelete";
        }
    }
}

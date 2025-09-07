package com.example.airbnbproject.controller;

import com.example.airbnbproject.domain.Accommodation;
import com.example.airbnbproject.domain.User;
import com.example.airbnbproject.dto.AccommodationRequestDto;
import com.example.airbnbproject.service.AccommodationService;
import com.example.airbnbproject.repository.AccommodationRepository;
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
public class AccommodationManageController {

    private final AccommodationRepository accommodationRepository;
    private final AccommodationService accommodationService;

    // 수정 폼 띄우기 (권한 검증 포함)
    @GetMapping("/edit")
    public String editForm(@RequestParam Long id,
                           Model model,
                           HttpSession session,
                           RedirectAttributes ra) {
        User user = (User) session.getAttribute("user"); // 로그인은 인터셉터 보장
        try {
            Accommodation accommodation = accommodationService.getForEdit(id, user); // 권한 검증 + 조회
            AccommodationRequestDto dto = new AccommodationRequestDto(
                    accommodation.getName(),
                    accommodation.getPrice(),
                    accommodation.getImage()
            );
            model.addAttribute("accommodation", accommodation);
            model.addAttribute("accommodationRequestDto", dto);
            return "accommodationEdit";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("msg", e.getMessage());
            return "redirect:/account?tab=listings";
        }
    }

    // 수정 처리
    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("accommodationRequestDto") AccommodationRequestDto dto,
                         BindingResult bindingResult,
                         @RequestParam Long id,
                         HttpSession session,
                         Model model,
                         RedirectAttributes ra) {

        if (bindingResult.hasErrors()) {
            // 폼 재표시를 위해 상단 표시용 엔티티만 다시 전달
            model.addAttribute("accommodation",
                    accommodationRepository.findById(id).orElse(null));
            return "accommodationEdit";
        }

        User user = (User) session.getAttribute("user");
        try {
            accommodationService.updateAccommodation(id, dto, user); // 권한 검증은 서비스
            ra.addFlashAttribute("msg", "수정이 완료되었습니다.");
            return "redirect:/account?tab=listings";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("msg", e.getMessage());
            return "redirect:/account?tab=listings";
        } catch (Exception e) {
            ra.addFlashAttribute("msg", "수정 처리 중 오류가 발생했습니다.");
            return "redirect:/account?tab=listings";
        }
    }

    // 삭제 요청 (소유자만)
    @PostMapping("/delete")
    public String requestDelete(@RequestParam Long id,
                                HttpSession session,
                                RedirectAttributes ra) {
        User user = (User) session.getAttribute("user"); // 로그인은 인터셉터 보장
        try {
            accommodationService.requestDelete(id, user); // 소유자만 허용(서비스에서 검증)
            ra.addFlashAttribute("msg", "숙소 삭제 요청이 접수되었습니다.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("msg", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("msg", "삭제 요청 처리 중 오류가 발생했습니다.");
        }
        return "redirect:/account?tab=listings";
    }
}

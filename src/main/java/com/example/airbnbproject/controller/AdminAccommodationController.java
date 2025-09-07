package com.example.airbnbproject.controller;

import com.example.airbnbproject.domain.Accommodation;
import com.example.airbnbproject.domain.AccommodationStatus;
import com.example.airbnbproject.domain.User;
import com.example.airbnbproject.repository.AccommodationRepository;
import com.example.airbnbproject.repository.UserRepository;
import com.example.airbnbproject.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminAccommodationController {

    private final UserRepository userRepository;

    private final AccommodationRepository accommodationRepository;
    private final AccommodationService accommodationService;


    @PostMapping("/accommodations/approve/{id}")
    public String approveAccommodation(@PathVariable Long id,
                                       RedirectAttributes ra) {
        try {
            Accommodation accommodation = accommodationRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("숙소를 찾을 수 없습니다."));
            accommodation.setStatus(AccommodationStatus.APPROVED);
            accommodationRepository.save(accommodation);
            ra.addFlashAttribute("msg", "숙소 승인이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("msg", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("msg", "숙소 승인 처리 중 오류가 발생했습니다.");
        }
        return "redirect:/admin?tab=accommodations";
    }

    @PostMapping("/accommodations/reject/{id}")
    public String rejectAccommodation(@PathVariable Long id,
                                      RedirectAttributes ra) {
        try {
            Accommodation accommodation = accommodationRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("숙소를 찾을 수 없습니다."));
            accommodation.setStatus(AccommodationStatus.REJECTED);
            accommodationRepository.save(accommodation);
            ra.addFlashAttribute("msg", "숙소 반려가 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("msg", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("msg", "숙소 반려 처리 중 오류가 발생했습니다.");
        }
        return "redirect:/admin?tab=accommodations";
    }

    @PostMapping("/accommodation/delete/approve/{id}")
    public String approveDelete(@PathVariable Long id,
                                HttpSession session,
                                RedirectAttributes ra) {
        try {
            User user = (User) session.getAttribute("user"); // 인터셉터가 관리자 보장
            accommodationService.deleteApproved(id, user);    // 서비스에서 권한/상태 검증
            ra.addFlashAttribute("msg", "숙소가 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("msg", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("msg", "숙소 삭제 승인 중 오류가 발생했습니다.");
        }
        return "redirect:/admin?tab=accommodations";
    }

    @PostMapping("/accommodation/delete/cancel/{id}")
    public String cancelDeleteRequest(@PathVariable Long id,
                                      RedirectAttributes ra) {
        try {
            Accommodation ac = accommodationRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("숙소를 찾을 수 없습니다."));
            ac.setStatus(AccommodationStatus.APPROVED); // 삭제 취소 → 다시 승인 상태
            accommodationRepository.save(ac);
            ra.addFlashAttribute("msg", "숙소 삭제 요청이 취소되었습니다.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("msg", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("msg", "삭제 요청 취소 처리 중 오류가 발생했습니다.");
        }
        return "redirect:/admin?tab=accommodations";
    }
}

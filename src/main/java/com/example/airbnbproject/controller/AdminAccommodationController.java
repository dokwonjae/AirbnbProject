package com.example.airbnbproject.controller;


import com.example.airbnbproject.domain.Accommodation;
import com.example.airbnbproject.domain.AccommodationStatus;
import com.example.airbnbproject.repository.AccommodationRepository;
import com.example.airbnbproject.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminAccommodationController {

    private final AccommodationRepository accommodationRepository;
    private final AccommodationService accommodationService;

    // 관리자 대시보드
    @GetMapping
    public String dashboard() {
        return "adminDashboard";
    }

    // 숙소 전체 보기 (승인/반려/대기)
    @GetMapping("/accommodations")
    public String showAllForAdmin(Model model) {
        List<Accommodation> all = accommodationRepository.findAll();
        model.addAttribute("accommodationData", all);
        return "adminAccommodationList";
    }

    @PostMapping("/accommodations/approve/{id}")
    public String approveAccommodation(@PathVariable Long id) {
        Accommodation accommodation = accommodationRepository.findById(id).orElseThrow();
        accommodation.setStatus(AccommodationStatus.APPROVED);
        accommodationRepository.save(accommodation);
        return "redirect:/admin/accommodations";
    }

    @PostMapping("/accommodations/reject/{id}")
    public String rejectAccommodation(@PathVariable Long id) {
        Accommodation accommodation = accommodationRepository.findById(id).orElseThrow();
        accommodation.setStatus(AccommodationStatus.REJECTED);
        accommodationRepository.save(accommodation);
        return "redirect:/admin/accommodations";
    }

    @PostMapping("/accommodation/delete/approve/{id}")
    public String approveDelete(@PathVariable Long id, RedirectAttributes ra) {
        accommodationService.deleteApproved(id);
        ra.addFlashAttribute("msg", "숙소가 삭제되었습니다.");
        return "redirect:/admin/accommodations";
    }

    @PostMapping("/accommodation/delete/cancel/{id}")
    public String cancelDeleteRequest(@PathVariable Long id, RedirectAttributes ra) {
        Accommodation ac = accommodationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("숙소 없음"));

        ac.setStatus(AccommodationStatus.APPROVED); // 삭제 취소 → 다시 승인 상태로
        accommodationRepository.save(ac);

        ra.addFlashAttribute("msg", "삭제 요청이 취소되었습니다.");
        return "redirect:/admin/accommodations";
    }



}


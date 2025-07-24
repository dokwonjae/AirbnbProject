package com.example.airbnbproject.controller;

import com.example.airbnbproject.domain.Accommodation;
import com.example.airbnbproject.domain.User;
import com.example.airbnbproject.dto.AccommodationRequestDto;
import com.example.airbnbproject.repository.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@RequestMapping("/accommodation")
public class AccommodationManageController {

    private final AccommodationRepository accommodationRepository;

    // 수정 폼 띄우기
    @GetMapping("/edit")
    public String editForm(@RequestParam Long id, Model model, HttpSession session) {
        Accommodation accommodation = accommodationRepository.findById(id).orElse(null);
        if (accommodation == null) {
            model.addAttribute("msg", "숙소 정보를 찾을 수 없습니다.");
            return "redirect:/myPage";
        }

        model.addAttribute("accommodation", accommodation);
        return "accommodationEdit"; // 등록과 수정은 같은 JSP 사용
    }

    // 수정 처리
    @PostMapping("/update")
    public String update(@ModelAttribute AccommodationRequestDto dto,
                         @RequestParam Long id,
                         HttpSession session,
                         Model model) {

        User user = (User) session.getAttribute("user");
        Accommodation accommodation = accommodationRepository.findById(id).orElse(null);

        if (accommodation == null || user == null || !accommodation.getUser().getId().equals(user.getId())) {
            model.addAttribute("msg", "수정 권한이 없거나 숙소가 존재하지 않습니다.");
            return "redirect:/myPage";
        }

        accommodation.setName(dto.getName());
        accommodation.setPrice(dto.getPrice());
        accommodation.setView(dto.getView());
        accommodation.setImage(dto.getImage());

        accommodationRepository.save(accommodation);
        return "redirect:/myPage";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id,
                         HttpSession session,
                         RedirectAttributes ra) {

        User user = (User) session.getAttribute("user");
        Accommodation accommodation = accommodationRepository.findById(id).orElse(null);

        if (accommodation == null || user == null || !accommodation.getUser().getId().equals(user.getId())) {
            ra.addFlashAttribute("msg", "삭제 권한이 없거나 숙소가 존재하지 않습니다.");
            return "redirect:/myPage";
        }

        accommodationRepository.delete(accommodation);
        ra.addFlashAttribute("msg", "숙소가 성공적으로 삭제되었습니다.");
        return "redirect:/myPage";
    }

}
package com.example.airbnbproject.controller;

import com.example.airbnbproject.domain.Accommodation;
import com.example.airbnbproject.domain.AccommodationInfo;
import com.example.airbnbproject.domain.User;
import com.example.airbnbproject.domain.UserRole;
import com.example.airbnbproject.dto.AccommodationInfoRequestDto;
import com.example.airbnbproject.repository.AccommodationRepository;
import com.example.airbnbproject.service.AccommodationInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/info")
public class AccommodationInfoManageController {

    private final AccommodationRepository accommodationRepository;
    private final AccommodationInfoService accommodationInfoService;

    // 등록 폼 (소유자만 접근)
    @GetMapping("/register")
    public String showRegisterForm(@RequestParam Long accommodationId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user"); // 로그인은 인터셉터가 보장

        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 숙소입니다."));

        boolean isOwner = accommodation.getUser().getId().equals(user.getId());
        if (!isOwner) {
            return "redirect:/accommodation/" + accommodationId + "?error=notOwner";
        }

        if (accommodation.getAccommodationInfo() != null) {
            return "redirect:/accommodation/" + accommodationId + "?error=infoExists";
        }

        model.addAttribute("accommodationId", accommodationId);
        model.addAttribute("accommodationInfoRequestDto", new AccommodationInfoRequestDto());
        return "accommodationInfoForm";
    }

    // 등록 처리 (소유자만 가능)
    @PostMapping("/register")
    public String register(@RequestParam Long accommodationId,
                           @Valid @ModelAttribute AccommodationInfoRequestDto dto,
                           BindingResult bindingResult,
                           Model model,
                           HttpSession session) throws IOException {

        if (bindingResult.hasErrors()) {
            model.addAttribute("accommodationId", accommodationId);
            return "accommodationInfoForm";
        }

        User user = (User) session.getAttribute("user");

        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 숙소입니다."));

        boolean isOwner = accommodation.getUser().getId().equals(user.getId());
        if (!isOwner) {
            return "redirect:/accommodation/" + accommodationId + "?error=notOwner";
        }

        try {
            accommodationInfoService.saveAccommodationInfo(dto, accommodation);
            return "redirect:/accommodation/" + accommodationId;
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("saveInfoError", ex.getMessage());
            model.addAttribute("accommodationId", accommodationId);
            return "accommodationInfoForm";
        }
    }

    // 수정 폼 (소유자만 접근)
    @GetMapping("/edit/{infoId}")
    public String showEditForm(@PathVariable Long infoId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        AccommodationInfo info = accommodationInfoService.findById(infoId);

        boolean isOwner = info.getAccommodation().getUser().getId().equals(user.getId());
        if (!isOwner) {
            return "redirect:/accommodation/" + info.getAccommodation().getId() + "?error=notOwner";
        }

        AccommodationInfoRequestDto dto = accommodationInfoService.toDto(info);
        model.addAttribute("accommodationId", info.getAccommodation().getId());
        model.addAttribute("accommodationInfoRequestDto", dto);
        model.addAttribute("editing", true);
        return "accommodationInfoForm";
    }

    // 수정 처리 (소유자만 가능)
    @PostMapping("/edit/{infoId}")
    public String edit(@PathVariable Long infoId,
                       @Valid @ModelAttribute AccommodationInfoRequestDto dto,
                       BindingResult bindingResult,
                       Model model,
                       HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");
        AccommodationInfo info = accommodationInfoService.findById(infoId);
        Long accommodationId = info.getAccommodation().getId();

        boolean isOwner = info.getAccommodation().getUser().getId().equals(user.getId());
        if (!isOwner) {
            return "redirect:/accommodation/" + accommodationId + "?error=notOwner";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("accommodationId", accommodationId);
            model.addAttribute("editing", true);
            return "accommodationInfoForm";
        }

        try {
            accommodationInfoService.update(infoId, dto);
            return "redirect:/accommodation/" + accommodationId;
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("updateInfoError", ex.getMessage());
            model.addAttribute("accommodationId", accommodationId);
            model.addAttribute("editing", true);
            return "accommodationInfoForm";
        }
    }

    // 삭제 (소유자 또는 ADMIN 가능)
    @PostMapping("/delete/{infoId}")
    public String delete(@PathVariable Long infoId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        AccommodationInfo info = accommodationInfoService.findById(infoId);
        Long accommodationId = info.getAccommodation().getId();

        boolean isOwner = info.getAccommodation().getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == UserRole.ADMIN;

        if (!isOwner && !isAdmin) {
            return "redirect:/accommodation/" + accommodationId + "?error=notAuthorized";
        }

        accommodationInfoService.delete(infoId);
        return "redirect:/accommodation/" + accommodationId;
    }
}

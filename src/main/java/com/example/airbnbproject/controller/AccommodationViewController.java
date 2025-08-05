package com.example.airbnbproject.controller;

import com.example.airbnbproject.domain.*;
import com.example.airbnbproject.repository.AccommodationInfoRepository;
import com.example.airbnbproject.repository.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AccommodationViewController {

    private final AccommodationRepository accommodationRepository;
    private final AccommodationInfoRepository accommodationInfoRepository;

    @GetMapping("/")
    public String index(Model model) {
        List<Accommodation> list = accommodationRepository.findByStatus(AccommodationStatus.APPROVED);
        model.addAttribute("accommodationData", list);
        return "index";
    }

    @GetMapping("/accommodation/{id}")
    public String showAccommodationDetail(@PathVariable Long id, Model model, HttpSession session) {
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 숙소가 없습니다."));

        AccommodationInfo info = accommodationInfoRepository.findByAccommodationId(id);

        // ✅ 로그인 사용자 정보 가져오기
        User user = (User) session.getAttribute("user");
        boolean isOwner = user != null && user.getId().equals(accommodation.getUser().getId());
        boolean isAdmin = user != null && user.getRole().name().equals("ADMIN");

        // ✅ 승인된 숙소만 접근 가능 (단, 관리자와 숙소 등록자는 예외)
        if (accommodation.getStatus() != AccommodationStatus.APPROVED && !isOwner && !isAdmin) {
            return "redirect:/?error=notApproved";
        }

        // ✅ 이미지 경로 리스트 만들기 (Base64 or URL)
        List<String> imagePaths = new ArrayList<>();
        if (info != null && info.getImages() != null) {
            for (AccommodationInfoImage image : info.getImages()) {
                if (image.getImageData() != null && image.getImageData().length > 0) {
                    imagePaths.add("data:image/jpeg;base64," + encodeImage(image.getImageData()));
                } else if (image.getImageUrl() != null) {
                    imagePaths.add(image.getImageUrl());
                }
            }
        }

        model.addAttribute("accommodation", accommodation);
        model.addAttribute("info", info);
        model.addAttribute("imagePaths", imagePaths);

        return "accommodationInfo";
    }


    private String encodeImage(byte[] imageBytes) {
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}

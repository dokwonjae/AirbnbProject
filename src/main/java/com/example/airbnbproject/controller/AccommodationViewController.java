package com.example.airbnbproject.controller;

import com.example.airbnbproject.domain.Accommodation;
import com.example.airbnbproject.domain.AccommodationInfo;
import com.example.airbnbproject.domain.AccommodationInfoImage;
import com.example.airbnbproject.repository.AccommodationInfoRepository;
import com.example.airbnbproject.repository.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
        List<Accommodation> list = accommodationRepository.findAll();
        model.addAttribute("accommodationData", list);
        return "index";
    }

    @GetMapping("/accommodation/{id}")
    public String showAccommodationDetail(@PathVariable Long id, Model model) {
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 숙소가 없습니다."));

        AccommodationInfo info = accommodationInfoRepository.findByAccommodationId(id);

        // 이미지 경로 리스트 만들기 (Base64 or URL)
        List<String> imagePaths = new ArrayList<>();
        if (info != null && info.getImages() != null) {
            for (AccommodationInfoImage image : info.getImages()) {
                if (image.getImageData() != null && image.getImageData().length > 0) {
                    // Base64 변환
                    imagePaths.add("data:image/jpeg;base64," + encodeImage(image.getImageData()));
                } else if (image.getImageUrl() != null) {
                    // URL 직접 사용
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

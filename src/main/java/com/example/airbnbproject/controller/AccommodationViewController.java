package com.example.airbnbproject.controller;

import com.example.airbnbproject.domain.Accommodation;
import com.example.airbnbproject.repository.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AccommodationViewController {

    private final AccommodationRepository accommodationRepository;

    @GetMapping("/")
    public String index(Model model) {
        List<Accommodation> list = accommodationRepository.findAll();
        model.addAttribute("accommodationData", list);
        return "index";
    }
}

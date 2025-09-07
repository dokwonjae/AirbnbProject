package com.example.airbnbproject.controller;

import com.example.airbnbproject.dto.AdminUserRowDto;
import com.example.airbnbproject.repository.AccommodationRepository;
import com.example.airbnbproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final AccommodationRepository accommodationRepository;


    @GetMapping
    public String dashboard(@RequestParam(value = "tab", required = false) String tab,
                            @ModelAttribute("msg") String msg,
                            Model model) {
        model.addAttribute("accommodationData",
                accommodationRepository.findAdminAccommodationRows());

        List<AdminUserRowDto> users = userRepository.findAdminUserRows();
        model.addAttribute("userList", users);
        return "admin";
    }
}

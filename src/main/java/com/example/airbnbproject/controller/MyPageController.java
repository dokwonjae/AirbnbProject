package com.example.airbnbproject.controller;

import com.example.airbnbproject.domain.Accommodation;
import com.example.airbnbproject.domain.User;
import com.example.airbnbproject.repository.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final AccommodationRepository accommodationRepository;

    @GetMapping("/myPage")
    public String showMyPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        List<Accommodation> myAccommodations = accommodationRepository.findAllByUserId(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("myAccommodations", myAccommodations);

        return "myPage";
    }
}

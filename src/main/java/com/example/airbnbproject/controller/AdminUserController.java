package com.example.airbnbproject.controller;

import com.example.airbnbproject.repository.ReservationRepository;
import com.example.airbnbproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    // 회원별 예약 내역
    @GetMapping("/{userId}/reservations")
    public String reservations(@PathVariable Long userId, Model model) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        var list = reservationRepository.findMyReservationRowsByUserId(userId);

        model.addAttribute("targetUser", user);
        model.addAttribute("reservations", list);
        // 마이페이지에서 쓰던 방식과 동일하게 LocalDate 포맷터 제공
        model.addAttribute("dtf", DateTimeFormatter.ofPattern("yyyy.MM.dd"));

        // ✅ 새로 만드는 JSP 경로
        return "admin/userReservations";
    }
}
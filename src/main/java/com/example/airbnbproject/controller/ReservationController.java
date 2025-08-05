package com.example.airbnbproject.controller;

import com.example.airbnbproject.domain.Reservation;
import com.example.airbnbproject.domain.User;
import com.example.airbnbproject.dto.ReservationRequestDto;
import com.example.airbnbproject.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public String createReservation(@ModelAttribute ReservationRequestDto dto, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Reservation reservation = reservationService.createReservation(dto, user);
        return "redirect:/reservation/" + reservation.getId();
    }


    // 예약 상세 조회 (로그인 유저 + 본인 예약만)
    @GetMapping("/{id}")
    public String showReservation(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Reservation reservation = reservationService.findByIdAndUser(id, user);
        model.addAttribute("reservation", reservation);
        return "reservation"; // => reservation.jsp
    }

}

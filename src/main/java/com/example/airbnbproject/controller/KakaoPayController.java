package com.example.airbnbproject.controller;

import com.example.airbnbproject.domain.User;
import com.example.airbnbproject.service.KakaoPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class KakaoPayController {

    private final KakaoPayService kakaoPayService;

    @PostMapping("/kakao")
    public String kakaoPay(@RequestParam Long reservationId,
                           @RequestParam int amount,
                           HttpSession session) {
        User user = (User) session.getAttribute("user");
        String redirectUrl = kakaoPayService.kakaoPayReady(reservationId, user.getId().toString(), amount, session);
        return "redirect:" + redirectUrl;
    }



    @GetMapping("/success")
    public String kakaoPaySuccess(@RequestParam("pg_token") String pgToken,
                                  @RequestParam("reservationId") String reservationId,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        kakaoPayService.kakaoPayApprove(pgToken, reservationId.toString(), user.getId().toString(), session);
        redirectAttributes.addFlashAttribute("message", "결제가 성공적으로 완료되었습니다!");
        return "redirect:/mypage/reservation/" + reservationId;
    }

    @GetMapping("/cancel")
    public String cancelPayment(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "결제가 취소되었습니다.");
        return "redirect:/";
    }

    @GetMapping("/fail")
    public String failPayment(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "결제에 실패했습니다.");
        return "redirect:/";
    }
}

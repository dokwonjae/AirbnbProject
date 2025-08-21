package com.example.airbnbproject.controller;

import com.example.airbnbproject.domain.User;
import com.example.airbnbproject.dto.KakaoPayApproveFormDto;
import com.example.airbnbproject.dto.KakaoPayApproveResponseDto;
import com.example.airbnbproject.dto.KakaoPayRequestDto;
import com.example.airbnbproject.service.KakaoPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class KakaoPayController {

    private final KakaoPayService kakaoPayService;

    @PostMapping("/kakao")
    public String kakaoPay(@ModelAttribute KakaoPayRequestDto dto,
                           HttpSession session) {
        User user = (User) session.getAttribute("user");

        String redirectUrl = kakaoPayService.kakaoPayReady(dto, user.getId().toString(), session);
        return "redirect:" + redirectUrl;
    }



    @GetMapping("/success")
    public String kakaoPaySuccess(@RequestParam("pg_token") String pgToken,
                                  @RequestParam("reservationId") String reservationId,
                                  HttpSession session,
                                  RedirectAttributes ra) {

        User user = (User) session.getAttribute("user");

        // 콜백 파라미터를 폼 DTO로 묶기
        KakaoPayApproveFormDto form = KakaoPayApproveFormDto.of(pgToken, reservationId);

        // 서비스는 폼 DTO를 받아서 카카오 요청/응답 DTO로 변환·호출
        KakaoPayApproveResponseDto resp =
                kakaoPayService.kakaoPayApprove(form, user.getId().toString(), session);

        ra.addFlashAttribute("message", "결제가 성공적으로 완료되었습니다!");
        return "redirect:/reservation/" + reservationId;
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

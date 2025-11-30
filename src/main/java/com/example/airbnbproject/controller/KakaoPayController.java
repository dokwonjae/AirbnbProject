package com.example.airbnbproject.controller;

import com.example.airbnbproject.domain.User;
import com.example.airbnbproject.dto.KakaoPayApproveFormDto;
import com.example.airbnbproject.dto.KakaoPayApproveResponseDto;
import com.example.airbnbproject.dto.KakaoPayReadyResponseDto;
import com.example.airbnbproject.dto.KakaoPayRequestDto;
import com.example.airbnbproject.service.KakaoPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class KakaoPayController {

    private final KakaoPayService kakaoPayService;

    @PostMapping("/kakao")
    public String kakaoPay(@Valid @ModelAttribute KakaoPayRequestDto dto,
                           BindingResult binding,
                           HttpSession session,
                           HttpServletRequest request,
                           RedirectAttributes ra) {

        if (binding.hasErrors()) {
            // 필드별 메시지를 플래시에 싣고 이전 화면으로
            ra.addFlashAttribute("fieldErrors", binding.getFieldErrors());
            ra.addFlashAttribute("errorMsg", "결제 요청 값이 유효하지 않습니다.");
            String back = Optional.ofNullable(request.getHeader("Referer")).orElse("/");
            return "redirect:" + back;
        }

        User user = (User) session.getAttribute("user");

        // 1) 서비스에서 Ready 요청 → 전체 DTO 받기
        KakaoPayReadyResponseDto ready =
                kakaoPayService.kakaoPayReady(dto, user.getId().toString(), session);

        // 2) UA 기반 간단 모바일 판별
        String ua = Optional.ofNullable(request.getHeader("User-Agent")).orElse("");
        boolean isMobile = ua.contains("Mobi") || ua.contains("Android") || ua.contains("iPhone");

        // 3) 모바일이면 app→mobile→pc 우선순위로 선택, PC는 pc 고정
        String redirectUrl = isMobile
                ? Optional.ofNullable(ready.getNextRedirectAppUrl())
                .or(() -> Optional.ofNullable(ready.getNextRedirectMobileUrl()))
                .orElse(ready.getNextRedirectPcUrl())
                : ready.getNextRedirectPcUrl();

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

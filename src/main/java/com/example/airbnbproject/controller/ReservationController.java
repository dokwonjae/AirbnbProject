package com.example.airbnbproject.controller;

import com.example.airbnbproject.domain.*;
import com.example.airbnbproject.dto.DisabledDateRangeDto;
import com.example.airbnbproject.dto.ReservationDetailResponseDto;
import com.example.airbnbproject.dto.ReservationRequestDto;
import com.example.airbnbproject.repository.AccommodationRepository;
import com.example.airbnbproject.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;
    private final AccommodationRepository accommodationRepository;

    @PostMapping
    public String createReservation(@Valid @ModelAttribute("reservationRequestDto") ReservationRequestDto dto,
                                    BindingResult br,
                                    HttpSession session,
                                    Model model) {
        if (br.hasErrors()) {
            return forwardToAccommodationView(dto, model);
        }

        User user = (User) session.getAttribute("user");
        try {
            Reservation reservation = reservationService.createReservation(dto, user);
            return "redirect:/reservation/" + reservation.getId(); // 성공은 PRG
        } catch (IllegalArgumentException e) {
            // (B) 유효성 실패(인원 초과/날짜 오류 등) → 글로벌 에러로 표시
            br.reject("reserveError", e.getMessage());
            return forwardToAccommodationView(dto, model);
        } catch (IllegalStateException e) {
            // (C) 기간 겹침 등 상태 오류 → 글로벌 에러로 표시
            br.reject("overlapError", e.getMessage());
            return forwardToAccommodationView(dto, model);
        }
    }

    // 예약 불가 날짜 API (기존 유지)
    @GetMapping("/accommodation/{id}/booked")
    @ResponseBody
    public List<DisabledDateRangeDto> bookedDates(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return reservationService.getDisabledRanges(id, from, to);
    }

    // 예약 상세 (기존 유지)
    @GetMapping("/{id}")
    public String showReservation(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        Reservation reservation = reservationService.findByIdAndUser(id, user);
        model.addAttribute("reservation", new ReservationDetailResponseDto(reservation));
        return "reservation";
    }

    private String forwardToAccommodationView(ReservationRequestDto dto, Model model) {
        Accommodation acc = accommodationRepository.findById(dto.getAccommodationId())
                .orElseThrow(() -> new IllegalArgumentException("숙소가 존재하지 않습니다."));

        // 상세 페이지가 필요로 하는 모델 채워주기
        model.addAttribute("accommodation", acc);
        model.addAttribute("info", acc.getAccommodationInfo());
        model.addAttribute("imagePaths", buildImagePaths(acc)); // ✅ Base64/URL 혼용이 아닌 Base64 우선(있으면)으로 통일
        // 폼 모델 유지(사용자 입력값 & BindingResult를 JSP에서 사용)
        model.addAttribute("reservationRequestDto", dto);

        return "accommodationInfo"; // 숙소 상세 JSP 이름
    }

    // ✅ AccommodationViewController 방식과 동일한 로직으로 통일
    private List<String> buildImagePaths(Accommodation acc) {
        AccommodationInfo info = acc.getAccommodationInfo();
        if (info == null || info.getImages() == null) return List.of();

        List<String> list = new ArrayList<>();
        for (AccommodationInfoImage img : info.getImages()) {
            if (img.getImageData() != null && img.getImageData().length > 0) {
                String base64 = Base64.getEncoder().encodeToString(img.getImageData());
                list.add("data:image/jpeg;base64," + base64);
            } else if (img.getImageUrl() != null && !img.getImageUrl().isEmpty()) {
                list.add(img.getImageUrl());
            }
        }
        return list;
    }

    @PostMapping("/{id}/cancel")
    public String cancelReservation(@PathVariable Long id,
                                    HttpSession session,
                                    RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        try {
            reservationService.cancelOrRefund(id, user); // ▼ 2) 서비스 구현
            ra.addFlashAttribute("msg", "예약이 취소되었습니다.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("msg", e.getMessage());
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("msg", "결제 취소 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }
        return "redirect:/reservation/" + id;
    }
}

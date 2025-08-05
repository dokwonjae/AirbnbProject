package com.example.airbnbproject.service;

import com.example.airbnbproject.config.KakaoPayConfig;
import com.example.airbnbproject.domain.*;
import com.example.airbnbproject.repository.PaymentRepository;
import com.example.airbnbproject.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class KakaoPayService {

    private final KakaoPayConfig kakaoPayConfig;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * ✅ 결제 준비 요청 (신형 API)
     */
    public String kakaoPayReady(Long reservationId, String userId, int amount, HttpSession session) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보가 없습니다."));

        reservation.setStatus(ReservationStatus.RESERVED);
        reservationRepository.save(reservation);

        Map<String, Object> body = new HashMap<>();
        body.put("cid", kakaoPayConfig.getCid());
        body.put("partner_order_id", reservationId.toString());
        body.put("partner_user_id", userId);
        body.put("item_name", "숙소 예약");
        body.put("quantity", 1);
        body.put("total_amount", amount);
        body.put("tax_free_amount", 0);
        body.put("approval_url", kakaoPayConfig.getApprovalUrl() + "?reservationId=" + reservationId);
        body.put("cancel_url", kakaoPayConfig.getCancelUrl());
        body.put("fail_url", kakaoPayConfig.getFailUrl());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY " + kakaoPayConfig.getSecretKey());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        String url = kakaoPayConfig.getHost() + "/v1/payment/ready";

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        Map<String, Object> responseBody = response.getBody();

        if (responseBody != null && responseBody.containsKey("tid")) {
            session.setAttribute("tid", responseBody.get("tid"));
            session.setAttribute("reservationId", reservationId);
            return responseBody.get("next_redirect_pc_url").toString();
        }

        throw new IllegalStateException("카카오페이 결제 준비 실패");
    }

    /**
     * ✅ 결제 승인 처리 + 결과 리턴
     */
    public Map<String, Object> kakaoPayApprove(String pgToken, String reservationIdStr, String userId, HttpSession session) {
        String tid = (String) session.getAttribute("tid");
        Long reservationId = Long.parseLong(reservationIdStr);

        if (tid == null || reservationId == null) {
            throw new IllegalStateException("세션에 결제 정보가 없습니다.");
        }

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보가 없습니다."));

        Map<String, Object> body = new HashMap<>();
        body.put("cid", kakaoPayConfig.getCid());
        body.put("tid", tid);
        body.put("partner_order_id", reservationId.toString());
        body.put("partner_user_id", userId);
        body.put("pg_token", pgToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY " + kakaoPayConfig.getSecretKey());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        String url = kakaoPayConfig.getHost() + "/v1/payment/approve";

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map<String, Object> responseBody = response.getBody();

            reservation.setStatus(ReservationStatus.PAID);
            reservationRepository.save(reservation);

            Payment payment = new Payment();
            payment.setReservation(reservation);
            payment.setStatus(PaymentStatus.PAID);
            payment.setTid(tid);
            payment.setAmount(reservation.getTotalAmount());
            payment.setPaymentDate(LocalDateTime.now()); // ✅ 정확한 필드명 사용
            paymentRepository.save(payment);

            return responseBody;
        } catch (Exception e) {
            handlePaymentFail(reservation.getId());
            throw new IllegalStateException("카카오페이 결제 승인 실패", e);
        } finally {
            session.removeAttribute("tid");
            session.removeAttribute("reservationId");
        }
    }

    public void handlePaymentCancel(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보가 없습니다."));

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);
    }

    public void handlePaymentFail(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보가 없습니다."));

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setStatus(PaymentStatus.FAILED);
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);
    }

}

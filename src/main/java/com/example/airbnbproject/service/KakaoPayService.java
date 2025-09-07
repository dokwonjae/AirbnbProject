package com.example.airbnbproject.service;

import com.example.airbnbproject.config.KakaoPayConfig;
import com.example.airbnbproject.domain.Payment;
import com.example.airbnbproject.domain.PaymentStatus;
import com.example.airbnbproject.domain.Reservation;
import com.example.airbnbproject.domain.ReservationStatus;
import com.example.airbnbproject.dto.*;
import com.example.airbnbproject.repository.PaymentRepository;
import com.example.airbnbproject.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Service
public class KakaoPayService {

    private final KakaoPayConfig kakaoPayConfig;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    /** 공통: 낙관락 재시도 */
    private <T> T retryOptimistic(Callable<T> work) {
        int attempts = 0;
        while (true) {
            try {
                return work.call();
            } catch (ObjectOptimisticLockingFailureException e) {
                if (++attempts >= 3) throw e;
                try { Thread.sleep(50L * attempts); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            } catch (RuntimeException re) {
                throw re; // 그대로 전파
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /** 결제 준비 */
    @Transactional
    public String kakaoPayReady(KakaoPayRequestDto dto, String userId, HttpSession session) {
        Long reservationId = dto.getReservationId();

        // 상태 검증 & (필요 시) RESERVED 셋팅 — 낙관락 재시도
        retryOptimistic(() -> {
            Reservation r = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new IllegalArgumentException("예약 정보가 없습니다."));

            if (r.getStatus() == ReservationStatus.PAID) {
                throw new IllegalStateException("이미 결제 완료된 예약입니다.");
            }
            // 보통 createReservation 시 RESERVED로 저장되어 있음. 멱등 처리.
            if (r.getStatus() != ReservationStatus.RESERVED) {
                r.setStatus(ReservationStatus.RESERVED);
            }
            return null;
        });

        // 카카오 요청 DTO
        Reservation reservation = reservationRepository.findById(reservationId).get();

        KakaoPayReadyRequestDto requestDto = new KakaoPayReadyRequestDto();
        requestDto.setCid(kakaoPayConfig.getCid());
        requestDto.setPartner_order_id(reservationId.toString());
        requestDto.setPartner_user_id(userId);
        requestDto.setItem_name(reservation.getAccommodation().getName());
        requestDto.setQuantity(1);
        requestDto.setTotal_amount(reservation.getTotalAmount());
        requestDto.setTax_free_amount(0);
        requestDto.setApproval_url(kakaoPayConfig.getApprovalUrl() + "?reservationId=" + reservationId);
        requestDto.setCancel_url(kakaoPayConfig.getCancelUrl());
        requestDto.setFail_url(kakaoPayConfig.getFailUrl());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY " + kakaoPayConfig.getSecretKey());
        HttpEntity<KakaoPayReadyRequestDto> request = new HttpEntity<>(requestDto, headers);

        ResponseEntity<KakaoPayReadyResponseDto> response =
                restTemplate.postForEntity(kakaoPayConfig.getHost() + "/v1/payment/ready",
                        request, KakaoPayReadyResponseDto.class);

        KakaoPayReadyResponseDto body = response.getBody();
        if (body != null && body.getTid() != null) {
            session.setAttribute("tid", body.getTid());
            session.setAttribute("reservationId", reservationId);
            return body.getNextRedirectPcUrl();
        }
        throw new IllegalStateException("카카오페이 결제 준비 실패");
    }

    /** 결제 승인 */
    @Transactional
    public KakaoPayApproveResponseDto kakaoPayApprove(KakaoPayApproveFormDto form,
                                                      String userId,
                                                      HttpSession session) {
        String tid = (String) session.getAttribute("tid");
        if (tid == null) throw new IllegalStateException("세션에 결제 정보가 없습니다.");

        Long reservationId = Long.parseLong(form.getReservationId());
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보가 없습니다."));

        // 이미 결제 완료면 멱등 처리(그냥 통과)
        if (reservation.getStatus() == ReservationStatus.PAID) {
            session.removeAttribute("tid");
            session.removeAttribute("reservationId");

            KakaoPayApproveResponseDto ok = new KakaoPayApproveResponseDto();
            ok.setTid(tid);
            return ok;
        }

        KakaoPayApproveRequestDto approveReq = new KakaoPayApproveRequestDto(
                kakaoPayConfig.getCid(),
                tid,
                reservationId.toString(),
                userId,
                form.getPgToken()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY " + kakaoPayConfig.getSecretKey());
        HttpEntity<KakaoPayApproveRequestDto> request = new HttpEntity<>(approveReq, headers);
        String url = kakaoPayConfig.getHost() + "/v1/payment/approve";

        try {
            ResponseEntity<KakaoPayApproveResponseDto> response =
                    restTemplate.postForEntity(url, request, KakaoPayApproveResponseDto.class);

            KakaoPayApproveResponseDto body = response.getBody();
            if (body == null || body.getTid() == null) {
                handlePaymentFailInternal(reservationId);
                throw new IllegalStateException("카카오페이 결제 승인 응답이 비어있거나 필수 필드가 누락되었습니다.");
            }

            // ✅ 낙관락 + 재시도로 상태 변경 (PAID)
            retryOptimistic(() -> {
                Reservation r = reservationRepository.findById(reservationId)
                        .orElseThrow(() -> new IllegalArgumentException("예약 정보가 없습니다."));
                if (r.getStatus() != ReservationStatus.PAID) {
                    r.setStatus(ReservationStatus.PAID);
                }
                return null;
            });

            // ✅ 멱등 저장: 같은 TID가 이미 저장되어 있으면 스킵
            if (!paymentRepository.existsByTid(tid)) {
                Reservation paid = reservationRepository.findById(reservationId).get();

                Payment payment = new Payment();
                payment.setReservation(paid);
                payment.setStatus(PaymentStatus.PAID);
                payment.setTid(tid);
                payment.setAmount(paid.getTotalAmount());
                payment.setPaymentDate(LocalDateTime.now());
                paymentRepository.save(payment);
            }

            return body;
        } catch (Exception e) {
            handlePaymentFailInternal(reservationId);
            throw new IllegalStateException("카카오페이 결제 승인 실패", e);
        } finally {
            session.removeAttribute("tid");
            session.removeAttribute("reservationId");
        }
    }

    @Transactional
    public void kakaoPayCancel(Reservation reservation) {
        // 1) 직전 결제건(TID) 찾기
        Payment paid = paymentRepository
                .findTopByReservationAndStatusOrderByPaymentDateDesc(reservation, PaymentStatus.PAID)
                .orElseThrow(() -> new IllegalStateException("결제 이력이 없어 환불할 수 없습니다."));

        // 2) 카카오 취소 요청
        KakaoPayCancelRequestDto reqDto = new KakaoPayCancelRequestDto(
                kakaoPayConfig.getCid(),
                paid.getTid(),
                reservation.getTotalAmount(),  // 전액 환불
                0
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY " + kakaoPayConfig.getSecretKey());

        HttpEntity<KakaoPayCancelRequestDto> request = new HttpEntity<>(reqDto, headers);
        String url = kakaoPayConfig.getHost() + "/v1/payment/cancel"; // 신버전 경로

        ResponseEntity<KakaoPayCancelResponseDto> resp =
                restTemplate.postForEntity(url, request, KakaoPayCancelResponseDto.class);

        KakaoPayCancelResponseDto body = resp.getBody();
        if (body == null || body.getTid() == null) {
            throw new IllegalStateException("카카오페이 환불에 실패했습니다.");
        }

        // 3) 환불 로그 적재 (상태 기록)
        Payment refund = new Payment();
        refund.setReservation(reservation);
        refund.setStatus(PaymentStatus.CANCELED);
        refund.setTid(paid.getTid());
        refund.setAmount(reservation.getTotalAmount());
        refund.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(refund);
    }

    /** 사용자가 결제 페이지에서 취소를 누른 경우 등 */
    @Transactional
    public void handlePaymentCancel(Long reservationId) {
        // 실제 환불 API가 아니라 '승인 전 취소' 상황 가정
        retryOptimistic(() -> {
            Reservation r = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new IllegalArgumentException("예약 정보가 없습니다."));
            if (r.getStatus() != ReservationStatus.CANCELED) {
                r.setStatus(ReservationStatus.CANCELED);
            }
            return null;
        });

        // 취소 로그 적재 (tid 없음)
        Reservation resv = reservationRepository.findById(reservationId).get();
        Payment payment = new Payment();
        payment.setReservation(resv);
        payment.setStatus(PaymentStatus.CANCELED);
        payment.setAmount(resv.getTotalAmount());
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);
    }

    /** 승인 실패 처리(네트워크 오류 등) */
    @Transactional
    public void handlePaymentFail(Long reservationId) {
        handlePaymentFailInternal(reservationId);
    }

    private void handlePaymentFailInternal(Long reservationId) {
        retryOptimistic(() -> {
            Reservation r = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new IllegalArgumentException("예약 정보가 없습니다."));
            if (r.getStatus() != ReservationStatus.CANCELED) {
                r.setStatus(ReservationStatus.CANCELED);
            }
            return null;
        });

        // 실패 로그 적재 (tid 없음)
        Reservation resv = reservationRepository.findById(reservationId).get();
        Payment payment = new Payment();
        payment.setReservation(resv);
        payment.setStatus(PaymentStatus.FAILED);
        payment.setAmount(resv.getTotalAmount());
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);
    }
}

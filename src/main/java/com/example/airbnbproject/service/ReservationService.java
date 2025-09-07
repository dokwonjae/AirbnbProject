package com.example.airbnbproject.service;

import com.example.airbnbproject.domain.*;
import com.example.airbnbproject.dto.DisabledDateRangeDto;
import com.example.airbnbproject.dto.ReservationRequestDto;
import com.example.airbnbproject.repository.AccommodationRepository;
import com.example.airbnbproject.repository.PaymentRepository;
import com.example.airbnbproject.repository.ReservationRepository;
import com.example.airbnbproject.support.RedisLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final AccommodationRepository accommodationRepository;
    private final RedisLockService redisLockService;
    private final PaymentRepository paymentRepository;
    private final KakaoPayService kakaoPayService;

    @Transactional
    public Reservation createReservation(ReservationRequestDto dto, User user) {

        if (dto.getCheckIn() == null || dto.getCheckOut() == null) {
            throw new IllegalArgumentException("체크인/체크아웃 날짜가 올바르지 않습니다.");
        }
        if (!dto.getCheckOut().isAfter(dto.getCheckIn())) {
            throw new IllegalArgumentException("체크아웃은 체크인보다 이후여야 합니다.");
        }
        if (dto.getGuestCount() == null || dto.getGuestCount() < 1) {
            throw new IllegalArgumentException("인원은 1명 이상이어야 합니다.");
        }

        // 숙소 로드
        Accommodation accommodation = accommodationRepository.findById(dto.getAccommodationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 숙소입니다."));

        if (accommodation.getStatus() != AccommodationStatus.APPROVED) {
            throw new IllegalArgumentException("승인되지 않은 숙소는 예약할 수 없습니다.");
        }

        // 분산 락: 숙소 단위
        final String lockKey = "accommodation:" + accommodation.getId();

        return redisLockService.withLock(lockKey, () -> {

            int capacity = (accommodation.getAccommodationInfo() != null)
                    ? accommodation.getAccommodationInfo().getPersonnel()
                    : Integer.MAX_VALUE;
            if (dto.getGuestCount() > capacity) {
                throw new IllegalArgumentException("최대 인원(" + capacity + "명)을 초과했습니다.");
            }

            // (1) 기간 겹침 검사
            boolean overlap = reservationRepository.existsOverlapping(
                    accommodation.getId(),
                    dto.getCheckIn(),
                    dto.getCheckOut(),
                    List.of(ReservationStatus.RESERVED, ReservationStatus.PAID)
            );
            if (overlap) {
                throw new IllegalStateException("해당 기간에는 이미 예약이 존재합니다.");
            }

            // (2) 숙박일수 계산
            long days = ChronoUnit.DAYS.between(dto.getCheckIn(), dto.getCheckOut());
            if (days <= 0) {
                throw new IllegalArgumentException("숙박일수는 1일 이상이어야 합니다.");
            }

            // (3) 예약 생성/저장
            Reservation reservation = new Reservation();
            reservation.setUser(user);
            reservation.setAccommodation(accommodation);
            reservation.setCheckIn(dto.getCheckIn());
            reservation.setCheckOut(dto.getCheckOut());
            reservation.setGuestCount(dto.getGuestCount());
            reservation.setStatus(ReservationStatus.RESERVED);
            reservation.setTotalAmount((int) (accommodation.getPrice() * days));

            return reservationRepository.save(reservation);
        });
    }

    @Transactional(readOnly = true)
    public List<DisabledDateRangeDto> getDisabledRanges(Long accId, LocalDate from, LocalDate to) {
        List<Reservation> list = reservationRepository.findOverlapping(
                accId, from, to, List.of(ReservationStatus.RESERVED, ReservationStatus.PAID));

        // [checkIn, checkOut) → [checkIn, checkOut-1] 로 내려줌
        return list.stream()
                .map(r -> new DisabledDateRangeDto(
                        r.getCheckIn(),
                        r.getCheckOut().minusDays(1)
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Reservation findByIdAndUser(Long id, User user) {
        return reservationRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보가 없거나 접근 권한이 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<Reservation> findByUser(User user) {
        return reservationRepository.findByUser(user);
    }

    @Transactional
    public void cancelOrRefund(Long reservationId, User user) {
        Reservation r = reservationRepository.findByIdAndUser(reservationId, user)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보가 없거나 권한이 없습니다."));

        switch (r.getStatus()) {
            case RESERVED:
                // 결제 전 취소: 단순 상태 변경
                r.setStatus(ReservationStatus.CANCELED);
                break;

            case PAID:
                // 결제 후 취소: 카카오페이 환불 먼저 시도 → 성공 시 예약 취소
                kakaoPayService.kakaoPayCancel(r);
                r.setStatus(ReservationStatus.CANCELED);
                break;

            default:
                throw new IllegalStateException("해당 상태에서는 취소할 수 없습니다. (현재: " + r.getStatus() + ")");
        }
    }
}

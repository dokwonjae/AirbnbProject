package com.example.airbnbproject.service;

import com.example.airbnbproject.domain.Accommodation;
import com.example.airbnbproject.domain.Reservation;
import com.example.airbnbproject.domain.ReservationStatus;
import com.example.airbnbproject.domain.User;
import com.example.airbnbproject.dto.ReservationRequestDto;
import com.example.airbnbproject.repository.AccommodationRepository;
import com.example.airbnbproject.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final AccommodationRepository accommodationRepository;

    // 예약 생성
    public Reservation createReservation(ReservationRequestDto dto, User user) {
        Accommodation accommodation = accommodationRepository.findById(dto.getAccommodationId())
                .orElseThrow(() -> new IllegalArgumentException("숙소가 존재하지 않습니다."));

        // ✅ 날짜 유효성 검증 (백엔드)
        if (dto.getCheckIn().isBefore(LocalDate.now()) || dto.getCheckOut().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("예약 날짜는 오늘 이후여야 합니다.");
        }

        long days = ChronoUnit.DAYS.between(dto.getCheckIn(), dto.getCheckOut());
        if (days <= 0) {
            throw new IllegalArgumentException("숙박일수는 1일 이상이어야 합니다.");
        }

        // ✅ 예약 객체 생성 및 설정
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setAccommodation(accommodation);
        reservation.setCheckIn(dto.getCheckIn());
        reservation.setCheckOut(dto.getCheckOut());
        reservation.setGuestCount(dto.getGuestCount());
        reservation.setStatus(ReservationStatus.RESERVED);
        reservation.setTotalAmount((int)(accommodation.getPrice() * days));  // ✅ 총 금액 계산

        return reservationRepository.save(reservation);
    }


    // 특정 예약 조회 (유저 검증 포함)
    public Reservation findByIdAndUser(Long id, User user) {
        return reservationRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보가 없거나 접근 권한이 없습니다."));
    }

    // 해당 유저의 모든 예약 리스트
    public List<Reservation> findByUser(User user) {
        return reservationRepository.findByUser(user);
    }
}

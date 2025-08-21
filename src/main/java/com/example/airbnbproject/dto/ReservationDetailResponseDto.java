package com.example.airbnbproject.dto;

import com.example.airbnbproject.domain.Reservation;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ReservationDetailDto {

    private final Long id;

    // 숙소 정보
    private final Long accommodationId;
    private final String accommodationName;

    // 날짜
    private final LocalDate checkIn;
    private final LocalDate checkOut;

    // 인원/금액/상태
    private final int guestCount;
    private final int totalAmount;     // 총 금액(예약 시점 계산값)
    private final String status;       // RESERVED/PAID 등

    public ReservationDetailDto(Reservation res) {
        this.id = res.getId();

        this.accommodationId = res.getAccommodation().getId();
        this.accommodationName = res.getAccommodation().getName();

        this.checkIn = res.getCheckIn();
        this.checkOut = res.getCheckOut();

        this.guestCount = res.getGuestCount();
        this.totalAmount = res.getTotalAmount();
        this.status = res.getStatus().name();
    }
}

package com.example.airbnbproject.dto;

import com.example.airbnbproject.domain.Reservation;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class ReservationDetailDto {
    private final Long id;
    private final String accommodationName;
    private final String checkIn;
    private final String checkOut;
    private final int price;
    private final String status;

    public ReservationDetailDto(Reservation res) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.id = res.getId();
        this.accommodationName = res.getAccommodation().getName();
        this.checkIn = res.getCheckIn().format(fmt);
        this.checkOut = res.getCheckOut().format(fmt);
        this.price = res.getAccommodation().getPrice();
        this.status = res.getStatus().name();
    }
}


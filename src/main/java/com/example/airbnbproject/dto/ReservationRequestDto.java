package com.example.airbnbproject.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class ReservationRequestDto {

    private Long accommodationId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkIn;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkOut;

    private int guestCount;
}

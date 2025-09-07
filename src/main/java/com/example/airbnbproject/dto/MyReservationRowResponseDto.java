package com.example.airbnbproject.dto;

import com.example.airbnbproject.domain.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class MyReservationRowResponseDto {
    private Long id;
    private String accommodationName;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private ReservationStatus status;
}

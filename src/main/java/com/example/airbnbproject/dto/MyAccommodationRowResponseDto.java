package com.example.airbnbproject.dto;

import com.example.airbnbproject.domain.AccommodationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyAccommodationRowResponseDto {
    private Long id;
    private String name;
    private int price;
    private AccommodationStatus status;
}

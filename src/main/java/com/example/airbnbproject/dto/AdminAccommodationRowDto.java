package com.example.airbnbproject.dto;

import com.example.airbnbproject.domain.AccommodationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminAccommodationRowDto {
    private final Long id;
    private final String name;
    private final String hostLoginId;
    private final AccommodationStatus status;


}


package com.example.airbnbproject.domain;

import com.example.airbnbproject.domain.Accommodation;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class AccommodationAvailableDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private boolean isBooked = false; // 예약 여부

    @ManyToOne
    @JoinColumn(name = "accommodation_id")
    private Accommodation accommodation;
}


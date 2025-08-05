package com.example.airbnbproject.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Accommodation accommodation;

    private LocalDate checkIn;

    private LocalDate checkOut;

    private int guestCount;

    private int totalAmount;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

}



package com.example.airbnbproject.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(
        indexes = {
                @Index(name = "idx_payment_reservation", columnList = "reservation_id"),
                @Index(name = "idx_payment_tid",        columnList = "tid")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_payment_tid_status", columnNames = {"tid", "status"})
        }
)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(length = 100, nullable = false)
    private String tid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;
}

package com.example.airbnbproject.repository;

import com.example.airbnbproject.domain.Payment;
import com.example.airbnbproject.domain.PaymentStatus;
import com.example.airbnbproject.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByTid(String tid);
    Optional<Payment> findTopByReservationAndStatusOrderByPaymentDateDesc(Reservation reservation, PaymentStatus status);
}


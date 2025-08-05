package com.example.airbnbproject.repository;

import com.example.airbnbproject.domain.Reservation;
import com.example.airbnbproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByIdAndUser(Long id, User user);
    List<Reservation> findByUser(User user);
}

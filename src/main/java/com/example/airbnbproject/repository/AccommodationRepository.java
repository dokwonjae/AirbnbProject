package com.example.airbnbproject.repository;

import com.example.airbnbproject.domain.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
    List<Accommodation> findAllByUserId(Long userId);
}

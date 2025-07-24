package com.example.airbnbproject.repository;

import com.example.airbnbproject.domain.AccommodationInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationInfoRepository extends JpaRepository<AccommodationInfo, Long> {
    AccommodationInfo findByAccommodationId(Long accommodationId);
    boolean existsByAccommodationId(Long accommodationId);

}

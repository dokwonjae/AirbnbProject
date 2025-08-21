package com.example.airbnbproject.repository;

import com.example.airbnbproject.domain.Accommodation;
import com.example.airbnbproject.domain.AccommodationStatus;
import com.example.airbnbproject.dto.MyAccommodationRowResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
    List<Accommodation> findAllByUserId(Long userId);

    List<Accommodation> findByStatus(AccommodationStatus status);

    @Query("""
            select new com.example.airbnbproject.dto.MyAccommodationRowResponseDto(
              a.id, a.name, a.price, a.view, a.status
            )
            from Accommodation a
            where a.user.id = :userId
            order by a.id desc
           """)
    List<MyAccommodationRowResponseDto> findMyAccommodationRows(Long userId);
}

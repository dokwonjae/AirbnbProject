package com.example.airbnbproject.repository;

import com.example.airbnbproject.domain.Accommodation;
import com.example.airbnbproject.domain.AccommodationStatus;
import com.example.airbnbproject.dto.AdminAccommodationRowDto;
import com.example.airbnbproject.dto.MyAccommodationRowResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
    List<Accommodation> findAllByUserId(Long userId);

    List<Accommodation> findByStatus(AccommodationStatus status);

    @Query("""
             select new com.example.airbnbproject.dto.MyAccommodationRowResponseDto(
               a.id, a.name, a.price, a.status
             )
             from Accommodation a
             where a.user.id = :userId
             order by a.id asc
            """)
    List<MyAccommodationRowResponseDto> findMyAccommodationRows(Long userId);

    @Query("""
              select a from Accommodation a
              join AccommodationInfo i on i.accommodation = a
              where a.status = :status
                and concat('|', lower(i.tags), '|') like concat('%|', lower(:tag), '|%')
            """)
    List<Accommodation> findApprovedByTag(@Param("tag") String tag,
                                          @Param("status") AccommodationStatus status);

    @Query("""
                select new com.example.airbnbproject.dto.AdminAccommodationRowDto(
                    a.id, a.name, u.loginId, a.status
                )
                from Accommodation a
                join a.user u
                order by a.id asc
            """)
    List<AdminAccommodationRowDto> findAdminAccommodationRows();

}

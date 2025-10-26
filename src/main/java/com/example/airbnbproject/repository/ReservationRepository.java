package com.example.airbnbproject.repository;

import com.example.airbnbproject.domain.Reservation;
import com.example.airbnbproject.domain.ReservationStatus;
import com.example.airbnbproject.domain.User;
import com.example.airbnbproject.dto.MyReservationRowResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByIdAndUser(Long id, User user);

    List<Reservation> findByUser(User user);

    @Query("""
                select count(r) > 0 from Reservation r
                where r.accommodation.id = :accommodationId
                  and r.status in :validStatuses
                  and r.checkOut > :checkIn
                  and r.checkIn  < :checkOut
            """)
    boolean existsOverlapping(@Param("accommodationId") Long accommodationId,
                              @Param("checkIn") LocalDate checkIn,
                              @Param("checkOut") LocalDate checkOut,
                              @Param("validStatuses") List<ReservationStatus> validStatuses);

    @Query("""
              select r from Reservation r
              where r.accommodation.id = :accommodationId
                and r.status in :validStatuses
                and r.checkOut > :from
                and r.checkIn  < :to
            """)
    List<Reservation> findOverlapping(@Param("accommodationId") Long accommodationId,
                                      @Param("from") LocalDate from,
                                      @Param("to") LocalDate to,
                                      @Param("validStatuses") List<ReservationStatus> validStatuses);

    @Query("""
             select new com.example.airbnbproject.dto.MyReservationRowResponseDto(
                r.id, a.name, r.checkIn, r.checkOut, r.status
             )
             from Reservation r
             join r.accommodation a
             where r.user = :user
             order by r.checkIn desc
            """)
    List<MyReservationRowResponseDto> findMyReservationRows(@Param("user") User user);

    @Query("""
        select new com.example.airbnbproject.dto.MyReservationRowResponseDto(
            r.id,
            a.name,
            r.checkIn,
            r.checkOut,
            r.status
        )
        from Reservation r
        join r.accommodation a
        where r.user.id = :userId
        order by r.checkIn desc
    """)
    List<MyReservationRowResponseDto> findMyReservationRowsByUserId(@Param("userId") Long userId);
}

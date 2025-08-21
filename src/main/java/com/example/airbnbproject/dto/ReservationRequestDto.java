package com.example.airbnbproject.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
public class ReservationRequestDto {

    @NotNull
    private Long accommodationId;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @FutureOrPresent
    private LocalDate checkIn;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Future
    private LocalDate checkOut;

    @NotNull(message = "인원수를 입력해주세요.")
    @Min(1)
    private Integer guestCount;

    @AssertTrue(message = "체크아웃은 체크인 이후여야 합니다.")
    public boolean isDateRangeValid() {
        if (checkIn == null || checkOut == null) return true;
        return checkOut.isAfter(checkIn);
    }
}

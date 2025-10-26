package com.example.airbnbproject.dto;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class KakaoPayRequestDto {

    @NotNull
    private Long reservationId;

    @Min(1)
    private int amount;
}


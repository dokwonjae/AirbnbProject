package com.example.airbnbproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPayApproveFormDto {

    @NotBlank
    private String pgToken;

    @NotBlank
    private String reservationId;

    public static KakaoPayApproveFormDto of(String pgToken, String reservationId) {
        return new KakaoPayApproveFormDto(pgToken, reservationId);
    }
}

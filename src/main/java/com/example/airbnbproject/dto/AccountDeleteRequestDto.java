package com.example.airbnbproject.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter @Setter
public class AccountDeleteRequestDto {
    @NotBlank
    @Size(min = 6, max = 64) // 사이트 정책에 맞춰 조정
    private String currentPassword;
}

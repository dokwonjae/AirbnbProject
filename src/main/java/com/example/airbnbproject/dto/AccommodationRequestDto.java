package com.example.airbnbproject.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationRequestDto {

    @NotBlank(message = "숙소 이름을 입력해주세요.")
    private String name;

    @Min(value = 1, message = "가격은 1 이상이어야 합니다.")
    private int price;

    @NotBlank(message = "전망 정보를 입력해주세요.")
    private String view;

    private String image;
}

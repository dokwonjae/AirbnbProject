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

    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private int price;

    private String view;

    private String image;
}

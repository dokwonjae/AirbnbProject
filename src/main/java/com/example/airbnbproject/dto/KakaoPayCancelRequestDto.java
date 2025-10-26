package com.example.airbnbproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoPayCancelRequestDto {
    private String cid;
    private String tid;
    private Integer cancel_amount;
    private Integer cancel_tax_free_amount;
}

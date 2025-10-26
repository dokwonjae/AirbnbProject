package com.example.airbnbproject.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoPayCancelResponseDto {

    @JsonProperty("tid")
    private String tid;

    @JsonProperty("canceled_amount")
    private CanceledAmount canceledAmount;

    @JsonProperty("status")
    private String status; // 써도 되고 안 써도 됨

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CanceledAmount {
        @JsonProperty("total")
        private Integer total; // 꼭 필요한 것만
    }
}

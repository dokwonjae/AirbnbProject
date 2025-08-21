package com.example.airbnbproject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPayApproveRequestDto {

        @JsonProperty("cid")
        private String cid;

        @JsonProperty("tid")
        private String tid;

        @JsonProperty("partner_order_id")
        private String partnerOrderId;

        @JsonProperty("partner_user_id")
        private String partnerUserId;

        @JsonProperty("pg_token")
        private String pgToken;
}

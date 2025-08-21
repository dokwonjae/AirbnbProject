package com.example.airbnbproject.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoPayReadyResponseDto {
    @JsonProperty("tid")
    private String tid;

    @JsonProperty("next_redirect_pc_url")
    private String nextRedirectPcUrl;
}

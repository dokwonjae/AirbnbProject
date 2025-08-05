package com.example.airbnbproject.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.kakaopay")  // ✅ 이걸로 수정해야 함!
public class KakaoPayConfig {
    private String host;
    private String secretKey;
    private String cid;
    private String approvalUrl;
    private String cancelUrl;
    private String failUrl;
}

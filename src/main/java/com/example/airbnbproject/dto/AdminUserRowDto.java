package com.example.airbnbproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
public class AdminUserRowDto {
    private final Long id;
    private final String loginId;
    private final String email;
    private final LocalDateTime createdAt;
    public String getCreatedAtText() {
        if (createdAt == null) return "-";
        return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

}

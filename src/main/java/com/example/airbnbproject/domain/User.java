package com.example.airbnbproject.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor

public class User {

    @Id // 기본 키 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 사용자 고유 ID

    @Column(nullable = false, length = 20, unique = true)
    private String loginId; // 로그인용 아이디

    @Column(nullable = false, length = 255)
    private String password; // 암호화된 비밀번호

    @Column(nullable = false, length = 20)
    private String tel; // 전화번호

    @Column(nullable = false, length = 254, unique = true)
    private String email; // 이메일

    @Column(nullable = false, length = 255)
    private String salt; // 비밀번호 암호화용 솔트

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role; // 권한

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
    @PrePersist @PreUpdate
    private void normalizeFields() {
        if (email != null) email = email.trim().toLowerCase(java.util.Locale.ROOT);
        if (tel   != null) tel   = tel.trim();
    }

}


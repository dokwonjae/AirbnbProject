package com.example.airbnbproject.repository;

import com.example.airbnbproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId); // 로그인 ID로 유저를 조회하는 메서드 (중복 체크나 로그인 용도)
}

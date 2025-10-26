package com.example.airbnbproject.repository;

import com.example.airbnbproject.domain.User;
import com.example.airbnbproject.dto.AdminUserRowDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);
    Optional<User> findByEmail(String loginId);
    boolean existsByEmailAndIdNot(String email, Long id);

    @Query("""
           select new com.example.airbnbproject.dto.AdminUserRowDto(
               u.id, u.loginId, u.email, u.createdAt
           )
           from User u
           order by u.createdAt asc
           """)
    List<AdminUserRowDto> findAdminUserRows();
}

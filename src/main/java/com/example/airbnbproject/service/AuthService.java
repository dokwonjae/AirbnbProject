package com.example.airbnbproject.service;

import com.example.airbnbproject.domain.User;
import com.example.airbnbproject.domain.UserRole;
import com.example.airbnbproject.dto.UserJoinRequestDto;
import com.example.airbnbproject.repository.UserRepository;
import com.example.airbnbproject.util.SHA256;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    @Transactional
    public void register(UserJoinRequestDto dto) {

        if (dto.getPassword() == null || !dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        if (userRepository.findByLoginId(dto.getLoginId()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        String normalizedEmail = dto.getEmail() == null
                ? null
                : dto.getEmail().trim().toLowerCase(java.util.Locale.ROOT);

        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 패스워드에 대한 salt 생성
        String salt = SHA256.createSalt();
        // 패스워드와 salt를 이용하여 SHA-256으로 암호화
        String encryptedPassword = SHA256.encrypt(dto.getPassword(), salt);

        // JoinRequestDto → User 엔티티 변환
        User user = new User();
        user.setLoginId(dto.getLoginId());
        user.setPassword(encryptedPassword);
        user.setSalt(salt);
        user.setEmail(normalizedEmail);
        user.setTel(dto.getTel().trim());
        user.setRole(UserRole.USER); // enum으로 일반 사용자 권한 설정

        // DB에 저장
        userRepository.save(user);
    }

    public User login(String loginId, String password) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        String encryptedInput = SHA256.encrypt(password, user.getSalt());
        if (!user.getPassword().equals(encryptedInput)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }
}

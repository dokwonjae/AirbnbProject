package com.example.airbnbproject.service;

import com.example.airbnbproject.domain.User;
import com.example.airbnbproject.domain.UserRole;
import com.example.airbnbproject.dto.JoinRequestDto;
import com.example.airbnbproject.repository.UserRepository;
import com.example.airbnbproject.util.SHA256;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service // 이 클래스가 서비스 레이어 컴포넌트임을 명시 (비즈니스 로직 담당)
@RequiredArgsConstructor // final 필드를 자동으로 생성자 주입 (userRepository 주입됨)
public class UserService {

    private final UserRepository userRepository; // 데이터베이스에 접근하는 레포지토리 의존성 주입
    @Transactional // 에러 없으면 commit, 예외 터지면 rollback
    public void register(JoinRequestDto dto) {
        // loginId 중복 검사
        if (userRepository.findByLoginId(dto.getLoginId()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
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
        user.setEmail(dto.getEmail());
        user.setTel(dto.getTel());
        user.setRole(UserRole.USER); // enum으로 일반 사용자 권한 설정

        // DB에 저장
        userRepository.save(user);
    }
}

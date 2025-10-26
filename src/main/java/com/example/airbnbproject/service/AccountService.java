package com.example.airbnbproject.service;

import com.example.airbnbproject.domain.User;
import com.example.airbnbproject.dto.UserContactUpdateRequestDto;
import com.example.airbnbproject.dto.UserPasswordChangeRequestDto;
import com.example.airbnbproject.repository.UserRepository;
import com.example.airbnbproject.util.SHA256;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));
    }

    @Transactional
    public void updateContact(Long userId, UserContactUpdateRequestDto dto) {
        User user = getById(userId);

        String normalizedEmail = dto.getEmail() == null
                ? null
                : dto.getEmail().trim().toLowerCase(java.util.Locale.ROOT);

        // 본인 제외 중복 이메일 방지
        if (userRepository.existsByEmailAndIdNot(normalizedEmail, userId)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        user.setTel(dto.getTel());
        user.setEmail(normalizedEmail);

    }

    @Transactional
    public void changePassword(Long userId, UserPasswordChangeRequestDto dto) {
        User user = getById(userId);

        String currentEncrypted = SHA256.encrypt(dto.getCurrentPassword(), user.getSalt());
        if (!currentEncrypted.equals(user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // ✅ 서비스 레벨에서도 동일 비번 방지(안전망)
        if (SHA256.encrypt(dto.getNewPassword(), user.getSalt()).equals(user.getPassword())) {
            throw new IllegalArgumentException("새 비밀번호가 기존 비밀번호와 동일합니다.");
        }

        // 새 해시 생성
        String newSalt = SHA256.createSalt();
        String newHashed = SHA256.encrypt(dto.getNewPassword(), newSalt);

        user.setSalt(newSalt);
        user.setPassword(newHashed);
    }


    @Transactional
    public void deleteAccount(Long userId, String currentPasswordPlain) {
        User user = getById(userId);

        // 1) 현재 비밀번호 확인
        String encryptedInput = SHA256.encrypt(currentPasswordPlain, user.getSalt());
        if (!encryptedInput.equals(user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 2) 익명화(20자 이내 loginId 보장)
        anonymize(user);
    }

    /** 내부 헬퍼: loginId 20자 이하/유니크 보장, 이메일 익명화, 비번 무력화 */
    private void anonymize(User user) {
        String id36 = Long.toString(user.getId(), 36);
        String ts36 = Long.toString(System.currentTimeMillis(), 36);

        String login = "del_" + id36 + "_" + ts36;               // 예: del_2s_kr4i8p
        login = login.length() <= 20 ? login : login.substring(0, 20);

        // 혹시 모를 유니크 충돌 방지(거의 없지만 안전하게)
        while (userRepository.findByLoginId(login).isPresent()) {
            ts36 = Long.toString(System.nanoTime(), 36);
            String tmp = "del_" + id36 + "_" + ts36;
            login = tmp.length() <= 20 ? tmp : tmp.substring(0, 20);
        }

        String newEmail = ("del+" + id36 + "." + ts36 + "@deleted.local").toLowerCase(Locale.ROOT);

        user.setLoginId(login);     // ★ VARCHAR(20) 안전
        user.setEmail(newEmail);
        user.setTel("");

        String newSalt = SHA256.createSalt();
        String newHashed = SHA256.encrypt("deleted-" + ts36, newSalt);
        user.setSalt(newSalt);
        user.setPassword(newHashed);
    }
}
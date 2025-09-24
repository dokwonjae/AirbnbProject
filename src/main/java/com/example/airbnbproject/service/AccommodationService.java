package com.example.airbnbproject.service;

import com.example.airbnbproject.domain.Accommodation;
import com.example.airbnbproject.domain.AccommodationStatus;
import com.example.airbnbproject.domain.User;
import com.example.airbnbproject.domain.UserRole;
import com.example.airbnbproject.dto.AccommodationRequestDto;
import com.example.airbnbproject.repository.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;

    @Transactional
    public Accommodation saveAccommodation(AccommodationRequestDto dto, User user) {
        Accommodation acc = Accommodation.of(dto, user);
        // 생성 직후 상태는 기존 도메인 규칙 그대로 사용(예: PENDING)
        return accommodationRepository.save(acc);
    }

    @Transactional
    public void updateAccommodation(Long id, AccommodationRequestDto dto, User user) {
        Accommodation ac = accommodationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("숙소가 존재하지 않습니다."));

        // 권한: 본인 또는 관리자만
        if (!isOwnerOrAdmin(ac, user)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        // ARCHIVED 는 편집 불가(기록 보존 상태)
        if (ac.getStatus() == AccommodationStatus.ARCHIVED) {
            throw new IllegalArgumentException("보존(ARCHIVED)된 숙소는 수정할 수 없습니다.");
        }

        // 필드 변경 — 트랜잭션 종료 시점에 dirty checking으로 UPDATE 반영
        ac.setName(dto.getName());
        ac.setPrice(dto.getPrice());
        ac.setImage(dto.getImage());
    }

    /**
     * 호스트가 "삭제 요청" 버튼을 눌렀을 때 상태만 변경.
     */
    @Transactional
    public void requestDelete(Long id, User user) {
        Accommodation ac = accommodationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("숙소가 존재하지 않습니다."));

        if (!isOwner(ac, user)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        if (ac.getStatus() == AccommodationStatus.ARCHIVED) {
            throw new IllegalArgumentException("이미 보존(ARCHIVED)된 숙소입니다.");
        }

        ac.setStatus(AccommodationStatus.DELETE_REQUESTED);
    }

    /**
     * (관리자 전용) 삭제 승인 시 — 물리 삭제 대신 ARCHIVED 로 전환.
     * 예약 이력의 유무와 상관없이 운영 일관성을 위해 '항상' ARCHIVED 로 통일.
     * 필요하면 정책에 맞게 분기(이력 없으면 물리삭제)로 바꿀 수 있으나, 현재 요구는 "물리삭제 금지".
     */
    @Transactional
    public void deleteApproved(Long id, User admin) {
        Accommodation ac = accommodationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("숙소가 존재하지 않습니다."));

        if (!isAdmin(admin)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        // 이미 ARCHIVED 인 경우 중복 처리 방지
        if (ac.getStatus() == AccommodationStatus.ARCHIVED) {
            return;
        }

        ac.setStatus(AccommodationStatus.ARCHIVED);
        // 물리 삭제는 절대 하지 않음.
    }

    @Transactional(readOnly = true)
    public Accommodation getForEdit(Long id, User user) {
        Accommodation ac = accommodationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("숙소가 존재하지 않습니다."));

        // 수정 폼 진입 권한: 소유자 또는 관리자
        if (!isOwnerOrAdmin(ac, user)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        // ARCHIVED 는 편집 폼 진입도 차단(읽기 전용이 필요하면 별도 뷰에서 처리)
        if (ac.getStatus() == AccommodationStatus.ARCHIVED && !isAdmin(user)) {
            throw new IllegalArgumentException("보존(ARCHIVED)된 숙소는 편집할 수 없습니다.");
        }

        return ac;
    }

    private boolean isOwnerOrAdmin(Accommodation ac, User user) {
        return isOwner(ac, user) || isAdmin(user);
    }

    private boolean isOwner(Accommodation ac, User user) {
        return user != null && ac.getUser().getId().equals(user.getId());
    }

    private boolean isAdmin(User user) {
        return user != null && user.getRole() == UserRole.ADMIN;
    }
}

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

        return accommodationRepository.save(acc);
    }

    @Transactional
    public void updateAccommodation(Long id, AccommodationRequestDto dto, User user) {
        Accommodation ac = accommodationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("숙소가 존재하지 않습니다."));

        // 권한: 본인 또는 관리자만
        if (!ac.getUser().getId().equals(user.getId()) && user.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        // 필드 변경 — 트랜잭션 종료 시점에 dirty checking으로 UPDATE 반영
        ac.setName(dto.getName());
        ac.setPrice(dto.getPrice());
        ac.setView(dto.getView());
        ac.setImage(dto.getImage());
        // accommodationRepository.save(ac); // <- 굳이 호출 안 해도 됨(영속 상태면 dirty checking)
    }

    @Transactional
    public void requestDelete(Long id, User user) {
        Accommodation ac = accommodationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("숙소가 존재하지 않습니다."));

        if (!ac.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        ac.setStatus(AccommodationStatus.DELETE_REQUESTED);
    }

    @Transactional
    public void deleteApproved(Long id, User user) {
        Accommodation ac = accommodationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("숙소가 존재하지 않습니다."));

        if (user == null || user.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        accommodationRepository.delete(ac);
    }
    @Transactional(readOnly = true)
    public Accommodation getForEdit(Long id, User user) {
        Accommodation ac = accommodationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("숙소가 존재하지 않습니다."));
        // 수정 폼 진입 권한: 소유자 또는 관리자
        if (!ac.getUser().getId().equals(user.getId()) && user.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        return ac;
    }

}

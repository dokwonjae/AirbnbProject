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
    public void requestDelete(Long id, User user) {
        Accommodation ac = accommodationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("숙소가 존재하지 않습니다."));

        if (!ac.getUser().getId().equals(user.getId()) && user.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        ac.setStatus(AccommodationStatus.DELETE_REQUESTED);
    }

    @Transactional
    public void deleteApproved(Long id) {
        Accommodation ac = accommodationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("숙소가 존재하지 않습니다."));

        accommodationRepository.delete(ac);
    }
}

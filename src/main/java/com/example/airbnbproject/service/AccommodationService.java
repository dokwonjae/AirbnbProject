package com.example.airbnbproject.service;

import com.example.airbnbproject.domain.Accommodation;
import com.example.airbnbproject.domain.User;
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
}

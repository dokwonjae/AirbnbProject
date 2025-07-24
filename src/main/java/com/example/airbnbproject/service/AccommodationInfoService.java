package com.example.airbnbproject.service;

import com.example.airbnbproject.domain.Accommodation;
import com.example.airbnbproject.domain.AccommodationInfo;
import com.example.airbnbproject.domain.AccommodationInfoImage;
import com.example.airbnbproject.dto.AccommodationInfoRequestDto;
import com.example.airbnbproject.repository.AccommodationInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationInfoService {

    private final AccommodationInfoRepository accommodationInfoRepository;

    public void saveAccommodationInfo(AccommodationInfoRequestDto dto, Accommodation accommodation) throws IOException {
        if (accommodation == null) {
            throw new IllegalArgumentException("연결할 숙소 정보가 없습니다.");
        }

        if (dto.getImages() == null || dto.getImages().isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 존재하지 않습니다.");
        }

        if (dto.getImages().size() > 5) {
            throw new IllegalArgumentException("최대 5개의 이미지만 업로드할 수 있습니다.");
        }

        AccommodationInfo info = new AccommodationInfo();
        info.setTitle(dto.getTitle());
        info.setLocation(dto.getLocation());
        info.setSubTitle(dto.getSubTitle());
        info.setPersonnel(dto.getPersonnel());
        info.setAmenities(dto.getAmenities());
        info.setAccommodation(accommodation);

        // 이미지 변환 및 추가
        List<AccommodationInfoImage> imageList = new ArrayList<>();
        int priority = 1;

        for (MultipartFile imageFile : dto.getImages()) {
            if (!imageFile.isEmpty()) {
                AccommodationInfoImage image = new AccommodationInfoImage();
                image.setImageData(imageFile.getBytes());
                image.setImageUrl(null);
                image.setPriority(priority++);
                image.setAccommodationInfo(info); // 양방향 연결
                imageList.add(image);
            }
        }

        info.setImages(imageList); // 리스트 설정

        accommodationInfoRepository.save(info); // cascade = ALL로 이미지도 같이 저장됨
    }

    public boolean existsByAccommodationId(Long accommodationId) {
        return accommodationInfoRepository.existsByAccommodationId(accommodationId);
    }

    public AccommodationInfo findById(Long id) {
        return accommodationInfoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상세정보 없음"));
    }

    public void update(Long infoId, AccommodationInfoRequestDto dto) throws IOException {
        AccommodationInfo info = findById(infoId);
        info.setTitle(dto.getTitle());
        info.setLocation(dto.getLocation());
        info.setSubTitle(dto.getSubTitle());
        info.setPersonnel(dto.getPersonnel());
        info.setAmenities(dto.getAmenities());

        // 이미지 초기화 후 재등록
        info.getImages().clear();
        List<AccommodationInfoImage> imageList = new ArrayList<>();
        int priority = 1;
        for (MultipartFile imageFile : dto.getImages()) {
            if (!imageFile.isEmpty()) {
                AccommodationInfoImage image = new AccommodationInfoImage();
                image.setImageData(imageFile.getBytes());
                image.setPriority(priority++);
                image.setAccommodationInfo(info);
                imageList.add(image);
            }
        }
        info.getImages().addAll(imageList);
        accommodationInfoRepository.save(info);
    }

    public Long delete(Long infoId) {
        AccommodationInfo info = findById(infoId);
        Long acId = info.getAccommodation().getId();
        accommodationInfoRepository.delete(info);
        return acId;
    }

    public AccommodationInfoRequestDto toDto(AccommodationInfo info) {
        AccommodationInfoRequestDto dto = new AccommodationInfoRequestDto();
        dto.setTitle(info.getTitle());
        dto.setLocation(info.getLocation());
        dto.setSubTitle(info.getSubTitle());
        dto.setPersonnel(info.getPersonnel());
        dto.setAmenities(info.getAmenities());
        return dto;
    }

}

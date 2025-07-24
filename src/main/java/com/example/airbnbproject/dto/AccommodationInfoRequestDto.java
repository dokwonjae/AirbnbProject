package com.example.airbnbproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationInfoRequestDto {
    @NotBlank(message = "숙소 타이틀은 필수입니다.")
    @Size(max = 50, message = "숙소 타이틀은 50자 이하로 작성해주세요.")
    private String title;

    @NotBlank(message = "숙소 위치는 필수입니다.")
    @Size(max = 50, message = "숙소 위치는 50자 이하로 작성해주세요.")
    private String location;

    @Size(max = 100, message = "부제목은 100자 이하로 작성해주세요.")
    private String subTitle;

    @NotBlank(message = "숙박 인원은 필수입니다.")
    private String personnel;

    @Size(max = 255, message = "편의시설 설명은 255자 이하로 작성해주세요.")
    private String amenities;

    @Size(max = 5, message = "최대 5장의 이미지만 업로드할 수 있습니다.")
    private List<MultipartFile> images;
}

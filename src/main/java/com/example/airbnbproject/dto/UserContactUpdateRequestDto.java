package com.example.airbnbproject.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserContactUpdateRequestDto {
    @NotBlank
    @Size(max=20)
    @Pattern(regexp="^\\d{2,3}-?\\d{3,4}-?\\d{4}$", message="전화번호 형식이 올바르지 않습니다.")
    private String tel;

    @NotBlank
    @Email
    @Size(max=254)
    private String email;
}

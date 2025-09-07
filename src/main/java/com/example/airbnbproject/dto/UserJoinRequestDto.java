package com.example.airbnbproject.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserJoinRequestDto {
    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 4, max = 20, message = "아이디는 4~20자 사이여야 합니다.")
    private String loginId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 6, max = 30, message = "비밀번호는 6~30자 사이여야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    @Size(min = 6, max = 30, message = "비밀번호 확인은 6~30자 사이여야 합니다.")
    private String confirmPassword;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "전화번호를 입력해주세요.")
    @Size(min = 10, max = 15, message = "전화번호는 10~15자 사이여야 합니다.")
    private String tel;
}

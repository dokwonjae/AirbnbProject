package com.example.airbnbproject.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserPasswordChangeRequestDto {

    @NotBlank
    private String currentPassword;

    @NotBlank
    @Size(min = 6, max = 64)
    private String newPassword;

    @NotBlank
    private String newPasswordConfirm;

    @AssertTrue(message = "새 비밀번호 확인이 일치하지 않습니다.")
    public boolean isConfirmMatches() {
        if (newPassword == null || newPasswordConfirm == null) return true;
        return newPassword.equals(newPasswordConfirm);
    }

    @AssertTrue(message = "새 비밀번호는 기존 비밀번호와 달라야 합니다.")
    public boolean isDifferentFromCurrent() {
        if (currentPassword == null || newPassword == null) return true;
        return !newPassword.equals(currentPassword);
    }
}
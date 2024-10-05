package com.example.ChoiGangDeliveryApp.user.dto;

import com.example.ChoiGangDeliveryApp.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {

    @NotBlank(message = "not blank")
    @Pattern(regexp = "^[a-zA-Z]{8,12}$", message = "유저네임은 영어로 8글자 이상, 12글자 이하여야 합니다.")
    private String username;

    @NotBlank(message = "not blank")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()-_+=]{8,15}$",
            message = "비밀번호는 영어와 숫자가 포함된 8자리 이상, 15자리 이하의 문자열이어야 합니다.")
    private String password;

    @NotBlank(message = "not blank")
    private String passCheck;
    @NotBlank(message = "not blank")
    private String phone;
    @NotBlank(message = "not blank")
    private String email;
    @NotBlank(message = "not blank")
    private UserRole role;


}

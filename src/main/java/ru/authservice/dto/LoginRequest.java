package ru.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LoginRequest {
    @NotBlank
    private String login;

    @NotBlank
    private String password;
}

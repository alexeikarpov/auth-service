package ru.authservice.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserRequest {
    @NotBlank
    private String login;

    @NotBlank
    private String password;

    @NotBlank
    @Email
    private String email;
}

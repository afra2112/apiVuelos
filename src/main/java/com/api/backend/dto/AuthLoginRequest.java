package com.api.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequest(@NotBlank @Email String email,
                               @NotBlank String password) {
}
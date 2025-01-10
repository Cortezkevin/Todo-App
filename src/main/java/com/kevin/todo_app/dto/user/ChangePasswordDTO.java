package com.kevin.todo_app.dto.user;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordDTO(
        @NotBlank( message = "Required") String tokenPassword,
        @NotBlank( message = "Required") String password,
        @NotBlank( message = "Required") String confirmPassword
) {
}

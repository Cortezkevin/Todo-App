package com.kevin.todo_app.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserDTO(
        @NotBlank(message="Required") @Email(message="Not valid") String email,
        @NotBlank(message="Required") String password,
        @NotBlank(message="Required") String confirmPassword,
        Boolean isAdmin
) {
}

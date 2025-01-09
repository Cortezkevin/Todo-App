package com.kevin.todo_app.dto.user;

public record CreateUserDTO(
        String email,
        String password,
        String confirmPassword,
        Boolean isAdmin
) {
}

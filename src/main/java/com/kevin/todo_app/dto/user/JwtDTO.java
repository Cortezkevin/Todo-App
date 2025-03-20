package com.kevin.todo_app.dto.user;

public record JwtDTO (
        String token,
        UserDTO user
) {
}

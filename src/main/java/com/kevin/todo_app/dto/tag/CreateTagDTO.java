package com.kevin.todo_app.dto.tag;

import jakarta.validation.constraints.NotBlank;

public record CreateTagDTO(
        @NotBlank(message="Required") String name
) {
}

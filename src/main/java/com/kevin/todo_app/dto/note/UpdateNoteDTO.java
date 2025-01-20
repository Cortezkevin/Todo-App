package com.kevin.todo_app.dto.note;

import com.kevin.todo_app.documents.note.Element;
import com.kevin.todo_app.enums.Color;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateNoteDTO(
        @NotNull(message="Required") String id,
        @NotBlank(message="Required") String title,
        @NotNull(message="Required or Empty Array") List<Element> content,
        @NotNull(message="Required or Empty Array") List<String> tags,
        @NotNull(message="Required") String color
) {
}

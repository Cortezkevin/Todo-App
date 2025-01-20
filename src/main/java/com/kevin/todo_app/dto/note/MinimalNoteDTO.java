package com.kevin.todo_app.dto.note;

import com.kevin.todo_app.documents.note.Note;
import com.kevin.todo_app.enums.Color;

import java.time.LocalDateTime;
import java.util.List;

public record MinimalNoteDTO(
        String id,
        String title,
        String color,
        List<String> tags,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean favorite,
        boolean fixed
) {
    public static MinimalNoteDTO toDTO(Note note){
        return new MinimalNoteDTO(
                note.getId(),
                note.getTitle(),
                note.getColor(),
                note.getTags(),
                note.getCreatedAt(),
                note.getUpdatedAt(),
                note.isFavorite(),
                note.isFixed()
        );
    }
}

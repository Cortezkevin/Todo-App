package com.kevin.todo_app.dto.note;

import com.kevin.todo_app.documents.note.Element;
import com.kevin.todo_app.documents.note.Note;

import java.time.LocalDateTime;
import java.util.List;

public record DetailedNoteDTO(
    String id,
    String title,
    List<Element> content,
    String color,
    List<String> tags,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    boolean deleted,
    boolean favorite,
    boolean fixed
) {
    public static DetailedNoteDTO toDTO(Note note){
        return new DetailedNoteDTO(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getColor(),
                note.getTags(),
                note.getCreatedAt(),
                note.getUpdatedAt(),
                note.isDeleted(),
                note.isFavorite(),
                note.isFixed()
        );
    }
}

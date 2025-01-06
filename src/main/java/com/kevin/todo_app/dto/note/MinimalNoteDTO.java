package com.kevin.todo_app.dto.note;

import com.kevin.todo_app.documents.Note;
import com.kevin.todo_app.enums.Color;

import java.util.List;

public record MinimalNoteDTO(
        String id,
        String title,
        Color color,
        List<String> tags
) {
    public static MinimalNoteDTO toDTO(Note note){
        return new MinimalNoteDTO(note.getId(), note.getTitle(), note.getColor(), note.getTags());
    }
}

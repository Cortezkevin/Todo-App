package com.kevin.todo_app.dto.note;

import com.kevin.todo_app.documents.Note;

public record MinimalNoteDTO(
        String id,
        String title
) {
    public static MinimalNoteDTO toDTO(Note note){
        return new MinimalNoteDTO(note.getId(), note.getTitle());
    }
}

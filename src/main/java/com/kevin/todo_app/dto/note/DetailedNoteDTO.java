package com.kevin.todo_app.dto.note;

import com.kevin.todo_app.documents.Element;
import com.kevin.todo_app.documents.Note;

import java.util.List;

public record DetailedNoteDTO(
    String id,
    String title,
    List<Element> content
) {
    public static DetailedNoteDTO toDTO(Note note){
        return new DetailedNoteDTO(note.getId(), note.getTitle(), note.getContent());
    }
}

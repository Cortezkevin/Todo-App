package com.kevin.todo_app.dto.note;

import com.kevin.todo_app.documents.Element;
import com.kevin.todo_app.documents.Note;
import com.kevin.todo_app.enums.Color;

import java.util.List;

public record DetailedNoteDTO(
    String id,
    String title,
    List<Element> content,
    Color color,
    List<String> tags
) {
    public static DetailedNoteDTO toDTO(Note note){
        return new DetailedNoteDTO(note.getId(), note.getTitle(), note.getContent(), note.getColor(), note.getTags());
    }
}

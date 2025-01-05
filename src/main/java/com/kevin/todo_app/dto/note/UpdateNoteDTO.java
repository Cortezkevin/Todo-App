package com.kevin.todo_app.dto.note;

import com.kevin.todo_app.documents.Element;

import java.util.List;

public record UpdateNoteDTO(
        String id,
        String title,
        List<Element> content
) {
}

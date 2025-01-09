package com.kevin.todo_app.dto.note;

import com.kevin.todo_app.documents.Element;
import com.kevin.todo_app.enums.Color;

import java.util.List;

public record UpdateNoteDTO(
        String id,
        String title,
        List<Element> content,
        List<String> tags,
        Color color,
        String user
) {
}

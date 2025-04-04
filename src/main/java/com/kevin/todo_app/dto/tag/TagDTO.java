package com.kevin.todo_app.dto.tag;

import com.kevin.todo_app.documents.tag.Tag;

public record TagDTO (
    String name,
    String color
){
    public static TagDTO toDTO(Tag tag){
        return new TagDTO(tag.getName(), tag.getColor());
    }
}

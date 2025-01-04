package com.kevin.todo_app.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Paragraph implements Element{
    private final static String ELEMENT_TYPE = "text";

    private String text;

    @Override
    public String getType() {
        return ELEMENT_TYPE;
    }
}

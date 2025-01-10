package com.kevin.todo_app.documents.note;

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

    private Integer index;
    private String text;
    private Boolean delete = false;

    @Override
    public String getType() {
        return ELEMENT_TYPE;
    }

    @Override
    public Integer getIndex() {
        return index;
    }

    @Override
    public Boolean isDelete() {
        return delete;
    }
}

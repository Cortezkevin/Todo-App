package com.kevin.todo_app.documents.note;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task implements Element {
    private final static String ELEMENT_TYPE = "task";

    private Integer index;
    private String description;
    private boolean completed = false;
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

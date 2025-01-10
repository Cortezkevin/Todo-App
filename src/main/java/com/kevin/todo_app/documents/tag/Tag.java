package com.kevin.todo_app.documents.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Data
@Document(collection = "tags")
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
    @Id
    private String id;
    private String name;
}

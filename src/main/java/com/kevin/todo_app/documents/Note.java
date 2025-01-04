package com.kevin.todo_app.documents;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@Document(collection = "notes")
@NoArgsConstructor
@AllArgsConstructor
public class Note {
    @Id
    private String id;
    private String title;
    private List<Element> content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

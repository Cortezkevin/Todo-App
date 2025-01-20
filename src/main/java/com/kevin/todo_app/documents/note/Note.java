package com.kevin.todo_app.documents.note;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    private String color = "default";
    private List<Element> content = new ArrayList<>();
    private List<String> tags = new ArrayList<>();

    private boolean deleted = false;
    private boolean fixed = false;

    private boolean favorite = false;

    private LocalDateTime fixedAt;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    private String user;
}
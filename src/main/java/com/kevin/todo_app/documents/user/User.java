package com.kevin.todo_app.documents.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kevin.todo_app.enums.RolName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Document(collection = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String id;
    private String email;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String tokenPassword;
    private List<RolName> roles = new ArrayList<>();
}

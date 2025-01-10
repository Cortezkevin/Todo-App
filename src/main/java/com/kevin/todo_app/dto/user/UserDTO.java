package com.kevin.todo_app.dto.user;

import com.kevin.todo_app.documents.user.User;
import com.kevin.todo_app.enums.RolName;

import java.util.List;

public record UserDTO(
        String id,
        String email,
        String password,
        List<String> roles
){
    public static UserDTO toDTO(User user){
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream().map(RolName::name).toList()
        );
    }
}

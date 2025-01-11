package com.kevin.todo_app.dto.response;

import org.springframework.http.HttpStatus;

public record ResponseDTO<T> (
        String message,
        HttpStatus status,
        T data
){
}

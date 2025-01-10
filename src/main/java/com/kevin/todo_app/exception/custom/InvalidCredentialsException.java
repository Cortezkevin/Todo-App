package com.kevin.todo_app.exception.custom;

public class InvalidCredentialsException extends Exception {
    public InvalidCredentialsException() {
        super("Invalid Credentials");
    }
}

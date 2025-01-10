package com.kevin.todo_app.exception;

public class PasswordsNotMatchException extends Exception{
    public PasswordsNotMatchException() {
        super("The passwords not matches");
    }
}

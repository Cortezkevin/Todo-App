package com.kevin.todo_app.exception.custom;

public class PasswordsNotMatchException extends Exception{
    public PasswordsNotMatchException() {
        super("The passwords not matches");
    }
}

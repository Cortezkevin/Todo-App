package com.kevin.todo_app.exception.custom;

public class NotInTheTrashException extends Exception {
    public NotInTheTrashException() {
        super("This note not belong in the trash");
    }
}

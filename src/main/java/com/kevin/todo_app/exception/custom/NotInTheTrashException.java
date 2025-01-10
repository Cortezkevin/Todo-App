package com.kevin.todo_app.exception.custom;

public class NotInTheTrashException extends Exception {
    public NotInTheTrashException() {
        super("La nota no se encuentra en la papelera");
    }
}

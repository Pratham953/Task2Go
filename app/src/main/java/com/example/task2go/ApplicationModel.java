package com.example.task2go;

public class ApplicationModel {
    private String userId, message;

    public ApplicationModel() {}

    public ApplicationModel(String userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public String getUserId() { return userId; }
    public String getMessage() { return message; }
}

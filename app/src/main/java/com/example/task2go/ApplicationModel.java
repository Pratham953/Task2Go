package com.example.task2go;

public class ApplicationModel {
    private String userId;  // User who applied
    private String message; // Message for application (optional)

    // Default constructor required for Firestore
    public ApplicationModel() { }

    public ApplicationModel(String userId) {
        this.userId = userId;
       // this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {  // ✅ Add this method
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

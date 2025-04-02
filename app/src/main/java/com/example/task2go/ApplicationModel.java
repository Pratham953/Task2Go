package com.example.task2go;

public class ApplicationModel {
    private String userId;
    private String taskId;
    private long timestamp;

    public ApplicationModel() {} // Required for Firebase

    public ApplicationModel(String userId, String taskId, long timestamp) {
        this.userId = userId;
        this.taskId = taskId;
        this.timestamp = timestamp;
    }

    public String getUserId() { return userId; }
    public String getTaskId() { return taskId; }
    public long getTimestamp() { return timestamp; }
}

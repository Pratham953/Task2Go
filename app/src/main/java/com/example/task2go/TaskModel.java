package com.example.task2go;

public class TaskModel {
    private String title;
    private String description;
    private String userId;
    private static String taskId;

    // Required empty constructor for Firestore
    public TaskModel() {}

    public TaskModel(String taskId, String title, String description, String userId) {
        TaskModel.taskId = taskId;
        this.title = title;
        this.description = description;
        this.userId = userId;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getUserId() { return userId; }
    public static String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { TaskModel.taskId = taskId; }
}

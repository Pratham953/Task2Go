package com.example.task2go;

public class TaskModel {
    private String taskId;
    private String title;
    private String description;
    private String userId;
    private String date;
    private String budget;

    // Required empty constructor for Firestore
    public TaskModel() {}

    public TaskModel(String taskId, String title, String description, String userId, String date, String budget) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.date = date;
        this.budget = budget;
    }

    // Getters
    public String getTaskId() { return taskId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getUserId() { return userId; }
    public String getDate() { return date; }
    public String getBudget() { return budget; }

    // Setter for taskId
    public void setTaskId(String taskId) { this.taskId = taskId; }
}

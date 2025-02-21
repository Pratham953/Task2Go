package com.example.task2go;

public class TaskModel {

        private String title, description, taskId;

        // Required empty constructor for Firestore
        public TaskModel() {}

        public TaskModel(String title, String description, String userId) {
            this.title = title;
            this.description = description;
            this.taskId = userId;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getTaskId() { return taskId; }
}

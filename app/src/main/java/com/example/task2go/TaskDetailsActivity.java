package com.example.task2go;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class TaskDetailsActivity extends AppCompatActivity {
    private static final String TAG = "TaskDetailsActivity";

    private TextView detailTitle, detailDescription, taskDeadline, taskLocation, taskPrice;
    private Button btnApplyTask;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String taskId, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details); // Your provided XML layout

        // Initialize Firestore and FirebaseAuth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        // Initialize UI elements
        detailTitle = findViewById(R.id.detailTitle);
        detailDescription = findViewById(R.id.detailDescription);
        taskDeadline = findViewById(R.id.taskDeadline);
        taskLocation = findViewById(R.id.taskLocation);
        taskPrice = findViewById(R.id.taskPrice);
        btnApplyTask = findViewById(R.id.btnApplyTask);

        // Get Task ID from Intent
        taskId = getIntent().getStringExtra("taskId");
        if (taskId == null) {
            Toast.makeText(this, "Task ID is missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load task details
        loadTaskDetails();

        // Apply for the task when button is clicked
        btnApplyTask.setOnClickListener(view -> applyForTask());
    }

    private void loadTaskDetails() {
        db.collection("tasks").document(taskId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String title = documentSnapshot.getString("title");
                        String description = documentSnapshot.getString("description");
                        String deadline = documentSnapshot.getString("date");  // Firestore field: date
                        String location = documentSnapshot.getString("location");
                        String price = documentSnapshot.getString("budget");  // Firestore field: budget

                        detailTitle.setText(title);
                        detailDescription.setText(description);
                        taskDeadline.setText("Deadline: " + deadline);
                        taskLocation.setText(location);
                        taskPrice.setText("₹" + price);
                    } else {
                        Toast.makeText(this, "Task not found!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching task details", e);
                    Toast.makeText(this, "Failed to load task details", Toast.LENGTH_SHORT).show();
                });
    }

    private void applyForTask() {
        if (userId == null) {
            Toast.makeText(this, "Please log in to apply for tasks.", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference taskRef = db.collection("tasks").document(taskId);

        // Move task to "acceptedTasks" collection under user's profile
        db.collection("users").document(userId).collection("acceptedTasks").document(taskId)
                .set(new TaskModel(taskId, detailTitle.getText().toString(), detailDescription.getText().toString(),
                        taskDeadline.getText().toString(), taskPrice.getText().toString(), taskLocation.getText().toString()))
                .addOnSuccessListener(aVoid -> {
                    // Remove task from the dashboard
                    taskRef.delete()
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(TaskDetailsActivity.this, "Task accepted!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> Log.e(TAG, "Error removing task from dashboard", e));
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error applying for task", e));
    }
}

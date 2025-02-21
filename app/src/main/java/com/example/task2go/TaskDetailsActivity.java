package com.example.task2go;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class TaskDetailsActivity extends AppCompatActivity {
    private String taskId, title, description;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextView titleText, descText;
    private Button btnApplyTask, btnViewApplications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Get UI Elements
        titleText = findViewById(R.id.detailTitle);
        descText = findViewById(R.id.detailDescription);
        btnApplyTask = findViewById(R.id.btnApplyTask);
        btnViewApplications = findViewById(R.id.btnViewApplications);

        // Retrieve Intent Data
        taskId = getIntent().getStringExtra("taskId");  // Correct usage
        title = getIntent().getStringExtra("title");
        description = getIntent().getStringExtra("description");

        // Check if Task ID is missing
        if (taskId == null || taskId.isEmpty()) {
            Toast.makeText(this, "TaskId is missing", Toast.LENGTH_LONG).show();
            finish(); // Close activity to prevent crashes
            return;
        }

        // Debugging: Log the received Task ID
        android.util.Log.d("TaskDetailsActivity", "Received Task ID: " + taskId);

        // Set task data to UI
        titleText.setText(title);
        descText.setText(description);

        // Check if the user is the owner of the task
        checkIfUserIsOwner();

        // Apply for task button click
        btnApplyTask.setOnClickListener(v -> applyForTask());

        // View applications button click
        btnViewApplications.setOnClickListener(v -> viewApplications());
    }

    private void checkIfUserIsOwner() {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (userId == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("tasks").document(taskId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String ownerId = documentSnapshot.getString("ownerId");
                        if (ownerId != null && ownerId.equals(userId)) {
                            btnApplyTask.setEnabled(false);
                            btnViewApplications.setEnabled(true);
                            Toast.makeText(this, "You are the owner of this task", Toast.LENGTH_SHORT).show();
                        } else {
                            btnApplyTask.setEnabled(true);
                            btnViewApplications.setEnabled(false);
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching task data", Toast.LENGTH_SHORT).show());
    }

    private void applyForTask() {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (userId == null) {
            Toast.makeText(this, "You need to log in to apply!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("tasks").document(taskId)
                .collection("applications").document(userId)
                .set(new ApplicationModel(userId))
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Applied Successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to apply", Toast.LENGTH_SHORT).show());
    }

    private void viewApplications() {
        Intent intent = new Intent(this, ApplicationListActivity.class);
        intent.putExtra("taskId", taskId);
        startActivity(intent);
    }
}

package com.example.task2go;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class TaskDetailsActivity extends AppCompatActivity {
    private TextView title, description;
    private Button btnApplyTask;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        title = findViewById(R.id.detailTitle);
        description = findViewById(R.id.detailDescription);
        btnApplyTask = findViewById(R.id.btnApplyTask);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Get Task Data from Intent
        taskId = getIntent().getStringExtra("taskId");
        String taskTitle = getIntent().getStringExtra("title");
        String taskDescription = getIntent().getStringExtra("description");

        if (taskId == null || taskId.isEmpty()) {
            Toast.makeText(this, "Error: Task not found", Toast.LENGTH_SHORT).show();
            finish();  // Close activity if no task ID is found
            return;
        }

        title.setText(taskTitle);
        description.setText(taskDescription);

        // Apply for Task when Button is Clicked
        btnApplyTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyForTask();
            }
        });

        Button btnViewApplications = findViewById(R.id.btnViewApplications);
        btnViewApplications.setOnClickListener(v -> {
            Intent intent = new Intent(TaskDetailsActivity.this, TaskApplicationsActivity.class);
            intent.putExtra("taskId", TaskModel.getTaskId());
            startActivity(intent);
        });

        checkIfUserApplied();

        Log.d("TaskDetails", "Task ID received: " + taskId);
    }

    private void applyForTask() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Please log in to apply", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        String taskId = getIntent().getStringExtra("TASK_ID");

        if (taskId == null) {
            Toast.makeText(this, "Error: Task not found", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference applicationRef = db.collection("TaskApplications").document(userId + "_" + taskId);

        applicationRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Toast.makeText(this, "You have already applied for this task!", Toast.LENGTH_SHORT).show();
                    } else {
                        saveApplicationToFirebase(userId, taskId);
                    }
                })
                .addOnFailureListener(e -> Log.e("FirebaseError", "Error: " + e.getMessage()));
    }


    private void saveApplicationToFirebase(String userId, String taskId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to "TaskApplications" collection
        DocumentReference applicationRef = db.collection("TaskApplications").document(userId + "_" + taskId);

        // Create application object
        Map<String, Object> applicationData = new HashMap<>();
        applicationData.put("userId", userId);
        applicationData.put("taskId", taskId);
        applicationData.put("timestamp", System.currentTimeMillis());

        applicationRef.set(applicationData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "You have successfully applied!", Toast.LENGTH_SHORT).show();
                    updateUIAfterApplying();  // Update button state
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to apply. Try again!", Toast.LENGTH_SHORT).show();
                    Log.e("FirebaseError", "Error: " + e.getMessage());
                });
    }

    private void updateUIAfterApplying() {
        Button applyButton = findViewById(R.id.btnApplyTask);
        applyButton.setText("Applied");
        applyButton.setEnabled(false); // Disable button
    }


    private void checkIfUserApplied() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) return;

        String userId = currentUser.getUid();
        String taskId = getIntent().getStringExtra("TASK_ID");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference applicationRef = db.collection("TaskApplications").document(userId + "_" + taskId);

        applicationRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        updateUIAfterApplying();  // User already applied, update UI
                    }
                });
    }





//    private void applyForTask() {
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        String message = "I am interested in this task!"; // Change this to user input later
//
//        Map<String, Object> application = new HashMap<>();
//        application.put("userId", userId);
//        application.put("message", message);
//        application.put("timestamp", System.currentTimeMillis());
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("tasks").document(taskId)
//                .collection("applications")
//                .add(application)
//                .addOnSuccessListener(docRef -> {
//                    Toast.makeText(this, "Application Sent!", Toast.LENGTH_SHORT).show();
//                    Log.d("Firestore", "Application added successfully!");
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    Log.e("Firestore", "Error adding application", e);
//                });
//    }

}

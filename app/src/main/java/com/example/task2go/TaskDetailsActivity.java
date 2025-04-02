package com.example.task2go;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.xml.KonfettiView;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class TaskDetailsActivity extends AppCompatActivity {

    private TextView detailTitle, detailDescription, taskDeadline, taskLocation, taskPrice;
    private MaterialButton btnApplyTask;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String taskId, userId;
    private KonfettiView konfettiView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        // Initialize Firestore & Auth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid(); // Get current user ID

        // Initialize UI components
        detailTitle = findViewById(R.id.detailTitle);
        detailDescription = findViewById(R.id.detailDescription);
        taskDeadline = findViewById(R.id.taskDeadline);
        taskLocation = findViewById(R.id.taskLocation);
        taskPrice = findViewById(R.id.taskPrice);
        btnApplyTask = findViewById(R.id.btnApplyTask);
        konfettiView = findViewById(R.id.konfettiView);

        // Get taskId from intent
        taskId = getIntent().getStringExtra("taskId");
        if (taskId == null) {
            showSnackbar("Task not found!", false);
            finish();
            return;
        }

        // Fetch task details from Firestore
        loadTaskDetails(taskId);

        // Apply button click listener
        btnApplyTask.setOnClickListener(v -> {
            applyTaskEffects(v);
            applyForTask();
        });
    }

    private void applyTaskEffects(View v) {
        // Button Animation (Shrink & Expand)
        v.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(150)
                .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(150));

        // Corrected Konfetti Effect
        EmitterConfig emitterConfig = new Emitter(2, TimeUnit.SECONDS).perSecond(300);
        konfettiView.start(
                new PartyFactory(emitterConfig)
                        .spread(360)
                        .position(0.5, 0.2, 0.5, 0.2)
                        .colors(Arrays.asList(Color.YELLOW, Color.GREEN, Color.BLUE)) // ✅ Fixed
                        .shapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                        .setSpeedBetween(10f, 30f)
                        .build()
        );

        // Vibrate for Feedback
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        }
    }

    private void applyForTask() {
        if (taskId == null) return;

        // Create applied task data
        TaskModel appliedTask = new TaskModel(taskId,
                detailTitle.getText().toString(),
                detailDescription.getText().toString(),
                userId,
                taskDeadline.getText().toString(),
                taskPrice.getText().toString());

        // Add task to "applied_tasks" collection
        db.collection("applied_tasks").document(taskId)
                .set(appliedTask)
                .addOnSuccessListener(aVoid -> {
                    removeTaskFromDashboard();
                    showSnackbar("Task Applied Successfully!", true);
                })
                .addOnFailureListener(e -> showSnackbar("Failed to apply!", false));
    }

    private void removeTaskFromDashboard() {
        // Delete task from "tasks" collection
        db.collection("tasks").document(taskId).delete()
                .addOnSuccessListener(aVoid -> finish()) // Close activity
                .addOnFailureListener(e -> showSnackbar("Failed to remove task", false));
    }

    private void loadTaskDetails(String taskId) {
        db.collection("tasks").document(taskId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Set task details
                        detailTitle.setText(documentSnapshot.getString("title"));
                        detailDescription.setText(documentSnapshot.getString("description"));
                        taskDeadline.setText("Deadline: " + documentSnapshot.getString("date"));
                        taskLocation.setText(documentSnapshot.getString("location"));
                        taskPrice.setText("₹" + documentSnapshot.getString("budget"));
                    } else {
                        showSnackbar("Task not found!", false);
                        finish();
                    }
                })
                .addOnFailureListener(e -> showSnackbar("Failed to load task", false));
    }

    private void showSnackbar(String message, boolean success) {
        Snackbar snackbar = Snackbar.make(btnApplyTask, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(this, success ? R.color.green : R.color.red));
        snackbar.show();
    }
}

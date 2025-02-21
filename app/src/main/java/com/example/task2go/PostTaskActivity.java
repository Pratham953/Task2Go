package com.example.task2go;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class PostTaskActivity extends AppCompatActivity {
    private EditText taskTitle, taskDescription;
    private Button btnPostTask;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_task);

        taskTitle = findViewById(R.id.taskTitle);
        taskDescription = findViewById(R.id.taskDescription);
        btnPostTask = findViewById(R.id.btnPostTask);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnPostTask.setOnClickListener(v -> postTask());
    }

    private void postTask() {
        String title = taskTitle.getText().toString();
        String description = taskDescription.getText().toString();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        Map<String, Object> task = new HashMap<>();
        task.put("title", title);
        task.put("description", description);
        task.put("userId", userId);

        db.collection("tasks").add(task)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Task Posted!", Toast.LENGTH_SHORT).show();
                    finish();  // Close the activity
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show());
    }
}

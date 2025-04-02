package com.example.task2go;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PostTaskActivity extends AppCompatActivity {

    private TextInputEditText edtTaskTitle, edtTaskDescription, edtTaskBudget, edtTaskDate, edtTaskLocation;
    private MaterialButton btnPostTask;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_task);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI Elements
        edtTaskTitle = findViewById(R.id.etTaskTitle);
        edtTaskDescription = findViewById(R.id.etTaskDescription);
        edtTaskBudget = findViewById(R.id.etBudget);
        edtTaskDate = findViewById(R.id.etDueDate);
        edtTaskLocation = findViewById(R.id.etLocation);
        btnPostTask = findViewById(R.id.btnCreateTask);
        calendar = Calendar.getInstance();

        // Initialize Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting Task...");
        progressDialog.setCancelable(false);

        // Open Date Picker when Date field is clicked
        edtTaskDate.setOnClickListener(v -> showDatePicker());

        // Button Click Listener
        btnPostTask.setOnClickListener(v -> postTaskToFirestore());
    }

    // Method to Show Date Picker
    private void showDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format the date as DD/MM/YYYY
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    edtTaskDate.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void postTaskToFirestore() {
        String title = edtTaskTitle.getText().toString().trim();
        String description = edtTaskDescription.getText().toString().trim();
        String budget = edtTaskBudget.getText().toString().trim();
        String date = edtTaskDate.getText().toString().trim();
        String location = edtTaskLocation.getText().toString().trim();

        // Validate Input Fields
        if (TextUtils.isEmpty(title)) {
            edtTaskTitle.setError("Title is required");
            return;
        }
        if (TextUtils.isEmpty(description)) {
            edtTaskDescription.setError("Description is required");
            return;
        }
        if (TextUtils.isEmpty(budget)) {
            edtTaskBudget.setError("Budget is required");
            return;
        }
        if (TextUtils.isEmpty(date)) {
            edtTaskDate.setError("Date is required");
            return;
        }
        if (TextUtils.isEmpty(location)) {
            edtTaskLocation.setError("Location is required");
            return;
        }

        // Show Progress Dialog
        progressDialog.show();

        // Task Data Map
        Map<String, Object> task = new HashMap<>();
        task.put("title", title);
        task.put("description", description);
        task.put("budget", budget);
        task.put("date", date);
        task.put("location", location);
        task.put("timestamp", System.currentTimeMillis());

        // Store in Firestore
        db.collection("tasks")
                .add(task)
                .addOnSuccessListener(documentReference -> {
                    progressDialog.dismiss();
                    Toast.makeText(PostTaskActivity.this, "Task Posted Successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(PostTaskActivity.this, "Failed to post task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

package com.example.task2go;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class TaskApplicationsActivity extends AppCompatActivity {
    private RecyclerView recyclerViewApplications;
    private ApplicationAdapter adapter;
    private List<ApplicationModel> applicationList;
    private FirebaseFirestore db;
    private String taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_applications);

        recyclerViewApplications = findViewById(R.id.recyclerViewApplications);
        recyclerViewApplications.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewApplications.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        applicationList = new ArrayList<>();
        adapter = new ApplicationAdapter(applicationList, this);
        recyclerViewApplications.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        taskId = getIntent().getStringExtra("taskId");

        if (taskId == null) {
            Toast.makeText(this, "Error: Task ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        loadApplications();
    }

    private void loadApplications() {
        db.collection("TaskApplications")
                .whereEqualTo("taskId", taskId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            ApplicationModel application = doc.toObject(ApplicationModel.class);
                            applicationList.add(application);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("Firebase", "Error fetching applications", task.getException());
                    }
                });
    }
}
